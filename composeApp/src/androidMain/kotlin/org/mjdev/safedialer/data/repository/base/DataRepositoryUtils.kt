package org.mjdev.safedialer.data.repository.base

import org.mjdev.safedialer.data.model.ContactModel

object DataRepositoryUtils {

    private fun createContact(caller: String?): ContactModel = ContactModel(
        displayName = caller ?: "",
        phoneNumber = caller ?: "",
    )

    fun IDataRepository.findContactByPhone(
        caller: String?
    ): ContactModel {
        val phone = runCatching {
            pnu.parse(caller, null)
        }.getOrNull()?.nationalNumber?.toString()
        return when {
            phone == null -> createContact(caller)
            else -> {
                val found: List<ContactModel> = contactSearchMap.filter { entry ->
                    entry.key.contains(phone, true) ||
                            entry.value.displayName.contains(phone, true)
                }.map { entry ->
                    entry.value
                }
                found.let { fphones ->
                    when {
                        fphones.isEmpty() -> createContact(caller)
                        (fphones.size == 1) -> found.first()
                        else -> fphones.firstOrNull { fp ->
                            fp.phoneNumber.removeWhites().contentEquals(phone, true)
                        } ?: createContact(caller)
                    }
                }
            }
        }
    }

    fun IDataRepository.findContactBySender(
        email: String?,
        senderName: String?
    ): ContactModel {
        val sender = email ?: senderName
        return when {
            sender == null -> createContact(sender)
            else -> {
                val found: List<ContactModel> = contactSearchMap.filter { entry ->
                    entry.value.emails.any { email ->
                        email.contains(email, true)
                    } || entry.value.displayName.contentEquals(senderName, true)
                }.map { entry ->
                    entry.value
                }
                found.let { fphones ->
                    when {
                        fphones.isEmpty() -> createContact(sender)
                        (fphones.size == 1) -> found.first()
                        else -> fphones.firstOrNull { fp ->
                            fp.phoneNumber.removeWhites().contentEquals(sender, true)
                        } ?: createContact(sender)
                    }
                }
            }
        }
    }

    private fun String.removeWhites() = replace("-", "")
        .replace(" ", "")
        .trim()
}