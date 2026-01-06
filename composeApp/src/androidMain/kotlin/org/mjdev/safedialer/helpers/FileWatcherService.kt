package org.mjdev.safedialer.helpers

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import kotlin.io.path.isDirectory
import kotlin.io.path.readBytes

@Suppress("RedundantSuspendModifier")
open class FileWatcherService(
    private val path: Path,
    private val recursively: Boolean = false,
    private val onFileCreated: (Path, ByteArray) -> Unit,
    private val onFileModified: (Path, ByteArray) -> Unit,
    private val onFileDeleted: (Path) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var watchJob: Job? = null
    private var watchService: WatchService? = null
    private val watchKeys = mutableMapOf<Path, WatchKey>()

    fun start() {
        watchJob = scope.launch {
            try {
                watchService = FileSystems.getDefault().newWatchService()
                registerDirectory(path)
                watchLoop()
            } catch (e: Exception) {
                Log.e(TAG, "Watch service error", e)
            }
        }
    }

    fun stop() {
        watchJob?.cancel()
        watchKeys.values.forEach { w -> w.cancel() }
        watchKeys.clear()
        watchService?.close()
        watchService = null
    }

    private suspend fun registerDirectory(
        path: Path,
    ): Unit = withContext(Dispatchers.IO) {
        if (path.isDirectory()) {
            val key: WatchKey = path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )
            watchKeys[path] = key
            if (recursively) {
                val files: Array<File>? = path.toFile().listFiles()
                files?.forEach { file: File ->
                    if (file.isDirectory) {
                        registerDirectory(file.toPath())
                    }
                }
            }
        }
    }

    private suspend fun watchLoop(): Unit = withContext(Dispatchers.IO) {
        while (true) {
            val key: WatchKey = watchService?.take() ?: break
            val dir: Path = watchKeys.map { e ->
                e.key to e.value
            }.firstOrNull { e ->
                e.second == key
            }?.first ?: continue
            val events: List<WatchEvent<*>> = key.pollEvents()
            for (event: WatchEvent<*> in events) {
                val kind: WatchEvent.Kind<*> = event.kind()
                if (kind == StandardWatchEventKinds.OVERFLOW) continue
                @Suppress("UNCHECKED_CAST")
                val ev = event as WatchEvent<Path>
                val filename: Path = ev.context()
                val child: Path = dir.resolve(filename)
                when (kind) {
                    StandardWatchEventKinds.ENTRY_CREATE -> handleCreate(dir, child)
                    StandardWatchEventKinds.ENTRY_MODIFY -> handleModify(dir, child)
                    StandardWatchEventKinds.ENTRY_DELETE -> handleDelete(dir, child)
                }
            }
            val valid: Boolean = key.reset()
            if (!valid) {
                watchKeys.remove(dir)
                if (watchKeys.isEmpty()) break
            }
        }
    }

    private suspend fun handleCreate(
        dir: Path,
        file: Path
    ) {
        if (dir.isDirectory()) {
            if (recursively) {
                registerDirectory(dir)
            }
        } else {
            onFileCreated(file, file.readBytes())
        }
    }

    @Suppress("unused")
    private suspend fun handleModify(
        dir: Path,
        file: Path
    ) {
        if (!file.isDirectory()) {
            onFileModified(file, file.readBytes())
        }
    }

    private suspend fun handleDelete(
        dir: Path,
        file: Path
    ) {
        if (watchKeys.contains(dir)) {
            watchKeys.remove(dir)
        }
        onFileDeleted(file)
    }

    companion object {
        val TAG = FileWatcherService::class.simpleName
    }
}