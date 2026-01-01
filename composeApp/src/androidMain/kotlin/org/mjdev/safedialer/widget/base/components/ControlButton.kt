package org.mjdev.safedialer.widget.base.components

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.unit.ColorProvider
import org.mjdev.safedialer.widget.base.GlanceComposeExt.ActionEmpty
import org.mjdev.safedialer.widget.base.GlanceComposeExt.rememberVectorImageProvider
import org.mjdev.safedialer.widget.base.GlancePreviews

@GlancePreviews
@OptIn(ExperimentalGlancePreviewApi::class)
@SuppressLint("RestrictedApi")
@GlanceComposable
@Composable
fun ControlButton(
    imageVector: ImageVector = Icons.Filled.PlayCircle,
    action: Action = ActionEmpty,
    tint: Color = Color.White,
    size: Dp = 32.dp
) {
    val imageProvider by rememberVectorImageProvider(imageVector, size, tint)
    Image(
        provider = imageProvider,
        contentDescription = "",
        modifier = GlanceModifier
            .size(size)
            .clickable(action),
        colorFilter = ColorFilter.tint(ColorProvider(tint))
    )
}
