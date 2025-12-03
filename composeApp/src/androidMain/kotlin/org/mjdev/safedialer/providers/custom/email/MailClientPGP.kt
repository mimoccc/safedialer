package org.mjdev.safedialer.providers.custom.email

import org.mjdev.safedialer.BuildConfig
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
import java.util.Properties
import android.content.Context
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMultipart
import org.mjdev.safedialer.helpers.ContactAutoEnricher
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class MailClientPGP(
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
    val pgpPassword: String = "",
    val pgpPublicKeyData: ByteArray = ByteArray(0),
    val recipientPublicKeys: Map<String, ByteArray> = emptyMap()
) {
    private val isPGPEnabled: Boolean
        get() = pgpCertData.isNotEmpty() && pgpPassword.isNotEmpty()

    suspend fun sendMail(
        from: List<String>,
        to: List<String>,
        subject: String,
        body: String,
        context: Context? = null,
        encrypt: Boolean = true,
        sign: Boolean = true,
        attachments: List<File> = emptyList()
    ) = runCatching {
        Log.d(TAG, "Sending PGP/MIME mail via SMTP server at $hostSmtp:$portSmtp")
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
        var message = MimeMessage(session).apply {
            setFrom(InternetAddress(from.first()))
            to.forEach { recipient ->
                addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
            }
            setSubject(subject)
            if (attachments.isEmpty()) {
                setContent(body, "text/html; charset=utf-8")
            } else {
                val multipart = MimeMultipart()
                val textPart = MimeBodyPart().apply {
                    setContent(body, "text/html; charset=utf-8")
                }
                multipart.addBodyPart(textPart)
                attachments.forEach { file ->
                    val attachmentPart = MimeBodyPart().apply {
                        attachFile(file)
                    }
                    multipart.addBodyPart(attachmentPart)
                }
                setContent(multipart)
            }
            saveChanges()
        }
        if (isPGPEnabled) {
            if (sign) {
                message = try {
                    PGPMimeHelper.signMimeMessage(message, pgpCertData, pgpPassword)
                } catch (e: Exception) {
                    Log.e(TAG, "PGP signing failed: ${e.message}", e)
                    message
                }
            }
            if (encrypt) {
                val recipientKeys = to.mapNotNull { email ->
                    recipientPublicKeys[email]
                }
                if (recipientKeys.isNotEmpty()) {
                    message = try {
                        PGPMimeHelper.encryptMimeMessage(message, recipientKeys)
                    } catch (e: Exception) {
                        Log.e(TAG, "PGP encryption failed: ${e.message}", e)
                        message
                    }
                } else {
                    Log.w(TAG, "No public keys found for recipients, sending unencrypted")
                }
            }
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
                    val processedMessage = if (isPGPEnabled && isPGPEncrypted(message)) {
                        try {
                            PGPMimeHelper.decryptMimeMessage(
                                message as MimeMessage,
                                pgpCertData,
                                pgpPassword
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to decrypt message: ${e.message}", e)
                            message
                        }
                    } else message
                    val sender = (processedMessage.from?.firstOrNull() as? InternetAddress)
                    val senderName = sender?.personal ?: sender?.address ?: ""
                    val senderEmail = sender?.address ?: ""
                    val subjectText = processedMessage.subject ?: ""
                    val bodyText = extractTextFromPartSafe(processedMessage)
                    val createdAt = (processedMessage.sentDate ?: processedMessage.receivedDate)?.time
                        ?: System.currentTimeMillis()
                    val recipients = processedMessage.getRecipients(Message.RecipientType.TO)?.mapNotNull {
                        (it as? InternetAddress)?.address
                    }?.joinToString(", ") ?: ""
                    val deleted = processedMessage.isSet(Flags.Flag.DELETED)
                    val flagged = processedMessage.isSet(Flags.Flag.FLAGGED)
                    items.add(
                        MailItem(
                            id = processedMessage.messageNumber.toLong(),
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

    private fun isPGPEncrypted(message: Message): Boolean = runCatching {
        val content = message.content
        if (content is MimeMultipart) {
            val contentType = message.contentType
            return contentType.contains("multipart/encrypted") &&
                   contentType.contains("application/pgp-encrypted")
        }
        false
    }.getOrDefault(false)

    private fun extractTextFromPartSafe(part: Part): String = runCatching {
        val text = extractTextFromPart(part)
        text
    }.getOrElse {
        Log.e(TAG, "Failed to extract text from part: ${it.message}")
        ""
    }

    private fun extractTextFromPart(part: Part): String = runCatching {
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
        val TAG = MailClientPGP::class.simpleName
        const val MAIL_DIR_INBOX = "INBOX"
    }
}