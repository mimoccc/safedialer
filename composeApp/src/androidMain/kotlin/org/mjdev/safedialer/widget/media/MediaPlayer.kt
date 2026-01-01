package org.mjdev.safedialer.widget.media

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import org.mjdev.safedialer.widget.media.components.MediaPlayerContent

class MediaPlayer : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            MediaPlayerContent()
        }
    }
}












