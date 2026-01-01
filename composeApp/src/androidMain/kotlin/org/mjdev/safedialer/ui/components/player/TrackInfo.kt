package org.mjdev.safedialer.ui.components.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun TrackInfo(
    modifier: Modifier = Modifier,
    trackTitle: String = "Track title",
    artistName: String = "Artist"
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(4.dp)
) {
    Text(
        text = trackTitle,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = artistName,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.7f)
    )
}