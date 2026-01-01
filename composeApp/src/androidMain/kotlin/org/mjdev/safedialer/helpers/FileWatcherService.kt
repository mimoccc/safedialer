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
class FileWatcherService(
    private val basePath: Path,
    private val onFileCreated: (Path, ByteArray) -> Unit,
    private val onFileModified: (Path, ByteArray) -> Unit,
    private val onFileDeleted: (Path) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var watchJob: Job? = null
    private var watchService: WatchService? = null
    private val watchKeys = mutableMapOf<WatchKey, Path>()

    fun start() {
        watchJob = scope.launch {
            try {
                watchService = FileSystems.getDefault().newWatchService()
                registerDirectoryRecursively(basePath)
                watchLoop()
            } catch (e: Exception) {
                Log.e(TAG, "Watch service error", e)
            }
        }
    }

    fun stop() {
        watchJob?.cancel()
        watchKeys.keys.forEach { it.cancel() }
        watchKeys.clear()
        watchService?.close()
        watchService = null
    }

    private suspend fun registerDirectoryRecursively(
        path: Path
    ): Unit = withContext(Dispatchers.IO) {
        if (path.isDirectory()) {
            val key: WatchKey = path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )
            watchKeys[key] = path
            val files: Array<File>? = path.toFile().listFiles()
            files?.forEach { file: File ->
                if (file.isDirectory) {
                    registerDirectoryRecursively(file.toPath())
                }
            }
        }
    }

    private suspend fun watchLoop(): Unit = withContext(Dispatchers.IO) {
        while (true) {
            val key: WatchKey = watchService?.take() ?: break
            val dir: Path = watchKeys[key] ?: continue
            val events: List<WatchEvent<*>> = key.pollEvents()
            for (event: WatchEvent<*> in events) {
                val kind: WatchEvent.Kind<*> = event.kind()
                if (kind == StandardWatchEventKinds.OVERFLOW) continue
                @Suppress("UNCHECKED_CAST")
                val ev = event as WatchEvent<Path>
                val filename: Path = ev.context()
                val child: Path = dir.resolve(filename)
                when (kind) {
                    StandardWatchEventKinds.ENTRY_CREATE -> handleCreate(child)
                    StandardWatchEventKinds.ENTRY_MODIFY -> handleModify(child)
                    StandardWatchEventKinds.ENTRY_DELETE -> handleDelete(child)
                }
            }
            val valid: Boolean = key.reset()
            if (!valid) {
                watchKeys.remove(key)
                if (watchKeys.isEmpty()) break
            }
        }
    }

    private suspend fun handleCreate(
        path: Path
    ) {
        if (path.isDirectory()) {
            registerDirectoryRecursively(path)
        } else if (path.toFile().name.endsWith(".eml")) {
            onFileCreated(path, path.readBytes())
        }
    }

    private suspend fun handleModify(
        path: Path
    ) {
        if (!path.isDirectory() && path.toFile().name.endsWith(".eml")) {
            onFileModified(path, path.readBytes())
        }
    }

    private suspend fun handleDelete(
        path: Path
    ) {
        onFileDeleted(path)
    }

    companion object {
        val TAG = FileWatcherService::class.simpleName
    }
}