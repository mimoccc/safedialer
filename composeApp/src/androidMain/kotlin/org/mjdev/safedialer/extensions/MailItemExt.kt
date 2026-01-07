package org.mjdev.safedialer.extensions

import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import org.mjdev.safedialer.extensions.StringExt.htmlToText
import org.mjdev.safedialer.providers.custom.email.MailItem
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Properties

object MailItemExt {

    fun parseMail(
        path: String,
        mailData: ByteArray
    ): MailItem {
        val session = Session.getInstance(Properties())
        val message = MimeMessage(session, ByteArrayInputStream(mailData))
        val from = (message.from?.firstOrNull() as? InternetAddress)
        val senderName = from?.personal ?: from?.address.orEmpty()
        val senderEmail = from?.address.orEmpty()
        val subject = message.subject.orEmpty()
        val createdAtMillis = (message.sentDate ?: message.receivedDate)?.time ?: 0L
        val recipientsCsv = buildList {
            addAll(addressesToList(message.getRecipients(Message.RecipientType.TO)))
            addAll(addressesToList(message.getRecipients(Message.RecipientType.CC)))
            addAll(addressesToList(message.getRecipients(Message.RecipientType.BCC)))
        }.distinct().joinToString(",")
//        val body = extractText(message) // todo mail body
        return MailItem(
            senderName = senderName,
            senderEmail = senderEmail,
            subject = subject,
            fileUri = path,
            createdAtMillis = createdAtMillis,
            mailboxName = "",
            recipients = recipientsCsv,
            isDeleted = false,
            isFlagged = false,
            isEncrypted = false,
            contact = null
        )
    }

    fun addressesToList(
        addresses: Array<jakarta.mail.Address>?
    ): List<String> = addresses?.mapNotNull { addr ->
        (addr as? InternetAddress)?.address
    } ?: emptyList()

    fun extractText(
        part: Part
    ): String {
        return try {
            val contentType = runCatching { part.contentType }.getOrNull()
            val content = runCatching { part.content }.getOrNull()
            when {
                part.isMimeType("text/plain") -> contentToString(
                    content,
                    contentType,
                    isHtml = false
                )

                part.isMimeType("text/html") -> contentToString(
                    content,
                    contentType,
                    isHtml = true
                )

                part.isMimeType("multipart/*") -> {
                    // Never cast directly; handle actual runtime type safely
                    when (content) {
                        is Multipart -> processMultipart(content)
                        is InputStream -> {
                            val ds = ByteArrayDataSource(
                                content.readBytes(),
                                contentType ?: "multipart/mixed"
                            )
                            processMultipart(MimeMultipart(ds))
                        }

                        is Message -> extractText(content)
                        is Part -> extractText(content)
                        is String -> content // odd case, but be tolerant
                        else -> ""
                    }
                }

                part.isMimeType("message/rfc822") -> {
                    when (content) {
                        is Message -> extractText(content)
                        is InputStream -> extractText(
                            MimeMessage(Session.getInstance(Properties()), content)
                        )

                        else -> ""
                    }
                }

                else -> {
                    when (content) {
                        is String -> contentToString(content, contentType, isHtml = false)
                        is InputStream -> readToString(content, parseCharset(contentType))
                        is Message -> extractText(content)
                        is Part -> extractText(content)
                        else -> ""
                    }
                }
            }.trim()
        } catch (e: Throwable) {
            e.printStackTrace()
            ""
        }
    }

    fun processMultipart(
        mp: Multipart
    ): String {
        val ct = (mp as? MimeMultipart)?.contentType ?: runCatching {
            mp.contentType
        }.getOrNull()
        val isAlternative = ct?.startsWith("multipart/alternative", ignoreCase = true) == true
        var textPlain: String? = null
        var textHtml: String? = null
        for (i in 0 until mp.count) {
            val bp = mp.getBodyPart(i)
            // Skip attachments when possible
            if (bp.disposition.isAttachmentDisposition()) continue
            val text = extractText(bp)
            when {
                bp.isMimeType("text/plain") && textPlain.isNullOrEmpty() ->
                    textPlain = text

                bp.isMimeType("text/html") && textHtml.isNullOrEmpty() ->
                    textHtml = text
                // For nested multiparts, extractText already resolved best effort
            }
            if (!textPlain.isNullOrEmpty() && isAlternative) break
        }
        return textPlain ?: textHtml ?: ""
    }

    fun contentToString(
        content: Any?,
        contentType: String?,
        isHtml: Boolean
    ): String = when (content) {
        is String -> if (isHtml) content.htmlToText() else content

        is InputStream -> {
            val txt = readToString(content, parseCharset(contentType))
            if (isHtml) txt.htmlToText() else txt
        }

        else -> content?.toString() ?: ""
    }

    fun String?.isAttachmentDisposition(): Boolean =
        this?.equals(Part.ATTACHMENT, ignoreCase = true) == true

    fun parseCharset(
        contentType: String?
    ): Charset {
        if (contentType.isNullOrBlank()) return StandardCharsets.UTF_8
        val regex = Regex("charset=([^;]+)", RegexOption.IGNORE_CASE)
        val match = regex.find(contentType)
        val raw = match?.groupValues?.getOrNull(1)?.trim()?.trim('"', '\'')
        return runCatching {
            if (!raw.isNullOrBlank()) Charset.forName(raw) else StandardCharsets.UTF_8
        }.getOrElse {
            StandardCharsets.UTF_8
        }
    }

    fun readToString(
        input: InputStream,
        charset: Charset
    ): String = input.bufferedReader(charset).use {
        it.readText()
    }

}
