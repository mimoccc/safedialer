package org.mjdev.safedialer.ui.components.navigation

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Previews
@Composable
fun NavigationIcon(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    imageBitmap: ImageBitmap? = null,
    icon: ImageVector = Icons.Filled.Phone,
    onClick: () -> Unit = {}
) = AppTheme {
    IconButton(
        modifier = modifier.size(size),
        onClick = onClick,
    ) {
        if (imageBitmap != null) {
            Image(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape,
                    )
                    .size(size)
                    .padding(4.dp)
                    .clip(CircleShape),
                bitmap = imageBitmap,
                contentDescription = ""
            )
        } else {
            Image(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape,
                    )
                    .size(size)
                    .padding(4.dp),
                imageVector = icon,
                contentDescription = "",
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary,
                )
            )
        }
    }
}
