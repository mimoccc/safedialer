package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun NavigationActions(
    showActions: Boolean = true,
    onServeClick: () -> Unit = {}
) {
    if (showActions) {
        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = onServeClick
        ) {
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .padding(4.dp),
                imageVector = Icons.Filled.Computer,
                contentDescription = "",
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = {
                // todo
            }
        ) {
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .padding(4.dp),
                imageVector = Icons.Filled.QrCodeScanner,
                contentDescription = "",
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = {
                // todo
            }
        ) {
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .padding(4.dp),
                imageVector = Icons.Filled.Settings,
                contentDescription = "",
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}