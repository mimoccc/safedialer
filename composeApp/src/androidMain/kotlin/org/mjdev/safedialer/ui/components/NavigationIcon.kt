package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@Composable
fun NavigationIcon(
    onClick: () -> Unit = {},
) {
    IconButton(
        modifier = Modifier.size(36.dp),
        onClick = onClick
    ) {
        Image(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .padding(4.dp),
            imageVector = Icons.Filled.Phone,
            contentDescription = "",
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}