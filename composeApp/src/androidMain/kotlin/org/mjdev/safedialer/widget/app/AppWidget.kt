package org.mjdev.safedialer.widget.app

import android.content.Context

import androidx.glance.GlanceId

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import org.mjdev.safedialer.widget.app.components.AppWidgetContent

class AppWidget() : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            AppWidgetContent()
        }
    }
}

//@OptIn(ExperimentalGlanceRemoteViewsApi::class)
//@Suppress("unused")
//@Previews
//@Composable
//fun AppWidgetPreview(
//    modifier: Modifier = Modifier.fillMaxSize(),
//    widget: GlanceAppWidget = AppWidget()
//) = InAppWidgetHostPreview(
//    modifier = modifier,
//    glanceAppWidget = widget,
//)
