package org.mjdev.safedialer.widget.app.actions

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import org.mjdev.safedialer.widget.app.AppWidget
import org.mjdev.safedialer.widget.app.helpers.Constants.CURRENT_SELECTION

class SelectItemAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val trackIndex = parameters[ActionParameters.Key(CURRENT_SELECTION)] ?: 0
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[intPreferencesKey(CURRENT_SELECTION)] = trackIndex
        }
        AppWidget().update(context, glanceId)
    }
}