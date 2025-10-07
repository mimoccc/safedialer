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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun ContactButtonsDefault(
    item: ListItem? = if (isInPreviewMode) ListItem.PREVIEW else null,
    iconSize: Dp = 32.dp,
    context: Context = LocalContext.current,
) = Row {
    if (item?.itemPhone?.isNotEmpty() == true || isInPreviewMode) {
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:${item?.itemPhone}")
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
                    Uri.parse("sms:${item?.itemPhone}")
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
                imageVector = if (item?.isBlocked == true) Icons.Filled.FavoriteBorder
                else Icons.Filled.Favorite,
                contentDescription = "",
                modifier = Modifier.padding(4.dp),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
    if (item?.itemId != null) {
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_URI,
                        item.itemId.toString()
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
    if (item?.itemId != null) {
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
