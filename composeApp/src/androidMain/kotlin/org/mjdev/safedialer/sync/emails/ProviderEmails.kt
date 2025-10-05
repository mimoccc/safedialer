package org.mjdev.safedialer.sync.emails

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.core.net.toUri
import kotbase.CouchbaseLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.mjdev.safedialer.R
import org.mjdev.safedialer.dao.DAO
import org.mjdev.safedialer.providers.custom.email.MailClient
import org.mjdev.safedialer.providers.custom.email.MailItem
import kotlin.getValue

class ProviderEmails() : ContentProvider(), DIAware {
    private var lastUpdate: Long = 0L
    private var updateJob: Job? = null
    private var periodicJob: Job? = null

    override val di by lazy {
        (context as DIAware).di
    }

    private val mailClient: MailClient by instance()
    private val dao: DAO by instance()
    private var localEmails: List<MailItem>? = null

    override fun onCreate(): Boolean {
        context?.applicationContext?.let { application ->
            CouchbaseLite.init(application)
        }
        localEmails = dao.emails.asList()
        startPeriodicUpdates()
        return true
    }

    private fun startPeriodicUpdates() = runCatching {
        periodicJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                updateMailsSafely()
                delay(FIVE_MINUTES_IN_MILLIS)
            }
        }
    }.onFailure { exception ->
        exception.printStackTrace()
    }

    private suspend fun updateMailsSafely() = runCatching {
        val now = System.currentTimeMillis()
        if (((now - lastUpdate) >= FIVE_MINUTES_IN_MILLIS) || (localEmails == null)) {
            lastUpdate = now
            updateJob = CoroutineScope(Dispatchers.IO).launch {
                localEmails = dao.emails.asList<MailItem>()
                mailClient.allMails.collectLatest { emails ->
                    dao.emails.clear()
                    dao.emails.addAll(emails)
                    val changes = emails.filter { newItem ->
                        localEmails?.none { mi ->
                            mi.id == newItem.id && mi == newItem
                        } == true
                    }
                    localEmails = emails
                    if (changes.isNotEmpty()) {
                        changes.forEach { email ->
                            onChange(email.id)
                        }
                    }
                }
            }
        }
    }.onFailure { exception ->
        exception.printStackTrace()
    }

    @Suppress("TYPE_INTERSECTION_AS_REIFIED_WARNING")
    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor {
        val cols = projection?.filterNotNull()?.toTypedArray() ?: PROJECTION
        val cursor = MatrixCursor(cols)
        localEmails?.forEach { mailItem ->
            val row = cols.map { col ->
                when (col) {
                    MAIL_ITEM_ID -> mailItem.id
                    MAIL_ITEM_SENDER_NAME -> mailItem.senderName
                    MAIL_ITEM_SENDER_EMAIL -> mailItem.senderEmail
                    MAIL_ITEM_SUBJECT -> mailItem.subject
                    MAIL_ITEM_BODY -> mailItem.body
                    MAIL_ITEM_CREATED_AT_MILLIS -> mailItem.createdAtMillis
                    MAIL_ITEM_MAILBOX_NAME -> mailItem.mailboxName
                    MAIL_ITEM_RECIPIENTS_CSV -> mailItem.recipientsCsv
                    MAIL_ITEM_IS_DELETED -> mailItem.isDeleted
                    MAIL_ITEM_IS_FLAGGED -> mailItem.isFlagged
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

    private suspend fun onChange(
        id: Long? = null
    ) {
        context?.let { ctx ->
            val auth = ctx.getString(R.string.authority_emails)
            val uriPath = if (id != null) "content://$auth/$id" else "content://$auth"
            val uri = uriPath.toUri()
            withContext(Dispatchers.Main) {
                ctx.contentResolver.notifyChange(uri, null)
            }
        }
    }

    companion object {
        const val FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000L

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
