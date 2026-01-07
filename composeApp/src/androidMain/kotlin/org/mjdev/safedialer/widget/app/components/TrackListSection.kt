package org.mjdev.safedialer.widget.app.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.preview.ExperimentalGlancePreviewApi
import org.mjdev.safedialer.widget.app.actions.AppWidgetActions
import org.mjdev.safedialer.widget.app.helpers.Constants.allItems
import org.mjdev.safedialer.widget.base.previews.GlancePreviews

@OptIn(ExperimentalGlancePreviewApi::class)
@SuppressLint("RestrictedApi")
@GlancePreviews
@GlanceComposable
@Composable
fun TrackListSection(
    trackListBackgroundColor: Color = Color(0xFF1A1A1A),
    items: List<Pair<String, String>> = allItems,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(trackListBackgroundColor)
            .cornerRadius(12.dp)
    ) {
        LazyColumn(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, track ->
                AppItem(
                    modifier = GlanceModifier
                        .fillMaxWidth(),
                    title = track.first,
                    subtitle = track.second,
                    onClick = AppWidgetActions.selectItem(index)
                )
                if (index < items.lastIndex) {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                }
            }
        }
    }
}
