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
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import org.mjdev.safedialer.R
import org.mjdev.safedialer.widget.base.GlanceComposeExt.ActionEmpty
import org.mjdev.safedialer.widget.base.GlanceComposeExt.rememberDerivedState
import org.mjdev.safedialer.widget.base.GlancePreviews

@OptIn(ExperimentalGlancePreviewApi::class)
@SuppressLint("RestrictedApi")
@GlancePreviews
@GlanceComposable
@Composable
fun TrackItem(
    modifier: GlanceModifier = GlanceModifier,
    title: String = "Title",
    artist: String = "Artist",
    isCurrentTrack: Boolean = false,
    onClick: Action = ActionEmpty,
    selectedColor: Color = Color(0xFF6699FF),
    unSelectedColor: Color = Color.Gray
) = Box(
    modifier = modifier
) {
    val textColor by rememberDerivedState(isCurrentTrack) {
        if (isCurrentTrack) selectedColor else unSelectedColor
    }
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 16.sp,
                    fontWeight = if (isCurrentTrack) FontWeight.Medium else FontWeight.Normal
                )
            )
            Text(
                text = artist,
                style = TextStyle(
                    color = ColorProvider(textColor.copy(alpha = 0.7f)),
                    fontSize = 14.sp
                )
            )
        }
        if (isCurrentTrack) {
            Image(
                provider = ImageProvider(R.mipmap.ic_launcher),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp),
                colorFilter = ColorFilter.tint(ColorProvider(Color(0xFF6699FF)))
            )
        }
    }
}
