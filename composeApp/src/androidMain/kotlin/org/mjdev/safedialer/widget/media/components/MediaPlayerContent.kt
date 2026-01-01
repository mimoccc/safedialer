package org.mjdev.safedialer.widget.media.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import org.mjdev.safedialer.widget.base.GlanceComposeExt.bool
import org.mjdev.safedialer.widget.base.GlanceComposeExt.int
import org.mjdev.safedialer.widget.base.GlancePreviews
import org.mjdev.safedialer.widget.media.helpers.Constants.CURRENT_TRACK
import org.mjdev.safedialer.widget.media.helpers.Constants.IS_PLAYING
import org.mjdev.safedialer.widget.media.helpers.Constants.IS_SHUFFLE
import org.mjdev.safedialer.widget.media.helpers.Constants.allTracks

@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun MediaPlayerContent(
    playerBackgroundColor: Color = Color(0xFF1A1A1A),
    imageBackgroundColor: Color = Color(0xFF3A3A3A),
    trackListBackgroundColor: Color = Color(0xFF2B2B2B),
    tracks: List<Pair<String, String>> = allTracks,
) {
    val prefs = currentState<Preferences>()
    val currentTrackIndex = prefs.int(CURRENT_TRACK)
    val isPlaying = prefs.bool(IS_PLAYING)
    val isShuffleEnabled = prefs.bool(IS_SHUFFLE)
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(playerBackgroundColor)
            .padding(16.dp)
            .cornerRadius(32.dp)
    ) {
        HeaderSection(
            currentTrackIndex = currentTrackIndex,
            tracks = tracks,
            imageBackgroundColor = imageBackgroundColor,
            isPlaying = isPlaying,
            isShuffleEnabled = isShuffleEnabled,
        )
        Spacer(modifier = GlanceModifier.height(12.dp))
        TrackListSection(
            tracks = tracks,
            currentTrackIndex = currentTrackIndex,
            trackListBackgroundColor = trackListBackgroundColor,
        )
    }
}
