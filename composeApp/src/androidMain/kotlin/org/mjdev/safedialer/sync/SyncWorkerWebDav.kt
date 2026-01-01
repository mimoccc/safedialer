package org.mjdev.safedialer.sync

import android.app.NotificationManager
import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.ContentResolver.getSyncAutomatically
import android.content.Context
import android.content.SyncResult
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.extensions.CustomExt.createIfNoExists
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

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class SyncWorkerWebDav<T : Entity>(
    context: Context,
    // synced subfolder in root webdav folder
    val dirName: String,
    val providerAuth: String,
    val filesDir: File = provideFileBase(context),
    autoInitialize: Boolean = true,
    allowParallelSyncs: Boolean = false
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs), DIAware {
    override val di by closestDI()

    protected var isRunning = false

    protected val webDav: WebDavClient by instance()
    protected val notificationManager: NotificationManager by instance()

    // base root webdav folder containing all the user data
    protected val baseRemoteFilesPath: String = "${webDav.baseUrl.trimEnd('/')}/$dirName"

    private val notificationId = dirName.hashCode()

    // standard path root of webdav custom folder
    protected val baseLocalFilesPath: Path by lazy {
        Paths.get(
            filesDir.absolutePath,
            dirName
        ).createIfNoExists()
    }

    // Private subfolder in synced folder / user visible only
    protected val privateSyncDir: Path by lazy {
        baseLocalFilesPath.resolve(
            DIR_PRIVATE
        ).createIfNoExists()
    }
    // Company subfolder in synced folder / company visible
    protected val companySyncDir: Path by lazy {
        baseLocalFilesPath.resolve(
            DIR_COMPANY
        ).createIfNoExists()
    }
    // Public subfolder in synced folder / public for everybody
    protected val publicSyncDir: Path by lazy {
        baseLocalFilesPath.resolve(
            DIR_PUBLIC
        ).createIfNoExists()
    }

    // Invoices only incoming subfolder / only user visible
    protected val incomingSyncDir: Path by lazy {
        baseLocalFilesPath.resolve(
            DIR_INCOMING
        ).createIfNoExists()
    }
    // Invoices only outgoing subfolder / only user visible
    protected val outgoingSyncDir: Path by lazy {
        baseLocalFilesPath.resolve(
            DIR_OUTGOING
        ).createIfNoExists()
    }

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        val isEnabled = getSyncAutomatically(account, authority)
        if (!isEnabled) return
        if (isRunning) return
        isRunning = true
        runCatching {
            Log.d(TAG, "Performing sync for $dirName")
            showNotification()
            prepareLocalFiles(syncResult)
            prepareRemoteFiles(syncResult)
            mergeChanges()
            hideNotification()
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    private fun prepareRemoteFiles(
        syncResult: SyncResult?
    ) = runCatching {
        Log.d(this::class.simpleName, "Syncing: $dirName")
        syncDirectory(
            localDirPath = baseLocalFilesPath,
            remoteDirPath = baseRemoteFilesPath,
            syncResult = syncResult
        )
    }.onFailure { e ->
        e.printStackTrace()
    }

    private fun showNotification() = runCatching {
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
    }.onFailure { e ->
        e.printStackTrace()
    }

    private fun updateNotification(
        syncResult: SyncResult?
    ) = runCatching {
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
    }.onFailure { e ->
        e.printStackTrace()
    }

    private fun hideNotification() = runCatching {
        notificationManager.cancel(notificationId)
    }.onFailure { e ->
        e.printStackTrace()
    }

    private fun syncDirectory(
        localDirPath: Path,
        remoteDirPath: String,
        syncResult: SyncResult?
    ) {
        runCatching {
            if (!localDirPath.exists()) localDirPath.createDirectories()
            val remoteDirStr = remoteDirPath.replace(webDav.baseUrl, "")
            webDav.mkcol(remoteDirStr)
            val localEntries = Files.list(localDirPath).collect(
                Collectors.toList()
            )
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

    private fun handleConflict(
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
                ConflictSolution.UPLOAD_LOCAL, ConflictSolution.UPDATE_REMOTE -> {
                    webDav.putFile(remoteFileStr, localData, "application/octet-stream")
                    syncResult?.stats?.numInserts = (syncResult.stats?.numInserts ?: 0) + 1
                }

                ConflictSolution.DOWNLOAD_REMOTE, ConflictSolution.UPDATE_LOCAL -> {
                    pathLocal.writeBytes(remoteData)
                    syncResult?.stats?.numUpdates = (syncResult.stats?.numUpdates ?: 0) + 1
                }

                ConflictSolution.DELETE_LOCAL -> {
                    pathLocal.deleteIfExists()
                    syncResult?.stats?.numDeletes = (syncResult.stats?.numDeletes ?: 0) + 1
                }

                ConflictSolution.DELETE_REMOTE -> {
                    webDav.delete(remoteFileStr)
                    syncResult?.stats?.numDeletes = (syncResult.stats?.numDeletes ?: 0) + 1
                }

                ConflictSolution.IGNORE -> {
                    syncResult?.stats?.numSkippedEntries =
                        (syncResult.stats?.numSkippedEntries ?: 0) + 1
                }
            }
            updateNotification(syncResult)
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    abstract fun prepareLocalFiles(syncResult: SyncResult?)
    abstract fun mergeChanges()

    open fun mergeConflict(
        conflict: ConflictType,
        pathLocal: Path,
        pathRemote: String,
        localData: ByteArray,
        remoteData: ByteArray
    ): ConflictSolution = runCatching {
        when (conflict) {
            ConflictType.MISSING_REMOTE -> {
                if (localData.isEmpty() || isDeleted(pathLocal)) ConflictSolution.IGNORE
                else ConflictSolution.UPLOAD_LOCAL
            }

            ConflictType.MISSING_LOCAL -> {
                if (remoteData.isEmpty()) ConflictSolution.IGNORE
                else ConflictSolution.DOWNLOAD_REMOTE
            }

            ConflictType.LOCAL_DIFFERENT_FROM_REMOTE -> {
                val timeLocal = Files.getLastModifiedTime(pathLocal).toMillis()
                val remoteFileStr = pathRemote.replace(webDav.baseUrl, "")
                val timeRemote = webDav.getLastModified(remoteFileStr)
                when {
                    timeRemote == null -> {
                        Log.w(TAG, "Cannot get remote timestamp, using size-based strategy")
                        if (remoteData.size > localData.size) ConflictSolution.DOWNLOAD_REMOTE
                        else ConflictSolution.UPDATE_LOCAL
                    }

                    timeLocal > timeRemote -> {
                        Log.d(TAG, "Local file is newer, uploading")
                        ConflictSolution.UPDATE_REMOTE
                    }

                    timeRemote > timeLocal -> {
                        Log.d(TAG, "Remote file is newer, downloading")
                        ConflictSolution.UPDATE_LOCAL
                    }

                    else -> {
                        Log.d(TAG, "Files have same timestamp but different content, ignoring")
                        ConflictSolution.IGNORE
                    }
                }
            }
        }
    }.getOrElse { e ->
        Log.e(TAG, "Error in mergeConflict, using safe fallback", e)
        ConflictSolution.IGNORE
    }

    fun delete(path: Path) {
        // todo delete file -> move it to trash
    }

    private fun isDeleted(localPath: Path): Boolean {
        // return if trash contains file
        return false
    }

    fun submitOnChangeEvent(
        id: Long? = null,
    ) = CoroutineScope(Dispatchers.Default).launch {
        val uriPath = if (id != null) "content://$providerAuth/$id"
        else "content://$providerAuth"
        val uri = uriPath.toUri()
        withContext(Dispatchers.Main) {
            context!!.contentResolver.notifyChange(uri, null)
        }
    }

    enum class ConflictType {
        // remote file is missing
        MISSING_REMOTE,
        // local file is missing
        MISSING_LOCAL,
        // local or remote file different
        LOCAL_DIFFERENT_FROM_REMOTE,
    }

    // solution when conflict happen
    enum class ConflictSolution {
        // ignore, no op
        IGNORE,
        // upload local to remote
        UPLOAD_LOCAL,
        // download remote to local
        DOWNLOAD_REMOTE,
        // delete local
        DELETE_LOCAL,
        // delete remote
        DELETE_REMOTE,
        // update local file, replace with remote
        UPDATE_LOCAL,
        // update remote file, replace with local
        UPDATE_REMOTE
    }

    companion object {
        private val TAG = this::class.simpleName

        // public subdirectory
        private const val DIR_PUBLIC = "Public"
        // private subdirectory
        private const val DIR_PRIVATE = "Private"
        // company visible directory
        private const val DIR_COMPANY = "Company"

        // subdir only for invoices
        private const val DIR_INCOMING = "Incoming"
        // subdir only for invoices
        private const val DIR_OUTGOING = "Outgoing"

        private fun String.toHumanReadable(): String = this.replace(".", "")
            .replace("/", " ")
            .replace("\\", " ")

        fun provideFileBase(context: Context): File {
            val appName = BuildConfig.APP_NAME
            val externalDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    File(Environment.getExternalStorageDirectory(), appName)
                } else {
                    context.getExternalFilesDir(null)?.resolve(appName)
                }
            } else {
                File(Environment.getExternalStorageDirectory(), appName)
            }
            val baseDir = externalDir ?: context.filesDir.resolve(appName)
            return baseDir.apply {
                if (!exists()) {
                    val created = mkdirs()
                    Log.d(TAG, "Creating base directory: $absolutePath, success: $created")
                }
            }
        }
    }
}


