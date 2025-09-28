package org.mjdev.safedialer.providers.custom.email

import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.sync.emails.ProviderEmails

data class MailFolder(
    @FieldMapping(
        ProviderEmails.MAIL_FOLDER_NAME,
        FieldMapping.PhysicalType.String
    )
    val name: String = ""
) : Entity()