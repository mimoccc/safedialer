package org.mjdev.safedialer.widget.media.actions

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import org.mjdev.safedialer.widget.media.MediaPlayer
import org.mjdev.safedialer.widget.media.helpers.Constants.CURRENT_TRACK
import org.mjdev.safedialer.widget.media.helpers.Constants.IS_PLAYING
import org.mjdev.safedialer.widget.media.helpers.Constants.TRACK_INDEX

class SelectTrackAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val trackIndex = parameters[ActionParameters.Key(TRACK_INDEX)] ?: 0
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[intPreferencesKey(CURRENT_TRACK)] = trackIndex
            prefs[booleanPreferencesKey(IS_PLAYING)] = true
        }
        MediaPlayer().update(context, glanceId)
    }
}