package org.mjdev.safedialer.ui.components.player

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun TrackList(
    modifier: Modifier = Modifier,
    tracks: List<Track> = listOf(),
    currentTrackIndex: Int = 0
) = Surface(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    color = Color(0xFF2B2B2B)
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp).fillMaxSize()
    ) {
        itemsIndexed(
            tracks
        ) { index, track ->
            TrackListItem(
                modifier = Modifier.padding(
                    bottom = 8.dp
                ),
                track = track,
                isCurrentTrack = index == currentTrackIndex
            )
            if (index == tracks.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
