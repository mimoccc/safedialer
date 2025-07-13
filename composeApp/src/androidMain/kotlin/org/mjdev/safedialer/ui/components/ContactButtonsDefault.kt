package org.mjdev.safedialer.ui.components

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.data.list.ListItem

@Preview
@Composable
fun ContactButtonsDefault(
    contact: ListItem? = ListItem(
        contactId = "+420702568909",
        phoneNumber = "+420702568909",
        displayName = "Milan Jurkulák",
        callId = "+420702568909"
    ),
    iconSize: Dp = 32.dp,
    context: Context = LocalContext.current,
    phoneNumber: String = contact?.phoneNumber ?: ""
) = Row {
    if (contact?.phoneNumber != null) {
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:$phoneNumber")
                ).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
        ) {
            Image(
                imageVector = Icons.Filled.Call,
                contentDescription = "",
                modifier = Modifier.padding(4.dp),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("sms:$phoneNumber")
                ).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        ) {
            Image(
                imageVector = Icons.Filled.Email,
                contentDescription = "",
                modifier = Modifier.padding(4.dp),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = {
                // todo block
            }
        ) {
            Image(
                imageVector = if (contact.isBlocked) Icons.Filled.FavoriteBorder
                else Icons.Filled.Favorite,
                contentDescription = "",
                modifier = Modifier.padding(4.dp),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
    if (contact?.contactId != null) {
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_URI,
                        java.lang.String.valueOf(contact?.contactId ?: "---")
                    )
                ).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        ) {
            Image(
                imageVector = Icons.Filled.ContactPhone,
                contentDescription = "",
                modifier = Modifier.padding(4.dp),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
    if (contact?.callId != null || contact?.contactId != null) {
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = {
                // todo : delete
            }
        ) {
            Image(
                imageVector = Icons.Filled.Delete,
                contentDescription = "",
                modifier = Modifier.padding(4.dp),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
