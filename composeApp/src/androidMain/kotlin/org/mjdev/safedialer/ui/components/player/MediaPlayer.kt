package org.mjdev.safedialer.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Previews
@Composable
fun MediaPlayer(
    modifier: Modifier = Modifier,
    trackTitle: String = "track",
    artistName: String = "artist",
    albumArt: Painter? = null,
    isPlaying: Boolean = true,
    isShuffleEnabled: Boolean = false,
    onPlayPauseClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onShuffleClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    tracks: List<Track> = listOf(Track(), Track(), Track()),
    currentTrackIndex: Int = 0,
) = AppTheme {
    val currentTrack by remember(currentTrackIndex) {
        derivedStateOf { tracks[currentTrackIndex] }
    }
    val isVideo by remember(currentTrack) {
        derivedStateOf {
            // todo
            true
        }
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFF1A1A1A)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxHeight(0.4f)
                    .fillMaxWidth(),
                visible = isVideo
            ) {
                VideoView()
            }
            Row {
                AlbumArtwork(
                    modifier = Modifier.size(110.dp),
                    albumArt = albumArt
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                ) {
                    TrackInfo(
                        modifier = Modifier
                            .padding(
                                start = 8.dp
                            )
                            .fillMaxWidth(),
                        trackTitle,
                        artistName
                    )
                    MediaControls(
                        isPlaying = isPlaying,
                        isShuffleEnabled = isShuffleEnabled,
                        onPlayPauseClick = onPlayPauseClick,
                        onPreviousClick = onPreviousClick,
                        onNextClick = onNextClick,
                        onShuffleClick = onShuffleClick,
                        onQueueClick = onQueueClick
                    )
                }
            }
            TrackList(
                modifier = Modifier.fillMaxSize(),
                tracks = tracks,
                currentTrackIndex = currentTrackIndex
            )
        }
    }
}

data class Track(
    val title: String = "Track title",
    val artist: String = "Artist"
)
