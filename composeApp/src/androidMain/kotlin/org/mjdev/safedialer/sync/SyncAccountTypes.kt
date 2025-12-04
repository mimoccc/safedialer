package org.mjdev.safedialer.sync

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.VoiceChat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import org.mjdev.safedialer.R
import org.mjdev.safedialer.ui.tabs.TabAI
import org.mjdev.safedialer.ui.tabs.TabContactList
import org.mjdev.safedialer.ui.tabs.TabEmails
import org.mjdev.safedialer.ui.tabs.TabMessages

enum class SyncAccountTypes(
    val priority: Int = 0,
    val needLogon:Boolean = false,
    val needDetail:Boolean = false,
    val authority: Int,
    val titleResId: Int = -1,
    val icon: ImageVector = Icons.Default.Apps,
    val searchable: Boolean = true,
    val content: @Composable (scrollState: LazyListState, filterText: MutableState<String>) -> Unit,
) {
    CALL_LOG(
        priority = 0,
        authority = R.string.authority_call_log,
        titleResId = R.string.sync_label_call_log,
        icon = Icons.Default.Call,
        searchable = true,
        needLogon = false,
        needDetail = true,
        content = { ss, ft ->
            org.mjdev.safedialer.ui.tabs.TabCallLog(ss, ft)
        }
    ),
    CONTACTS(
        priority = 1,
        authority = R.string.authority_contacts,
        titleResId = R.string.sync_label_contacts,
        icon = Icons.Default.ContactPhone,
        needLogon = false,
        needDetail = true,
        content = { ss, ft ->
            TabContactList(ss, ft)
        }
    ),
    MESSAGES(
        priority = 2,
        authority = R.string.authority_messages,
        titleResId = R.string.sync_label_messages,
        icon = Icons.AutoMirrored.Filled.Message,
        searchable = true,
        needLogon = false,
        needDetail = true,
        content = { ss, ft ->
            TabMessages(ss, ft)
        }
    ),
    EMAILS(
        priority = 3,
        authority = R.string.authority_emails,
        titleResId = R.string.sync_label_emails,
        icon = Icons.Default.Mail,
        searchable = true,
        needLogon = true,
        needDetail = true,
        content = { ss, ft ->
            TabEmails(ss, ft)
        }
    ),
    CALENDAR(
        priority = 4,
        authority = R.string.authority_calendar,
        titleResId = R.string.sync_label_calendar,
        icon = Icons.Default.CalendarMonth,
        searchable = false,
        needLogon = true,
        needDetail = true,
        content = { ss, ft ->
            // todo
        }
    ),
    TASKS(
        priority = 5,
        authority = R.string.authority_tasks,
        titleResId = R.string.sync_label_tasks,
        icon = Icons.Default.TaskAlt,
        searchable = true,
        needLogon = true,
        needDetail = true,
        content = { ss, ft ->
            // todo
        }
    ),
    INVOICES(
        priority = 6,
        authority = R.string.authority_invoices,
        titleResId = R.string.sync_label_invoices,
        icon = Icons.Default.Newspaper,
        searchable = true,
        needLogon = true,
        needDetail = true,
        content = { ss, ft ->
            // todo
        }
    ),
    AI(
        priority = 7,
        authority = R.string.authority_ai,
        titleResId = R.string.sync_label_ai,
        icon = Icons.Default.VoiceChat,
        searchable = false,
        needLogon = true,
        needDetail = false,
        content = { ss, ft ->
            TabAI(ss, ft)
        }
    ),
    GALLERY(
        priority = 8,
        authority = R.string.authority_gallery,
        titleResId = R.string.sync_label_gallery,
        icon = Icons.Default.Image,
        searchable = false,
        needLogon = true,
        needDetail = false,
        content = { ss, ft ->
            // todo
        }
    ),
    NOTES(
        priority = 9,
        authority = R.string.authority_notes,
        titleResId = R.string.sync_label_notes,
        icon = Icons.Default.NoteAlt,
        searchable = true,
        needLogon = true,
        needDetail = true,
        content = { ss, ft ->
            // todo
        }
    ),
    AUTHENTICATOR(
        priority = 9,
        authority = R.string.authority_authenticator,
        titleResId = R.string.sync_label_authenticator,
        icon = Icons.Default.Person,
        searchable = true,
        needLogon = true,
        needDetail = true,
        content = { ss, ft ->
            // todo
        }
    ),
    ;

    companion object {
        val Enum<*>.titleResId
            get() = if (this is SyncAccountTypes) this.titleResId else ordinal
    }
}