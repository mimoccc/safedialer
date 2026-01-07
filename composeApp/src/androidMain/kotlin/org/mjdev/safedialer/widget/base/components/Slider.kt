package org.mjdev.safedialer.widget.base.components

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import org.mjdev.safedialer.widget.base.extensions.GlanceComposeExt.rememberDerivedState

@GlanceComposable
@Composable
fun Slider(
    modifier: GlanceModifier = GlanceModifier,
    sliderHeight: Dp = 6.dp,
    sliderColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
    currentPosition: Float = 0f,
    duration: Float = 0f,
) {
    val size = LocalSize.current
    val progress: Float by rememberDerivedState(currentPosition) {
        if (duration > 0f) (currentPosition / duration).coerceIn(0f, 1f) else 0f
    }
    val progressImage: Bitmap? by rememberDerivedState(progress) {
        val widthPx = (size.width.value * Resources.getSystem().displayMetrics.density).toInt()
        val heightPx = (sliderHeight.value * Resources.getSystem().displayMetrics.density).toInt()
        createBitmap(widthPx, heightPx, Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = backgroundColor.toArgb()
            }
            canvas.drawRoundRect(
                0f, 0f, widthPx.toFloat(), heightPx.toFloat(),
                heightPx / 2f, heightPx / 2f,
                bgPaint
            )
            if (progress > 0f) {
                val fgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = sliderColor.toArgb()
                }
                val progressWidth = widthPx * progress
                canvas.drawRoundRect(
                    0f, 0f, progressWidth, heightPx.toFloat(),
                    heightPx / 2f, heightPx / 2f,
                    fgPaint
                )
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        contentAlignment = Alignment.TopStart
    ) {
        if (progressImage != null) {
            Image(
                contentDescription = "",
                provider = ImageProvider(progressImage!!),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(sliderHeight)
                    .cornerRadius(sliderHeight / 2)
            )
        }
    }
}
