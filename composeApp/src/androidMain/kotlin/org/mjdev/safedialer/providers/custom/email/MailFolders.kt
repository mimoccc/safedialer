package org.mjdev.safedialer.providers.custom.email

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MailFolders(
    val boxName: String,
    val serverName: String,
    val icon: ImageVector,
) {
    object INBOX : MailFolders(
        boxName = "Inbox",
        serverName = "INBOX",
        icon = Icons.Default.Inbox,
    )
}