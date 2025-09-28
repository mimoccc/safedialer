package org.mjdev.safedialer.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Mail
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import org.mjdev.safedialer.ui.tabs.TabCallLog
import org.mjdev.safedialer.ui.tabs.TabContactList
import org.mjdev.safedialer.ui.tabs.TabEmails
import org.mjdev.safedialer.ui.tabs.TabMessages

enum class Tabs(
    val title: String,
    val icon: ImageVector = Icons.Default.Apps,
    val content: @Composable (scrollState: LazyListState, filterText: MutableState<String>) -> Unit,
) {
    CallLog(
        title = "Calls",
        icon = Icons.Default.Call,
        content = { ss, ft ->
            TabCallLog(ss, ft)
        }
    ),
    Contacts(
        title = "Contacts",
        icon = Icons.Default.ContactPhone,
        content = { ss, ft ->
            TabContactList(ss, ft)
        }
    ),
    Messages(
        title = "Messages",
        icon = Icons.AutoMirrored.Filled.Message,
        content = { ss, ft ->
            TabMessages(ss, ft)
        }
    ),
    Emails(
        title = "Emails",
        icon = Icons.Default.Mail,
        content = { ss, ft ->
            TabEmails(ss, ft)
        }
    );

    override fun toString(): String = title
}
