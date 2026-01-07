package org.mjdev.safedialer.widget.media.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.text.Text
import org.mjdev.safedialer.widget.media.actions.MediaPlayerActions
import org.mjdev.safedialer.widget.base.previews.GlancePreviews
import kotlinx.coroutines.flow.flow
import org.mjdev.safedialer.service.media.MediaService

@OptIn(ExperimentalGlancePreviewApi::class)
@SuppressLint("RestrictedApi")
@GlancePreviews
@GlanceComposable
@Composable
fun TrackListSection(
    trackListBackgroundColor: Color = Color(0xFF1A1A1A),
) {
    val service = remember { MediaService.getInstance() }
    val playlist = remember(service?.playlist) {
        service?.playlist ?: flow { emit(emptyList()) }
    }.collectAsState(null)
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(trackListBackgroundColor)
            .cornerRadius(12.dp)
    ) {
        // loading
        if (playlist.value == null) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        // empty
        } else if (playlist.value?.isEmpty() == true) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("No media file")
            }
        // content
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize(),
            ) {
                item {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                }
                itemsIndexed(
                    items = playlist.value ?: emptyList(),
                    itemId = { i, t -> playlist.value?.indexOf(t)?.toLong() ?: i.toLong() }
                ) { index, track ->
                    TrackItem(
                        modifier = GlanceModifier.fillMaxWidth(),
                        index = index,
                        track = track,
                        onClick = MediaPlayerActions.selectTrack(index),
                    )
                }
                item {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                }
            }
        }
    }
}
