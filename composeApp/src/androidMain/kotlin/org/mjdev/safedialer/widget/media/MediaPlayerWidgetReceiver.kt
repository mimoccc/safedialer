package org.mjdev.safedialer.widget.media

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MediaPlayerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MediaPlayer()
}