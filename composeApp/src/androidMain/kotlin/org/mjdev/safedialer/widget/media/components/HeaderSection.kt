package org.mjdev.safedialer.widget.media.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.graphics.ImageDecoder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.BitmapImageProvider
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.flow.flow
import org.mjdev.safedialer.R
import org.mjdev.safedialer.service.media.MediaService
import org.mjdev.safedialer.service.media.MediaService.Companion.EmptyState
import org.mjdev.safedialer.widget.base.extensions.GlanceComposeExt.rememberDerivedState
import org.mjdev.safedialer.widget.base.previews.GlancePreviews
import org.mjdev.safedialer.widget.base.components.Slider

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun HeaderSection(
    imageBackgroundColor: Color = Color(0xFF3A3A3A),
) {
    val context: Context = LocalContext.current
    val service = remember { MediaService.getInstance() }
    val playlist = remember(service?.playlist) {
        service?.playlist ?: flow { emit(emptyList()) }
    }.collectAsState(null)
    val state by remember(service?.playbackState, service?.playlist) {
        service?.playbackState ?: flow { emit(EmptyState) }
    }.collectAsState(EmptyState)
    val currentTrackIndex = remember(
        service?.playbackState,
        service?.playlist,
        state.currentMediaIndex
    ) {
        state.currentMediaIndex
    }
    val duration = remember(
        state,
        state.duration,
        state.currentMediaIndex,
        state.currentPosition
    ) {
        state.duration.toFloat()
    }
    val currentPosition = remember(
        state,
        state.duration,
        state.currentMediaIndex,
        state.currentPosition
    ) {
        state.currentPosition.toFloat()
    }
    val title: String by rememberDerivedState(currentTrackIndex) {
        playlist.value?.getOrNull(currentTrackIndex)?.mediaMetadata?.title?.toString() ?: "-"
    }
    val artist by rememberDerivedState(currentTrackIndex) {
        playlist.value?.getOrNull(currentTrackIndex)?.mediaMetadata?.artist?.toString() ?: "-"
    }
    val image by rememberDerivedState(currentTrackIndex) {
        try {
            playlist.value?.getOrNull(currentTrackIndex)
                ?.mediaMetadata
                ?.artworkUri
                ?.let { uri ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(context.contentResolver, uri)
                        )
                    } else {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    }
                }
        } catch (t: Throwable) {
//            t.printStackTrace()
            null
        }
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
            )
            if (image != null) {
                Image(
                    provider = BitmapImageProvider(image!!),
                    contentDescription = null,
                    modifier = GlanceModifier
                        .size(100.dp)
                        .cornerRadius(12.dp),
                )
            } else {
                Box(
                    modifier = GlanceModifier
                        .size(100.dp)
                        .cornerRadius(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = GlanceModifier.size(100.dp)
//                            .background(Color.White.copy(alpha = 0.2f))
//                            .cornerRadius(50.dp)
                            .padding(start = 35.dp, top = 10.dp),
                        text = "?",
                        style = TextStyle(
                            color = ColorProvider(Color.Black.copy(alpha = 0.35f)),
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Image(
                provider = ImageProvider(R.drawable.bgcase),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(104.dp)
                    .cornerRadius(12.dp),
            )
        }
        Spacer(
            modifier = GlanceModifier.width(12.dp)
        )
        Column(
            modifier = GlanceModifier
                .defaultWeight()
                .padding(end = 8.dp)
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = artist,
                maxLines = 1,
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                    fontSize = 14.sp
                )
            )
            Spacer(
                modifier = GlanceModifier.height(2.dp)
            )
            Slider(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color.White.copy(0.5f))
                    .cornerRadius(5.dp),
                duration = duration,
                currentPosition = currentPosition,
            )
            Spacer(
                modifier = GlanceModifier.height(2.dp)
            )
            ControlsSection()
        }
    }
}
