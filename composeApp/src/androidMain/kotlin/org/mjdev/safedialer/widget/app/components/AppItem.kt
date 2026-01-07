package org.mjdev.safedialer.widget.app.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import org.mjdev.safedialer.widget.base.extensions.GlanceComposeExt.ActionEmpty
import org.mjdev.safedialer.widget.base.previews.GlancePreviews

@OptIn(ExperimentalGlancePreviewApi::class)
@SuppressLint("RestrictedApi")
@GlancePreviews
@GlanceComposable
@Composable
fun AppItem(
    modifier: GlanceModifier = GlanceModifier,
    title: String = "Title",
    subtitle: String = "Subtitle",
    textColor: Color = Color.Gray,
    onClick: Action = ActionEmpty
) = Box(
    modifier = modifier
) {
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
                    fontWeight = FontWeight.Normal,
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    color = ColorProvider(textColor.copy(alpha = 0.7f)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            )
        }
    }
}
