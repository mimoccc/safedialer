package org.mjdev.safedialer.data.model

import org.mjdev.safedialer.data.list.IListItem

data class MessageModel(
    override val phoneNumber: String = "+420 702 568 909",
    override val displayName: String = "Milan Jurkulák",
    override val date: Long = 0L,
    val smsId: String? = null,
    val contact: ContactModel? = null,
) : IListItem