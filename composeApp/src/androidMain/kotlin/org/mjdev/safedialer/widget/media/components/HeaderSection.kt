package org.mjdev.safedialer.widget.media.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import org.mjdev.safedialer.R
import org.mjdev.safedialer.widget.base.GlanceComposeExt.rememberDerivedState
import org.mjdev.safedialer.widget.base.GlancePreviews
import org.mjdev.safedialer.widget.media.helpers.Constants.allTracks

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun HeaderSection(
    currentTrackIndex: Int = 0,
    isPlaying: Boolean = false,
    isShuffleEnabled: Boolean = false,
    tracks: List<Pair<String, String>> = allTracks,
    imageBackgroundColor: Color = Color(0xFF3A3A3A),
) {
    val title : String by rememberDerivedState(currentTrackIndex) {
        tracks.getOrNull(currentTrackIndex)?.first ?: ""
    }
    val artist by rememberDerivedState(currentTrackIndex) {
        tracks.getOrNull(currentTrackIndex)?.second ?: ""
    }
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .size(100.dp)
                .background(imageBackgroundColor)
                .cornerRadius(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.audio_player_default_show_bg),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(100.dp)
                    .cornerRadius(12.dp),
//                colorFilter = ColorFilter.tint(ColorProvider(Color.White.copy(alpha = 0.6f)))
            )
            Image(
                provider = ImageProvider(R.drawable.bgcase),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(104.dp)
                    .cornerRadius(12.dp),
//                colorFilter = ColorFilter.tint(ColorProvider(Color.White.copy(alpha = 0.6f)))
            )
        }
        Spacer(
            modifier = GlanceModifier.width(12.dp)
        )
        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = artist,
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                    fontSize = 14.sp
                )
            )
            Spacer(
                modifier = GlanceModifier.height(12.dp)
            )
            ControlsSection(
                isPlaying = isPlaying,
                isShuffleEnabled = isShuffleEnabled
            )
        }
    }
}
