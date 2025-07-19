package org.mjdev.safedialer.email

data class MailItem(
    val id: Long,
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val body: String,
    val createdAtMillis: Long,
    val mailboxName: String,
    val recipientsCsv: String,
    val isDeleted: Boolean,
    val isFlagged: Boolean
)
