package org.mjdev.safedialer.sync

import android.app.NotificationManager
import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.service.IncomingCallService.Companion.CHANNEL_ID
import org.mjdev.safedialer.webdav.WebDavClient
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
abstract class SyncWorkerWebDav<T : Entity>(
    context: Context,
    val dirName: String,
    val filesDir: File = context.filesDir,
    autoInitialize: Boolean = true,
    allowParallelSyncs: Boolean = false
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs), DIAware {
    private val TAG = this::class.simpleName

    override val di by closestDI()

    protected val webDav: WebDavClient by instance()
    protected val notificationManager: NotificationManager by instance()
    protected var scope: CoroutineScope? = null

    protected val baseLocalFilesPath: Path = Paths.get(filesDir.absolutePath, dirName)
    protected val baseRemoteFilesPath: String = "${webDav.baseUrl.trimEnd('/')}/$dirName"

    private val notificationId = dirName.hashCode()

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        runCatching {
            Log.d(TAG, "Performing sync for $dirName")
            if (scope?.isActive == true) {
                scope?.cancel(null)
            }
            scope = CoroutineScope(Dispatchers.IO + Job())
            scope?.launch {
                sync(syncResult)
            }
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    suspend fun sync(
        syncResult: SyncResult?
    ) = runCatching {
        Log.d(this::class.simpleName, "Syncing: $dirName")
        showNotification()
        prepareLocalFiles()
        syncDirectory(
            localDirPath = baseLocalFilesPath,
            remoteDirPath = baseRemoteFilesPath,
            syncResult = syncResult
        )
        hideNotification()
    }.onFailure { e ->
        e.printStackTrace()
    }

    private suspend fun showNotification() = runCatching {
        withContext(Dispatchers.Main) {
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Syncing $dirName")
                .setContentText("Sync in progress...")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setOngoing(true)
                .build()
                .also { notification ->
                    notificationManager.cancel(notificationId)
                    notificationManager.notify(notificationId, notification)
                }
        }
    }.onFailure { e ->
        e.printStackTrace()
    }

    private suspend fun updateNotification(
        syncResult: SyncResult?
    ) = runCatching {
        withContext(Dispatchers.Main) {
            val stats = syncResult?.stats
            val details = if (stats != null) {
                "Added: ${stats.numInserts}, Updated: ${stats.numUpdates}, Deleted: ${stats.numDeletes}"
            } else {
                "Sync in progress..."
            }
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("${BuildConfig.APP_NAME} - Syncing ${dirName.toHumanReadable()}")
                .setContentText(details)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setOngoing(true)
                .build()
            notificationManager.notify(notificationId, notification)
        }
    }.onFailure { e ->
        e.printStackTrace()
    }

    private fun hideNotification() = runCatching {
        notificationManager.cancel(notificationId)
    }.onFailure { e ->
        e.printStackTrace()
    }

    private suspend fun syncDirectory(
        localDirPath: Path,
        remoteDirPath: String,
        syncResult: SyncResult?
    ) {
        runCatching {
            if (!localDirPath.exists()) localDirPath.createDirectories()
            val remoteDirStr = remoteDirPath.replace(webDav.baseUrl, "")
            webDav.mkcol(remoteDirStr)
            val localEntries = withContext(Dispatchers.IO) {
                Files.list(localDirPath)
            }.use { it.collect(Collectors.toList()) }
            val remoteEntries = webDav.listExtended(remoteDirStr).filter { remoteFile ->
                remoteFile.name != "." &&
                        remoteFile.name != ".." &&
                        remoteFile.name.trimEnd('/') != dirName
            }
            val processedRemoteNames = mutableSetOf<String>()
            localEntries.forEach { localPath ->
                val name = localPath.name
                val remoteEntry = remoteEntries.find { it.name == name }
                val remotePath = "${remoteDirPath.trimEnd('/')}/$name"
                val remoteFileStr = remotePath.replace(webDav.baseUrl, "")
                if (localPath.isDirectory()) {
                    syncDirectory(localPath, remotePath, syncResult)
                } else {
                    if (remoteEntry == null) {
                        val localData = localPath.readBytes()
                        handleConflict(
                            ConflictType.MISSING_REMOTE,
                            localPath,
                            remotePath,
                            localData,
                            ByteArray(0),
                            syncResult
                        )
                    } else {
                        val localData = localPath.readBytes()
                        val remoteData = webDav.readFile(remoteFileStr)
                        if (!localData.contentEquals(remoteData)) {
                            handleConflict(
                                ConflictType.LOCAL_DIFFERENT_FROM_REMOTE,
                                localPath,
                                remotePath,
                                localData,
                                remoteData,
                                syncResult
                            )
                        }
                    }
                }
                processedRemoteNames.add(name)
            }
            remoteEntries.forEach { remoteEntry ->
                if (remoteEntry.name !in processedRemoteNames) {
                    val name = remoteEntry.name
                    val localPath = localDirPath.resolve(name)
                    val remotePath = "${remoteDirPath.trimEnd('/')}/$name"
                    val remoteFileStr = remotePath.replace(webDav.baseUrl, "")
                    if (remoteEntry.isCollection) {
                        syncDirectory(localPath, remotePath, syncResult)
                    } else {
                        val remoteData = webDav.readFile(remoteFileStr)
                        handleConflict(
                            ConflictType.MISSING_LOCAL,
                            localPath,
                            remotePath,
                            ByteArray(0),
                            remoteData,
                            syncResult
                        )
                    }
                }
            }
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    private suspend fun handleConflict(
        type: ConflictType,
        pathLocal: Path,
        pathRemote: String,
        localData: ByteArray,
        remoteData: ByteArray,
        syncResult: SyncResult?
    ) {
        runCatching {
            val solution = mergeConflict(type, pathLocal, pathRemote, localData, remoteData)
            val remoteFileStr = pathRemote.replace(webDav.baseUrl, "")
            when (solution) {
                ConflictSolution.UPLOAD_LOCAL -> {
                    webDav.putFile(remoteFileStr, localData, "application/octet-stream")
                    syncResult?.stats?.numInserts = (syncResult?.stats?.numInserts ?: 0) + 1
                }

                ConflictSolution.DOWNLOAD_REMOTE -> {
                    pathLocal.writeBytes(remoteData)
                    syncResult?.stats?.numUpdates = (syncResult?.stats?.numUpdates ?: 0) + 1
                }

                ConflictSolution.DELETE_LOCAL -> {
                    pathLocal.deleteIfExists()
                    syncResult?.stats?.numDeletes = (syncResult?.stats?.numDeletes ?: 0) + 1
                }

                ConflictSolution.DELETE_REMOTE -> {
                    webDav.delete(remoteFileStr)
                    syncResult?.stats?.numDeletes = (syncResult?.stats?.numDeletes ?: 0) + 1
                }

                ConflictSolution.IGNORE -> {
                    syncResult?.stats?.numSkippedEntries =
                        (syncResult?.stats?.numSkippedEntries ?: 0) + 1
                }
            }
            updateNotification(syncResult)
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    abstract suspend fun prepareLocalFiles()

    abstract suspend fun mergeConflict(
        conflict: ConflictType,
        pathLocal: Path,
        pathRemote: String,
        localData: ByteArray,
        remoteData: ByteArray
    ): ConflictSolution

    enum class ConflictType {
        MISSING_REMOTE,
        MISSING_LOCAL,
        LOCAL_DIFFERENT_FROM_REMOTE,
    }

    enum class ConflictSolution {
        IGNORE,
        UPLOAD_LOCAL,
        DOWNLOAD_REMOTE,
        DELETE_LOCAL,
        DELETE_REMOTE
    }

    companion object {
        private fun String.toHumanReadable(): String = this.replace(".", "")
            .replace("/", " ")
            .replace("\\", " ")
    }
}


