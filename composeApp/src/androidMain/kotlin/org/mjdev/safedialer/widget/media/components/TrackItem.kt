package org.mjdev.safedialer.widget.media.components

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.flow
import org.mjdev.safedialer.service.media.MediaService
import org.mjdev.safedialer.service.media.MediaService.Companion.EmptyState
import org.mjdev.safedialer.widget.base.extensions.GlanceComposeExt.ActionEmpty
import org.mjdev.safedialer.widget.base.extensions.GlanceComposeExt.rememberDerivedState
import org.mjdev.safedialer.widget.base.previews.GlancePreviews
import org.mjdev.safedialer.widget.base.vector.ImageVectorProvider.rememberVectorImageProvider

@OptIn(ExperimentalGlancePreviewApi::class)
@SuppressLint("RestrictedApi")
@GlancePreviews
@GlanceComposable
@Composable
fun TrackItem(
    modifier: GlanceModifier = GlanceModifier,
    index: Int = 0,
    selectedColor: Color = Color(0xFF6699FF),
    unSelectedColor: Color = Color.Gray,
    track: MediaItem,
    onClick: Action = ActionEmpty,
) = Box(
    modifier = modifier
) {
    val service = remember { MediaService.getInstance() }
    val state by remember(service?.playbackState) {
        service?.playbackState ?: flow { emit(EmptyState) }
    }.collectAsState(EmptyState)
    val currentTrackIndex = remember(state, state.currentMediaIndex) {
        state.currentMediaIndex
    }
    val isPlaying = remember(state, state.isPlaying) {
        state.isPlaying
    }
    val title: String = remember(track) { track.mediaMetadata?.title?.toString() ?: "-" }
    val artist = remember(track) { track.mediaMetadata?.artist?.toString() ?: "-" }
    val isCurrentTrack = remember(index, currentTrackIndex) {
        index == currentTrackIndex
    }
    val textColor by rememberDerivedState(isCurrentTrack) {
        if (isCurrentTrack) selectedColor else unSelectedColor
    }
    val image by rememberVectorImageProvider(
        Icons.Filled.Equalizer,
        24.dp,
        Color(0xFF6699FF)
    )
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (isCurrentTrack && isPlaying) Color.White.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .clickable(onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 16.sp,
                    fontWeight = if (isCurrentTrack) FontWeight.Medium else FontWeight.Normal
                )
            )
            Text(
                text = artist,
                maxLines = 1,
                style = TextStyle(
                    color = ColorProvider(textColor.copy(alpha = 0.7f)),
                    fontSize = 14.sp
                )
            )
        }
        if (isCurrentTrack && isPlaying) {
            Image(
                provider = image,
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp),
            )
        } else {
            Spacer(
                modifier = GlanceModifier.size(24.dp)
            )
        }
    }
}
