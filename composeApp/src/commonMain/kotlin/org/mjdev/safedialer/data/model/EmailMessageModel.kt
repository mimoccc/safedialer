package org.mjdev.safedialer.data.model

import org.mjdev.safedialer.data.list.IListItem

data class EmailMessageModel(
    override val phoneNumber: String = "",
    override val displayName: String = "",
    override val date: Long = 0L,
    val id: Long = 0L,
    val contact: ContactModel? = null,
    val senderName: String = "",
    val senderEmail: String = "",
    val subject: String = "",
    val body: String = "",
    val mailboxName: String = "",
    val recipientsCsv: String = "",
    val recipients : List<String> = emptyList(),
    val isDeleted: Boolean = false,
    val isFlagged: Boolean = false
)  : IListItem
