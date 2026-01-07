package org.mjdev.safedialer.widget.media.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.flow
import org.mjdev.safedialer.service.media.MediaService
import org.mjdev.safedialer.service.media.MediaService.Companion.EmptyState
import org.mjdev.safedialer.widget.media.actions.MediaPlayerActions
import org.mjdev.safedialer.widget.base.previews.GlancePreviews
import org.mjdev.safedialer.widget.base.components.ControlButton

@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun ControlsSection() {
    val service = remember { MediaService.getInstance() }
    val state by remember(service?.playbackState) {
        service?.playbackState ?: flow { emit(EmptyState) }
    }.collectAsState(EmptyState)
    val isPlaying = remember (state, state.isPlaying) {
        state.isPlaying
    }
    val isShuffleEnabled = remember(state, state.isShuffleEnabled) {
        state.isShuffleEnabled
    }
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            imageVector = Icons.Filled.Shuffle,
            action = MediaPlayerActions.toggleShuffle(),
            tint = if (isShuffleEnabled) Color.White else Color.White.copy(alpha = 0.5f),
            size = 24.dp,
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        ControlButton(
            imageVector = Icons.Filled.SkipPrevious,
            action = MediaPlayerActions.previous(),
            size = 32.dp,
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        ControlButton(
            imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            action = if (isPlaying) MediaPlayerActions.pause() else MediaPlayerActions.play(),
            size = 48.dp,
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        ControlButton(
            imageVector = Icons.Filled.SkipNext,
            action = MediaPlayerActions.next(),
            size = 32.dp,
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        ControlButton(
            imageVector = Icons.Filled.Share,
            action = MediaPlayerActions.shareItem(),
            size = 24.dp,
        )
    }
}
