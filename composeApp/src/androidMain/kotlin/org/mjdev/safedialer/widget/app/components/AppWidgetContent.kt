package org.mjdev.safedialer.widget.app.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import org.mjdev.safedialer.widget.app.helpers.Constants.allItems
import org.mjdev.safedialer.widget.base.GlancePreviews

@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun AppWidgetContent(
    playerBackgroundColor: Color = Color(0xFF1A1A1A),
    imageBackgroundColor: Color = Color(0xFF3A3A3A),
    trackListBackgroundColor: Color = Color(0xFF2B2B2B),
    items: List<Pair<String, String>> = allItems,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(playerBackgroundColor)
            .padding(16.dp)
            .cornerRadius(32.dp)
    ) {
        HeaderSection(
            imageBackgroundColor = imageBackgroundColor,
        )
        Spacer(modifier = GlanceModifier.height(12.dp))
        TrackListSection(
            items = items,
            trackListBackgroundColor = trackListBackgroundColor,
        )
    }
}
