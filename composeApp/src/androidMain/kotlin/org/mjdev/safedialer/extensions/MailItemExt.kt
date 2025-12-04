package org.mjdev.safedialer.extensions

import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.Part
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import org.mjdev.safedialer.providers.custom.email.MailItem
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Properties

object MailItemExt {

    fun parseMail(
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
        val body = extractText(message)
        return MailItem(
            senderName = senderName,
            senderEmail = senderEmail,
            subject = subject,
            body = body,
            createdAtMillis = createdAtMillis,
            mailboxName = "",
            recipients = recipientsCsv,
            isDeleted = false,
            isFlagged = false,
            isEncrypted = false,
            contact = null
        )
    }

    private fun addressesToList(
        addresses: Array<jakarta.mail.Address>?
    ): List<String> = addresses?.mapNotNull { addr ->
        (addr as? InternetAddress)?.address
    } ?: emptyList()

    private fun extractText(
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

    private fun processMultipart(
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

    private fun contentToString(
        content: Any?,
        contentType: String?,
        isHtml: Boolean
    ): String = when (content) {
        is String -> if (isHtml) htmlToText(content) else content
        is InputStream -> {
            val txt = readToString(content, parseCharset(contentType))
            if (isHtml) htmlToText(txt) else txt
        }

        else -> content?.toString() ?: ""
    }

    private fun String?.isAttachmentDisposition(): Boolean =
        this?.equals(Part.ATTACHMENT, ignoreCase = true) == true

    private fun parseCharset(contentType: String?): Charset {
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

    private fun readToString(
        input: InputStream,
        charset: Charset
    ): String = input.bufferedReader(charset).use {
        it.readText()
    }

    private fun htmlToText(
        html: String
    ): String {
        // very simple HTML tag stripper; avoid bringing dependencies here
        return html
            .replace(Regex("<br ?/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n\n")
            .replace(Regex("<[^>]+>"), "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }

}