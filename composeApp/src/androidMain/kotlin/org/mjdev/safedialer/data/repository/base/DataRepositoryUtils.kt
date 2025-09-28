package org.mjdev.safedialer.data.repository.base


object DataRepositoryUtils {

//    fun IDataRepository.findContactByPhone(
//        caller: String?
//    ): kotlinx.coroutines.flow.Flow<ContactModel> {
//        val phone = runCatching {
//            pnu.parse(caller, null)
//        }.getOrNull()?.nationalNumber?.toString()
//        return rawContacts.map { allContacts ->
//            when {
//                phone == null -> createContact(caller)
//                else -> {
//                    val found: List<ContactModel> = allContacts.filter { entry ->
//                        val hasPhone = entry.phone?.contains(phone, true) ?: false
//                        val hsName = entry.displayName?.contains(phone, true) ?: false
//                        hasPhone || hsName
//                    }.map { contact ->
//                        ContactModel(
//                            contactId = contact.id,
//                            displayName = contact.displayName ?: "",
//                            phoneNumber = contact.phone ?: "",
//                            photoThumbnailUri = contact.uriPhoto,
//                            photoUri = contact.uriPhoto,
//                            isBlocked = false, // todo
//                        )
//                    }
//                    found.let { cts ->
//                        when {
//                            cts.isEmpty() -> createContact(phone)
//                            (cts.size == 1) -> found.first()
//                            else -> cts.firstOrNull { fc ->
//                                fc.phoneNumber.removeWhites().contentEquals(phone, true)
//                            } ?: createContact(phone)
//                        }
//                    }
//                }
//            }
//        }
//    }

//    fun IDataRepository.findContactBySender(
//        email: String?,
//        senderName: String?
//    ): kotlinx.coroutines.flow.Flow<ContactModel> {
//        val sender = email ?: senderName
//        return rawContacts.map { allContacts ->
//            when {
//                sender == null -> createContact(sender)
//                else -> {
//                    val found: List<ContactModel> = allContacts.filter { entry ->
//                        val hasEmail = entry.email?.contains(sender, true) ?: false
//                        val hasName = entry.displayName?.contentEquals(senderName, true) ?: false
//                        hasEmail || hasName
//                    }.map { contact ->
//                        ContactModel(
//                            contactId = contact.id,
//                            displayName = contact.displayName ?: "",
//                            phoneNumber = contact.phone ?: "",
//                            photoThumbnailUri = contact.uriPhoto,
//                            photoUri = contact.uriPhoto,
//                            isBlocked = false, // todo
//                        )
//                    }
//                    found.let { cts ->
//                        when {
//                            cts.isEmpty() -> createContact(sender)
//                            (cts.size == 1) -> found.first()
//                            else -> cts.firstOrNull { fp ->
//                                // todo
//                                fp.phoneNumber.removeWhites().contentEquals(sender, true)
//                            } ?: createContact(sender)
//                        }
//                    }
//                }
//            }
//        }
//    }

//    private fun String.removeWhites() = replace("-", "")
//        .replace(" ", "")
//        .trim()
}