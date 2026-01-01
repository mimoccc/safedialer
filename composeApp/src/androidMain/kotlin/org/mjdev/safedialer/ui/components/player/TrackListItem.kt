package org.mjdev.safedialer.ui.components.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun TrackListItem(
    modifier: Modifier = Modifier,
    track: Track? = null,
    isCurrentTrack: Boolean = true
) = Box(
    modifier = modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = track?.title ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isCurrentTrack) Color(0xFF6699FF)
                else Color.White,
                fontWeight = if (isCurrentTrack) FontWeight.SemiBold
                else FontWeight.Normal
            )
            Text(
                text = track?.artist ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = if (isCurrentTrack) Color(0xFF6699FF).copy(alpha = 0.7f)
                else Color.White.copy(
                    alpha = 0.6f
                )
            )
        }
        if (isCurrentTrack) {
            Icon(
                imageVector = Icons.Filled.Equalizer,
                contentDescription = "Playing",
                tint = Color(0xFF6699FF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
