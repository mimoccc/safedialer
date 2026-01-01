package org.mjdev.safedialer.widget.media.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import org.mjdev.safedialer.widget.media.actions.MediaPlayerActions
import org.mjdev.safedialer.widget.base.GlancePreviews
import org.mjdev.safedialer.widget.base.components.ControlButton

@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun ControlsSection(
    isPlaying: Boolean = false,
    isShuffleEnabled: Boolean = false,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            imageVector = Icons.Filled.ShuffleOn,
            action = MediaPlayerActions.toggleShuffle(),
            tint = if (isShuffleEnabled) Color.White else Color.White.copy(alpha = 0.5f)
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        ControlButton(
            imageVector = Icons.Filled.SkipPrevious,
            action = MediaPlayerActions.previous()
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        ControlButton(
            imageVector = if (isPlaying) Icons.Filled.PlayArrow else Icons.Filled.Stop,
            action = MediaPlayerActions.playPause(),
            size = 48.dp
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        ControlButton(
            imageVector = Icons.Filled.SkipNext,
            action = MediaPlayerActions.next()
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        ControlButton(
            imageVector = Icons.Filled.Queue,
            action = MediaPlayerActions.openQueue()
        )
    }
}
