package org.mjdev.safedialer.widget.media.actions

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import org.mjdev.safedialer.widget.media.MediaPlayer
import org.mjdev.safedialer.widget.media.helpers.Constants.IS_SHUFFLE

class ToggleShuffleAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val shuffle = prefs[booleanPreferencesKey(IS_SHUFFLE)] ?: false
            prefs[booleanPreferencesKey(IS_SHUFFLE)] = !shuffle
        }
        MediaPlayer().update(context, glanceId)
    }
}