package org.mjdev.safedialer.widget.base.actions

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import org.mjdev.safedialer.widget.media.MediaPlayer

class CustomAction(
    val onCustomAction: CustomAction.(
        context: Context,
        glanceId: GlanceId,
        prefs: MutablePreferences,
        parameters: ActionParameters,
    ) -> Unit = { c, i, ps, pr -> }
) : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            onCustomAction.invoke(
                this,
                context,
                glanceId,
                prefs,
                parameters
            )
        }
        MediaPlayer().update(context, glanceId)
    }
}