package org.mjdev.safedialer.widget.base

import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidget
import com.google.android.glance.appwidget.host.AppWidgetHostPreview
import org.mjdev.safedialer.R
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.widget.app.AppWidget
import org.mjdev.safedialer.widget.base.GlanceComposeExt.rememberPreviewSize

@Previews
@Composable
fun InAppWidgetHostPreview(
    modifier: Modifier = Modifier,
    glanceAppWidget: GlanceAppWidget = AppWidget(),
//    provider: AppWidgetProviderInfo? = null,
    @LayoutRes
    previewResId: Int = R.layout.widget_preview
) = Box(
    modifier = modifier
) {
    val displaySize by rememberPreviewSize()
//    val state = preferencesOf(glanceAppWidget.countKey to 2)
    Box(modifier = modifier) {
        AppWidgetHostPreview(
            modifier = Modifier.fillMaxSize(),
            displaySize = displaySize
        ) { context ->
            RemoteViews(context.packageName, previewResId)
        }
//        GlanceAppWidgetHostPreview(
//            modifier = Modifier.fillMaxSize(),
//            glanceAppWidget = glanceAppWidget,
//            state = state,
//            displaySize = displaySize,
//            provider = provider,
//        )
    }
}
