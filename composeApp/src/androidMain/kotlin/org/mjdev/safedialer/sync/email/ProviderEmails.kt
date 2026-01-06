package org.mjdev.safedialer.sync.email

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import org.mjdev.safedialer.R
import org.mjdev.safedialer.extensions.MailItemExt.parseMail
import org.mjdev.safedialer.helpers.FileWatcherService
import org.mjdev.safedialer.providers.core.Entity.Companion.toInt
import org.mjdev.safedialer.providers.custom.email.MailItem
import org.mjdev.safedialer.sync.SyncWorkerWebDav.Companion.provideFileBase
import org.mjdev.safedialer.webdav.WebDavClient
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.also
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readBytes

class ProviderEmails : ContentProvider() {
    private val dirName = WebDavClient.DIR_IMAP
    private var fileWatcher: FileWatcherService? = null
    private val authority
        get() = context?.getString(R.string.authority_emails)
    private val filesDir: File? by lazy {
        provideFileBase(context!!)
    }
    private val baseLocalFilesPath: Path by lazy {
        Paths.get(
            filesDir?.absolutePath,
            dirName
        )
    }
    private val emails: Flow<List<MailItem>> = flow {
        val emails = mutableListOf<MailItem>()
        if (baseLocalFilesPath.exists()) {
            Files.walk(baseLocalFilesPath).use { paths ->
                paths.forEach { path ->
                    if (path.isDirectory()) {
                        // watcher takes this
                    } else if (path.toFile().absolutePath.contains(".eml")) {
                        parseEmail(
                            path.toFile().absolutePath,
                            path.readBytes()
                        ).also { email ->
                            emails.add(email)
                        }
                    } else {
                        Log.e(TAG, "Unrecognizable email file: $path")
                    }
                }
            }
        } else {
            Log.w(TAG, "Folder with emails does not exists: $baseLocalFilesPath")
        }
        emit(emails)
    }.flowOn(Dispatchers.IO)

    private fun startFileWatcher() {
        fileWatcher = FileWatcherService(
            path = baseLocalFilesPath,
            recursively = true,
            onFileCreated = { path, data ->
                // todo
                notifyChanged()
            },
            onFileModified = { path, data ->
                notifyChanged()
            },
            onFileDeleted = { path ->
                notifyChanged()
            }
        )
        fileWatcher?.start()
    }

    private fun notifyChanged(
        id: Long? = null
    ) {
        val uriPath = if (id != null) "content://$authority/$id"
        else "content://$authority"
        val uri = uriPath.toUri()
        Log.d(TAG, "Submitting change of $dirName : $id")
        context?.contentResolver?.notifyChange(uri, null)
    }

    private fun parseEmail(
        path: String,
        data: ByteArray
    ): MailItem = path.split("/").let { parsedPath ->
        parsedPath[parsedPath.size - 2]
    }.let { folder ->
        Log.d(TAG, "Parsing email at : $path.")
        Log.d(TAG, "Folder: $folder")
        Log.d(TAG, "Data size: ${data.size}")
        parseMail(path, data).copy(
            mailboxName = folder
        ).apply {
            Log.d(TAG, "Parsed email: $this")
        }
    }

    override fun onCreate(): Boolean {
        startFileWatcher()
        return true
    }

    override fun shutdown() {
        super.shutdown()
        fileWatcher?.stop()
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor {
        Log.d(TAG, "Query called with uri: $uri")
        val cols = projection?.filterNotNull()?.toTypedArray() ?: PROJECTION
        val cursor = MatrixCursor(cols)
        runBlocking {
            emails.collectLatest { mailList ->
                mailList.forEach { mailItem ->
                    cols.map { col ->
                        when (col) {
                            MAIL_ITEM_ID -> mailItem.id
                            MAIL_ITEM_SENDER_NAME -> mailItem.senderName
                            MAIL_ITEM_SENDER_EMAIL -> mailItem.senderEmail
                            MAIL_ITEM_SUBJECT -> mailItem.subject
                            MAIL_ITEM_URI -> mailItem.fileUri
                            MAIL_ITEM_CREATED_AT_MILLIS -> mailItem.createdAtMillis
                            MAIL_ITEM_MAILBOX_NAME -> mailItem.mailboxName
                            MAIL_ITEM_RECIPIENTS_CSV -> mailItem.recipients
                            MAIL_ITEM_IS_DELETED -> mailItem.isDeleted.toInt()
                            MAIL_ITEM_IS_FLAGGED -> mailItem.isFlagged.toInt()
                            else -> null
                        }
                    }.also { row ->
                        cursor.addRow(row)
                    }
                }
            }
        }
        return cursor
    }

    override fun getType(
        uri: Uri
    ): String? = MailItem::class.simpleName

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? = null

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int = 0

    companion object {
        val TAG = ProviderEmails::class.simpleName

        const val MAIL_ITEM_ID = "id"
        const val MAIL_ITEM_SENDER_NAME = "senderName"
        const val MAIL_ITEM_SENDER_EMAIL = "senderEmail"
        const val MAIL_ITEM_SUBJECT = "subject"
        const val MAIL_ITEM_URI = "uri"
        const val MAIL_ITEM_CREATED_AT_MILLIS = "createdAtMillis"
        const val MAIL_ITEM_MAILBOX_NAME = "mailboxName"
        const val MAIL_ITEM_RECIPIENTS_CSV = "recipientsCsv"
        const val MAIL_ITEM_IS_DELETED = "isDeleted"
        const val MAIL_ITEM_IS_FLAGGED = "isFlagged"
        const val MAIL_ITEM_IS_ENCRYPTED = "isEncrypted"

        const val MAIL_FOLDER_NAME = "mailFolderName"

        private val PROJECTION = arrayOf(
            MAIL_ITEM_ID,
            MAIL_ITEM_SENDER_NAME,
            MAIL_ITEM_SENDER_EMAIL,
            MAIL_ITEM_SUBJECT,
            MAIL_ITEM_URI,
            MAIL_ITEM_CREATED_AT_MILLIS,
            MAIL_ITEM_MAILBOX_NAME,
            MAIL_ITEM_RECIPIENTS_CSV,
            MAIL_ITEM_IS_DELETED,
            MAIL_ITEM_IS_FLAGGED
        )
    }
}
