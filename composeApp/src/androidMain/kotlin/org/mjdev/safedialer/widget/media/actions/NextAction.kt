package org.mjdev.safedialer.widget.media.actions

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import org.mjdev.safedialer.widget.media.MediaPlayer
import org.mjdev.safedialer.widget.media.helpers.Constants.CURRENT_TRACK

class NextAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentTrack = prefs[intPreferencesKey(CURRENT_TRACK)] ?: 0
            prefs[intPreferencesKey(CURRENT_TRACK)] = currentTrack + 1
        }
        MediaPlayer().update(context, glanceId)
    }
}