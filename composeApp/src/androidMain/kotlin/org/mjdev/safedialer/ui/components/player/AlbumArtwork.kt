package org.mjdev.safedialer.ui.components.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun AlbumArtwork(
    modifier: Modifier = Modifier,
    albumArt: Painter? = null
) = Surface(
    modifier = modifier,
    shape = RoundedCornerShape(32.dp),
    color = Color(0xFF4A4A4A)
) {
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        if (albumArt != null) {
            Image(
                painter = albumArt,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
