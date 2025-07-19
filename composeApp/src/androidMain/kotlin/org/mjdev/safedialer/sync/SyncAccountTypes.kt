package org.mjdev.safedialer.sync

import org.mjdev.safedialer.R

enum class SyncAccountTypes(
    val authority: Int
) {
    CALENDAR(R.string.authority_calendar),
    CALL_LOG(R.string.authority_call_log),
    CONTACTS(R.string.authority_contacts),
    EMAILS(R.string.authority_emails),
    GALLERY(R.string.authority_gallery),
    TASKS(R.string.authority_tasks)
}