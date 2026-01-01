package org.mjdev.safedialer.sync.email

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.mjdev.safedialer.R
import org.mjdev.safedialer.extensions.CustomExt.submitOnChangeEvent
import org.mjdev.safedialer.extensions.MailItemExt.parseMail
import org.mjdev.safedialer.helpers.FileWatcherService
import org.mjdev.safedialer.providers.core.Entity.Companion.toInt
import org.mjdev.safedialer.providers.custom.email.MailItem
import org.mjdev.safedialer.sync.SyncWorkerWebDav.Companion.provideFileBase
import org.mjdev.safedialer.webdav.WebDavClient
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readBytes

class ProviderEmails : ContentProvider(), DIAware {
    override val di by lazy {
        (context as DIAware).di
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val dirName = WebDavClient.DIR_IMAP
    private var fileWatcher: FileWatcherService? = null
    private val localEmails = CopyOnWriteArrayList<MailItem>()

    private val filesDir: File by lazy {
        provideFileBase(context!!)
    }

    private val baseLocalFilesPath: Path by lazy {
        Paths.get(
            filesDir.absolutePath,
            dirName
        )
    }

    override fun onCreate(): Boolean {
//        readAllMails()
//        startFileWatcher()
        return true
    }

    override fun shutdown() {
        super.shutdown()
        fileWatcher?.stop()
    }

    private fun startFileWatcher() {
        fileWatcher = FileWatcherService(
            basePath = baseLocalFilesPath,
            onFileCreated = { path, data ->
                val absolutePath = path.toFile().absolutePath
                parseEmail(absolutePath, data).also { email ->
                    localEmails.add(email)
                    submitOnChangeEvent(localEmails.size.toLong())
                    Log.d(TAG, "Email created: ${email.subject}")
                }
            },
            onFileModified = { path, data ->
                val absolutePath = path.toFile().absolutePath
                localEmails.indexOfFirst { email ->
                    email.id == absolutePath.hashCode().toLong()
                }.takeIf {
                    it != -1
                }?.let { index ->
                    parseEmail(absolutePath, data).also { email ->
                        localEmails.set(index, email)
                        submitOnChangeEvent(email.id)
                        Log.d(TAG, "Email modified: ${email.subject}")
                    }
                }
            },
            onFileDeleted = { path ->
                val absolutePath = path.toFile().absolutePath
                localEmails.removeIf { it.id == absolutePath.hashCode().toLong() }.also { removed ->
                    if (removed) {
                        submitOnChangeEvent()
                        Log.d(TAG, "Email deleted: $absolutePath")
                    }
                }
            }
        )
        fileWatcher?.start()
    }

    private fun readAllMails() = scope.launch {
        if (baseLocalFilesPath.exists()) {
            Files.walk(baseLocalFilesPath).use { paths ->
                paths.forEach { path ->
                    if (path.isDirectory()) {
                        // omit
                    } else if (path.toFile().absolutePath.contains(".eml")) {
                        parseEmail(
                            path.toFile().absolutePath,
                            path.readBytes()
                        ).also { email ->
                            localEmails.add(email)
                            submitOnChangeEvent(localEmails.size.toLong())
                        }
                    } else {
                        Log.e(TAG, "Unrecognizable email file: $path")
                    }
                }
            }
        } else {
            Log.w(TAG, "Folder with emails does not exists: $baseLocalFilesPath")
        }
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
        parseMail(data).copy(
            mailboxName = folder
        ).apply {
            Log.d(TAG, "Parsed email: $this")
        }
    }

    @Suppress("TYPE_INTERSECTION_AS_REIFIED_WARNING")
    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor {
        Log.d(TAG, "Query called with uri: $uri")
        Log.d(TAG, "Is emails: ${localEmails.isNotEmpty()}")
//        if (localEmails.isEmpty()) {
//            Log.d(TAG, "Starting update, due local emails are empty.")
//            readAllMails()
//        }
        Log.d(TAG, "Providing emails.")
        Log.d(TAG, "Local emails: ${localEmails.size}")
        val cols = projection?.filterNotNull()?.toTypedArray() ?: PROJECTION
        val cursor = MatrixCursor(cols)
        localEmails.forEach { mailItem ->
            val row = cols.map { col ->
                when (col) {
                    MAIL_ITEM_ID -> mailItem.id
                    MAIL_ITEM_SENDER_NAME -> mailItem.senderName
                    MAIL_ITEM_SENDER_EMAIL -> mailItem.senderEmail
                    MAIL_ITEM_SUBJECT -> mailItem.subject
                    MAIL_ITEM_BODY -> mailItem.body
                    MAIL_ITEM_CREATED_AT_MILLIS -> mailItem.createdAtMillis
                    MAIL_ITEM_MAILBOX_NAME -> mailItem.mailboxName
                    MAIL_ITEM_RECIPIENTS_CSV -> mailItem.recipients
                    MAIL_ITEM_IS_DELETED -> mailItem.isDeleted.toInt()
                    MAIL_ITEM_IS_FLAGGED -> mailItem.isFlagged.toInt()
                    else -> null
                }
            }.toTypedArray()
            cursor.addRow(row)
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

        val Context.EmailsProviderAuth: String
            get() = getString(R.string.authority_emails)

        const val MAIL_ITEM_ID = "id"
        const val MAIL_ITEM_SENDER_NAME = "senderName"
        const val MAIL_ITEM_SENDER_EMAIL = "senderEmail"
        const val MAIL_ITEM_SUBJECT = "subject"
        const val MAIL_ITEM_BODY = "body"
        const val MAIL_ITEM_CREATED_AT_MILLIS = "createdAtMillis"
        const val MAIL_ITEM_MAILBOX_NAME = "mailboxName"
        const val MAIL_ITEM_RECIPIENTS_CSV = "recipientsCsv"
        const val MAIL_ITEM_IS_DELETED = "isDeleted"
        const val MAIL_ITEM_IS_FLAGGED = "isFlagged"
        const val MAIL_ITEM_IS_ENCRYPTED = "isEncrypted"

        const val MAIL_FOLDER_NAME = "mailFolderName"

        val PROJECTION = arrayOf(
            MAIL_ITEM_ID,
            MAIL_ITEM_SENDER_NAME,
            MAIL_ITEM_SENDER_EMAIL,
            MAIL_ITEM_SUBJECT,
            MAIL_ITEM_BODY,
            MAIL_ITEM_CREATED_AT_MILLIS,
            MAIL_ITEM_MAILBOX_NAME,
            MAIL_ITEM_RECIPIENTS_CSV,
            MAIL_ITEM_IS_DELETED,
            MAIL_ITEM_IS_FLAGGED
        )
    }
}
