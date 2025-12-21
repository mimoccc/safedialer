package org.mjdev.safedialer.sync.email

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import org.kodein.di.DIAware
import org.mjdev.safedialer.providers.custom.email.MailItem
import kotlin.getValue

class ProviderEmails : ContentProvider(), DIAware {
    override val di by lazy {
        (context as DIAware).di
    }

//    private var lastUpdate: Long = 0L
//    private var updateJob: Job? = null
//    private var periodicJob: Job? = null
//    private val webDav: WebDavClient by instance()
//    private val dao: DAO by instance()
//    private var localEmails = mutableListOf<MailItem>()

//    private val auth
//        get() = context!!.getString(R.string.authority_emails)

    override fun onCreate(): Boolean {
//        Log.d(TAG, "Provider created")
//        localEmails = dao.emails.asList()
//        startPeriodicUpdates()
        return true
    }

//    private fun startPeriodicUpdates() = runCatching {
//        Log.d(TAG, "Start periodic update called")
//        periodicJob = CoroutineScope(Dispatchers.IO + Job()).launch {
//            while (true) {
//                updateMailsSafely()
//                delay(FIVE_MINUTES_IN_MILLIS)
//            }
//        }
//    }.onFailure { e ->
//        Log.e(TAG, "Email update error.", e)
//    }.onSuccess {
//        Log.d(TAG, "Emails updated.")
//    }

//    private suspend fun updateMailsSafely() = runCatching {
//        Log.d(TAG, "Start update called")
//        val now = System.currentTimeMillis()
//        if (((now - lastUpdate) >= FIVE_MINUTES_IN_MILLIS) || localEmails.isEmpty()) {
//            Log.d(TAG, "Starting update")
//            lastUpdate = now
//            updateJob = CoroutineScope(Dispatchers.IO + Job()).launch {
////                localEmails = dao.emails.asList<MailItem>()
//                webDav.allEmails.collectLatest { mapPathData ->
//                    Log.d(TAG, "Got (${mapPathData.size} emails.)")
//                    localEmails.clear()
//                    mapPathData.forEach { entry ->
//                        parseEmail(
//                            entry.key,
//                            entry.value
//                        ).also { mailItem ->
//                            localEmails.add(mailItem)
//                        }
//////                    val changes = emails.filter { newItem ->
//////                        localEmails.none { mi ->
//////                            mi.id == newItem.id && mi == newItem
//////                        } == true
//////                    }
//////                    localEmails = emails
//////                    if (changes.isNotEmpty()) {
//////                        changes.forEach { email ->
//////                            onChange(email.id)
//////                        }
//////                    }
//                    }
//                    Log.d(TAG, "Sending on change event.")
//                    onChange()
//                }
//            }
//        }
//    }.onFailure { exception ->
//        Log.e(TAG, exception.message, exception)
//        onChange()
//    }

//    private fun parseEmail(
//        path: String,
//        data: ByteArray
//    ): MailItem = path.split("/").let { parsedPath ->
//        parsedPath[parsedPath.size - 2]
//    }.let { folder ->
//        Log.d(TAG, "Parsing email at : $path.")
//        Log.d(TAG, "Folder: $folder")
//        Log.d(TAG, "Data size: ${data.size}")
//        parseMail(data).copy(
//            mailboxName = folder
//        ).apply {
//            Log.d(TAG, "Parsed email: $this")
//        }
//    }

    @Suppress("TYPE_INTERSECTION_AS_REIFIED_WARNING")
    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor {
//        Log.d(TAG, "Query called with uri: $uri")
//        Log.d(TAG, "Is emails: ${localEmails.isNotEmpty()}")
//        if (localEmails.isEmpty()) {
//            Log.d(TAG, "Starting periodic updates, due local emails are empty.")
//            startPeriodicUpdates()
//        }
//        Log.d(TAG, "Providing emails.")
//        Log.d(TAG, "Local emails: ${localEmails.size}")
        val cols = projection?.filterNotNull()?.toTypedArray() ?: PROJECTION
        val cursor = MatrixCursor(cols)
//        localEmails.forEach { mailItem ->
//            val row = cols.map { col ->
//                when (col) {
//                    MAIL_ITEM_ID -> mailItem.id
//                    MAIL_ITEM_SENDER_NAME -> mailItem.senderName
//                    MAIL_ITEM_SENDER_EMAIL -> mailItem.senderEmail
//                    MAIL_ITEM_SUBJECT -> mailItem.subject
//                    MAIL_ITEM_BODY -> mailItem.body
//                    MAIL_ITEM_CREATED_AT_MILLIS -> mailItem.createdAtMillis
//                    MAIL_ITEM_MAILBOX_NAME -> mailItem.mailboxName
//                    MAIL_ITEM_RECIPIENTS_CSV -> mailItem.recipients
//                    MAIL_ITEM_IS_DELETED -> mailItem.isDeleted.toInt()
//                    MAIL_ITEM_IS_FLAGGED -> mailItem.isFlagged.toInt()
//                    else -> null
//                }
//            }.toTypedArray()
//            cursor.addRow(row)
//        }
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

//    private suspend fun onChange() {
//        context?.let { ctx ->
//            val uriPath = "content://$auth"
//            val uri = uriPath.toUri()
//            withContext(Dispatchers.Main) {
//                ctx.contentResolver.notifyChange(uri, null)
//            }
//        }
//    }

//    private suspend fun onChange(
//        id: Long? = null
//    ) {
//        context?.let { ctx ->
//            val uriPath = if (id != null) "content://$auth/$id" else "content://$auth"
//            val uri = uriPath.toUri()
//            withContext(Dispatchers.Main) {
//                ctx.contentResolver.notifyChange(uri, null)
//            }
//        }
//    }

    companion object {
        val TAG = ProviderEmails::class.simpleName

//        const val FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000L

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
