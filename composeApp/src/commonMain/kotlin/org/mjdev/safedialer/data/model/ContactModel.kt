package org.mjdev.safedialer.data.model

import org.mjdev.safedialer.data.list.IListItem

data class ContactModel(
    override val phoneNumber: String = "+420 702 568 909",
    override val displayName: String = "Milan Jurkulák",
    override val date:Long = 0L,
    val contactId: String? = null,
    val photoThumbnailUri: String? = null,
    val photoUri: String? = null,
    val isBlocked: Boolean = false,
    val isDanger: Boolean = false,
    val isFine: Boolean = true,
) : IListItem