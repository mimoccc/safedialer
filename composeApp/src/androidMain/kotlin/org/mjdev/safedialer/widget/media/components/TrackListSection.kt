package org.mjdev.safedialer.widget.media.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.preview.ExperimentalGlancePreviewApi
import org.mjdev.safedialer.widget.media.actions.MediaPlayerActions
import org.mjdev.safedialer.widget.base.GlancePreviews
import org.mjdev.safedialer.widget.media.helpers.Constants.allTracks

@OptIn(ExperimentalGlancePreviewApi::class)
@SuppressLint("RestrictedApi")
@GlancePreviews
@GlanceComposable
@Composable
fun TrackListSection(
    trackListBackgroundColor: Color = Color(0xFF1A1A1A),
    currentTrackIndex: Int = 0,
    tracks: List<Pair<String, String>> = allTracks,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(trackListBackgroundColor)
            .cornerRadius(12.dp)
    ) {
        LazyColumn(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            itemsIndexed(tracks) { index, track ->
                TrackItem(
                    modifier = GlanceModifier
                        .fillMaxWidth(),
                    title = track.first,
                    artist = track.second,
                    isCurrentTrack = index == currentTrackIndex,
                    onClick = MediaPlayerActions.selectTrack(index)
                )
                if (index < tracks.lastIndex) {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                }
            }
        }
    }
}
