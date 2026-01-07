package org.mjdev.safedialer.widget.app.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
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
import org.mjdev.safedialer.widget.app.actions.AppWidgetActions
import org.mjdev.safedialer.widget.app.helpers.CustomAppExt.rememberCurrentUser
import org.mjdev.safedialer.widget.base.previews.GlancePreviews

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun HeaderSection(
    imageBackgroundColor: Color = Color(0xFF3A3A3A),
) {
    val user = rememberCurrentUser(
        pictureWidth = 100.dp,
        pictureHeight = 100.dp
    )
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .size(100.dp)
                .background(imageBackgroundColor)
                .cornerRadius(50.dp),
            contentAlignment = Alignment.Center
        ) {
            user.value.picture?.also { picture ->
                Image(
                    provider = ImageProvider(picture.asAndroidBitmap()),
                    contentDescription = null,
                    modifier = GlanceModifier
                        .size(100.dp)
                        .cornerRadius(50.dp),
                )
            }
            Image(
                provider = ImageProvider(R.drawable.bgcase),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(104.dp)
                    .cornerRadius(50.dp)
                    .clickable(AppWidgetActions.openApp()),
            )
        }
        Spacer(
            modifier = GlanceModifier.width(12.dp)
        )
        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            ControlsSection()
            Spacer(
                modifier = GlanceModifier.height(4.dp)
            )
            Column(
                modifier = GlanceModifier.defaultWeight()
            ) {
                Text(
                    text = user.value.name,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = user.value.email,
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}
