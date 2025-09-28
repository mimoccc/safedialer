package org.mjdev.safedialer.providers.custom.email

import android.util.Log
import jakarta.mail.Authenticator
import jakarta.mail.Flags
import jakarta.mail.Folder
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import java.util.Properties
import android.content.Context
import jakarta.mail.Multipart
import jakarta.mail.Part
import kotlinx.coroutines.flow.map
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.sync.contacts.ContactAutoEnricher
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class MailClient(
    val hostImap: String = BuildConfig.SERVER,
    val hostSmtp: String = BuildConfig.SERVER,
    val portImap: Int = 993,
    val portSmtp: Int = 465,
    val userImap: String = BuildConfig.SERVER_UNAME,
    val passwordImap: String = BuildConfig.SERVER_UPASS,
    val userSmtp: String = BuildConfig.SERVER_UNAME,
    val passwordSmtp: String = BuildConfig.SERVER_UPASS,
    val props: Properties = Properties(),
    val pgpCertData: ByteArray = ByteArray(0),
    val pgpPassword: String = ""
) {
    val mailFolders = flow {
        runCatching {
            listMailFolders().onSuccess { data ->
                emit(data)
            }.onFailure { e ->
                emit(emptyList())
            }
        }.getOrNull() ?: emit(emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allMails = mailFolders.map { folders ->
        folders.flatMap { folder ->
            getMailItemsInFolder(folder.fullName).getOrNull() ?: emptyList()
        }
    }

    suspend fun setMailRead(
        folderName: String = MAIL_DIR_INBOX,
        messageIds: List<Int>,
    ) = runCatching {
        Log.d(
            TAG,
            "Connecting to IMAP server at $hostImap:$portImap with SSL to mark messages as read"
        )
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val folder = store.getFolder(folderName)
            folder.open(Folder.READ_WRITE)
            try {
                messageIds.forEach { messageId ->
                    if (messageId > 0 && messageId <= folder.messageCount) {
                        val message = folder.getMessage(messageId)
                        message.setFlag(Flags.Flag.SEEN, true)
                        Log.d(TAG, "Marked message $messageId as read")
                    } else {
                        Log.w(TAG, "Invalid message ID: $messageId")
                    }
                }
                Log.d(TAG, "Successfully marked ${messageIds.size} messages as read in $folderName")
            } finally {
                folder.close(false)
            }
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to mark mail as read: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun setMailUnread(
        folderName: String = MAIL_DIR_INBOX,
        messageIds: List<Int>,
    ) = runCatching {
        Log.d(
            TAG,
            "Connecting to IMAP server at $hostImap:$portImap with SSL to mark messages as unread"
        )
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val folder = store.getFolder(folderName)
            folder.open(Folder.READ_WRITE)
            try {
                messageIds.forEach { messageId ->
                    if (messageId > 0 && messageId <= folder.messageCount) {
                        val message = folder.getMessage(messageId)
                        message.setFlag(Flags.Flag.SEEN, false)
                        Log.d(TAG, "Marked message $messageId as unread")
                    } else {
                        Log.w(TAG, "Invalid message ID: $messageId")
                    }
                }
                Log.d(
                    TAG,
                    "Successfully marked ${messageIds.size} messages as unread in $folderName"
                )
            } finally {
                folder.close(false)
            }
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to mark mail as unread: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun deleteMail(
        folderName: String = MAIL_DIR_INBOX,
        messageIds: List<Int>,
    ) = runCatching {
        Log.d(TAG, "Connecting to IMAP server at $hostImap:$portImap with SSL to delete messages")
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val folder = store.getFolder(folderName)
            folder.open(Folder.READ_WRITE)
            try {
                messageIds.forEach { messageId ->
                    if (messageId > 0 && messageId <= folder.messageCount) {
                        val message = folder.getMessage(messageId)
                        message.setFlag(Flags.Flag.DELETED, true)
                        Log.d(TAG, "Marked message $messageId for deletion")
                    } else {
                        Log.w(TAG, "Invalid message ID: $messageId")
                    }
                }
                folder.close(true)
                Log.d(TAG, "Successfully deleted ${messageIds.size} messages from $folderName")
            } catch (e: Exception) {
                folder.close(false)
                throw e
            }
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to delete mail: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun moveMail(
        fromFolder: String = MAIL_DIR_INBOX,
        toFolder: String = MAIL_DIR_INBOX,
        messageIds: List<Int> = emptyList(),
    ) = runCatching {
        Log.d(
            TAG,
            "Connecting to IMAP server at $hostImap:$portImap with SSL to move messages from $fromFolder to $toFolder"
        )
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val sourceFolder = store.getFolder(fromFolder)
            val destinationFolder = store.getFolder(toFolder)
            sourceFolder.open(Folder.READ_WRITE)
            try {
                if (!destinationFolder.exists()) {
                    destinationFolder.create(Folder.HOLDS_MESSAGES)
                    Log.d(TAG, "Created destination folder: $toFolder")
                }
                val messagesToMove = mutableListOf<Message>()
                messageIds.forEach { messageId ->
                    if (messageId > 0 && messageId <= sourceFolder.messageCount) {
                        val message = sourceFolder.getMessage(messageId)
                        messagesToMove.add(message)
                        Log.d(TAG, "Prepared message $messageId for moving")
                    } else {
                        Log.w(TAG, "Invalid message ID: $messageId")
                    }
                }
                if (messagesToMove.isNotEmpty()) {
                    sourceFolder.copyMessages(messagesToMove.toTypedArray(), destinationFolder)
                    Log.d(TAG, "Copied ${messagesToMove.size} messages to $toFolder")
                    messagesToMove.forEach { message ->
                        message.setFlag(Flags.Flag.DELETED, true)
                    }
                    Log.d(TAG, "Marked ${messagesToMove.size} messages for deletion in $fromFolder")
                }
                sourceFolder.close(true)
                Log.d(
                    TAG,
                    "Successfully moved ${messagesToMove.size} messages from $fromFolder to $toFolder"
                )
            } catch (e: Exception) {
                sourceFolder.close(false)
                throw e
            }
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to move mail: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun sendMail(
        from: List<String>,
        to: List<String>,
        subject: String,
        body: String,
        context: Context? = null,
    ) = runCatching {
        Log.d(TAG, "Sending mail via SMTP server at $hostSmtp:$portSmtp")
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.smtp.host", hostSmtp)
            setProperty("mail.smtp.port", portSmtp.toString())
            setProperty("mail.smtp.auth", "true")
            when (portSmtp) {
                465 -> {
                    setProperty("mail.smtp.ssl.enable", "true")
                    setProperty("mail.smtp.starttls.enable", "false")
                }

                587 -> {
                    setProperty("mail.smtp.ssl.enable", "false")
                    setProperty("mail.smtp.starttls.enable", "true")
                }

                25 -> {
                    setProperty("mail.smtp.ssl.enable", "false")
                    setProperty("mail.smtp.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.smtp.ssl.enable", "true")
                    setProperty("mail.smtp.starttls.enable", "true")
                }
            }
            setProperty("mail.smtp.ssl.protocols", "TLSv1.3 TLSv1.2")
            setProperty("mail.smtp.ssl.checkserveridentity", "true")
            remove("mail.smtp.ssl.trust")
        }
        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(userSmtp, passwordSmtp)
        })
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(from.first()))
            to.forEach { recipient ->
                addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
            }
            setSubject(subject)
            setContent(body, "text/html; charset=utf-8")
        }
        Transport.send(message)
        Log.d(TAG, "Mail sent successfully to ${to.joinToString(", ")}")
        if (context != null) {
            to.forEach { recipient ->
                ContactAutoEnricher.enrichFromEmail(
                    context,
                    recipient.substringBefore('@'),
                    recipient,
                    body
                )
            }
            from.forEach { sender ->
                ContactAutoEnricher.enrichFromEmail(
                    context,
                    sender.substringBefore('@'),
                    sender,
                    body
                )
            }
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to send mail: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun checkMailCount(
        folder: String = MAIL_DIR_INBOX
    ): Result<Int> = runCatching {
        Log.d(TAG, "Connecting to IMAP server at $hostImap:$portImap with SSL to check mail count")
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val inbox = store.getFolder(folder)
            inbox.open(Folder.READ_ONLY)
            try {
                val messageCount = inbox.messageCount
                Log.d(TAG, "Found $messageCount messages in INBOX")
                messageCount
            } finally {
                inbox.close(false)
            }
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to check mail count: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun listMailFolders(): Result<List<Folder>> = runCatching {
        Log.d(TAG, "Connecting to IMAP server at $hostImap:$portImap with SSL to list mail folders")
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val defaultFolder = store.defaultFolder
            val folders = defaultFolder.list("*")
            Log.d(TAG, "Found ${folders.size} mail folders:")
            folders.forEach { folder ->
                val folderName = folder.name
                val folderFullName = folder.fullName
                val messageCount = try {
                    if (folder.exists()) {
                        folder.open(Folder.READ_ONLY)
                        val count = folder.messageCount
                        folder.close(false)
                        count
                    } else {
                        0
                    }
                } catch (e: Exception) {
                    0
                }
                Log.d(TAG, "  - $folderName ($folderFullName) - $messageCount messages")
            }
            folders.toList()
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to list mail folders: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun getMailsInFolder(
        folder: String = MAIL_DIR_INBOX
    ): Result<List<Message>> = runCatching {
        Log.d(TAG, "Connecting to IMAP server at $hostImap:$portImap with SSL")
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val inbox = store.getFolder(folder)
            inbox.open(Folder.READ_ONLY)
            try {
                val messages = inbox.messages
                Log.d(TAG, "Found ${messages.size} messages in $folder")
                messages.forEachIndexed { idx, message ->
                    val from = message.from?.firstOrNull()?.toString() ?: "Unknown sender"
                    val subject = message.subject ?: "No subject"
                    Log.d(TAG, "$idx. From: $from | Subject: $subject")
                }
                messages.toList()
            } finally {
                inbox.close(false)
            }
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to check mail: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    suspend fun getMailItemsInFolder(
        folder: String = MAIL_DIR_INBOX
    ): Result<List<MailItem>> = runCatching {
        Log.d(TAG, "Connecting to IMAP server at $hostImap:$portImap with SSL (summaries)")
        val properties = Properties().apply {
            putAll(props)
            setProperty("mail.store.protocol", "imap")
            setProperty("mail.imap.host", hostImap)
            setProperty("mail.imap.port", portImap.toString())
            when (portImap) {
                993 -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "false")
                }

                143 -> {
                    setProperty("mail.imap.ssl.enable", "false")
                    setProperty("mail.imap.starttls.enable", "true")
                }

                else -> {
                    setProperty("mail.imap.ssl.enable", "true")
                    setProperty("mail.imap.starttls.enable", "true")
                }
            }
            setProperty("mail.imap.ssl.trust", "*")
            setProperty("mail.imap.ssl.checkserveridentity", "false")
            setProperty("mail.imap.ssl.protocols", "TLSv1.3 TLSv1.2")
        }
        val session = Session.getInstance(properties)
        val store = session.getStore("imap")
        store.connect(hostImap, portImap, userImap, passwordImap)
        try {
            val inbox = store.getFolder(folder)
            if (!inbox.exists()) {
                return@runCatching emptyList<MailItem>()
            }
            inbox.open(Folder.READ_ONLY)
            try {
                val items = mutableListOf<MailItem>()
                val messages = inbox.messages
                for (message in messages) {
                    val sender = (message.from?.firstOrNull() as? InternetAddress)
                    val senderName = sender?.personal ?: sender?.address ?: ""
                    val senderEmail = sender?.address ?: ""
                    val subjectText = message.subject ?: ""
                    val bodyText = extractTextFromPartSafe(message)
                    val createdAt = (message.sentDate ?: message.receivedDate)?.time
                        ?: System.currentTimeMillis()
                    val recipients = message.getRecipients(Message.RecipientType.TO)?.mapNotNull {
                        (it as? InternetAddress)?.address
                    }?.joinToString(", ") ?: ""
                    val deleted = message.isSet(Flags.Flag.DELETED)
                    val flagged = message.isSet(Flags.Flag.FLAGGED)
                    items.add(
                        MailItem(
                            id = message.messageNumber.toLong(),
                            senderName = senderName,
                            senderEmail = senderEmail,
                            subject = subjectText,
                            body = bodyText,
                            createdAtMillis = createdAt,
                            mailboxName = folder,
                            recipientsCsv = recipients,
                            isDeleted = deleted,
                            isFlagged = flagged
                        )
                    )
                }
                items
            } finally {
                inbox.close(false)
            }
        } finally {
            store.close()
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to read summaries: ${e.message}")
        Log.e(TAG, e.message ?: "error", e)
    }

    private fun extractTextFromPartSafe(
        part: Part
    ): String = runCatching {
        extractTextFromPart(part)
    }.getOrElse {
        Log.e(TAG, "Failed to extract text from part: ${it.message}")
        Log.e(TAG, it.message ?: "error", it)
        ""
    }

    private fun extractTextFromPart(
        part: Part
    ): String = runCatching {
        val contentPart = if (part is MimeMessage) {
            val bos = ByteArrayOutputStream()
            part.writeTo(bos)
            val inputStream = ByteArrayInputStream(bos.toByteArray())
            MimeMessage(part.session, inputStream)
        } else part

        when {
            contentPart.isMimeType("text/plain") -> {
                (contentPart.content as? String) ?: ""
            }

            contentPart.isMimeType("text/html") -> {
                (contentPart.content as? String)
                    ?.replace("<[^>]+>".toRegex(), " ")
                    ?: ""
            }

            contentPart.isMimeType("multipart/*") -> {
                val mp = contentPart.content as? Multipart
                if (mp != null) {
                    buildString {
                        for (i in 0 until mp.count) {
                            val bodyPart = mp.getBodyPart(i)
                            val t = extractTextFromPart(bodyPart)
                            if (t.isNotBlank()) {
                                append(t)
                                if (this.length > 4096) break
                            }
                        }
                    }
                } else ""
            }

            else -> {
                contentPart.content as? String ?: ""
            }
        }
    }.getOrElse { mex ->
        Log.e(TAG, "MessagingException during extractTextFromPart: ${mex.message}")
        ""
    }

    companion object {
        val TAG = MailClient::class.simpleName
        const val MAIL_DIR_INBOX = "INBOX"
    }
}
