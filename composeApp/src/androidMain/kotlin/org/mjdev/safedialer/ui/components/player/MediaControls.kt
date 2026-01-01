package org.mjdev.safedialer.ui.components.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun MediaControls(
    isPlaying: Boolean = false,
    isShuffleEnabled: Boolean = false,
    onPlayPauseClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onShuffleClick: () -> Unit = {},
    onQueueClick: () -> Unit = {}
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
) {
    IconButton(onClick = onShuffleClick) {
        Icon(
            imageVector = Icons.Default.Shuffle,
            contentDescription = "Shuffle",
            tint = if (isShuffleEnabled) Color.White else
                Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
    }
    IconButton(onClick = onPreviousClick) {
        Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "Previous",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
    IconButton(onClick = onPlayPauseClick) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause
            else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
    IconButton(onClick = onNextClick) {
        Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Next",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
    IconButton(onClick = onQueueClick) {
        Icon(
            imageVector = Icons.Filled.Queue,
            contentDescription = "Queue",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
