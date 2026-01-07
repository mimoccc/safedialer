package org.mjdev.safedialer.widget.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import org.mjdev.safedialer.widget.app.actions.AppWidgetActions
import org.mjdev.safedialer.widget.base.previews.GlancePreviews
import org.mjdev.safedialer.widget.base.components.ControlButton

@OptIn(ExperimentalGlancePreviewApi::class)
@GlancePreviews
@GlanceComposable
@Composable
fun ControlsSection() {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            imageVector = Icons.Filled.Apps,
            action = AppWidgetActions.openApp(),
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
    }
}
