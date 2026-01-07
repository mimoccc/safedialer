package org.mjdev.safedialer.widget.media.actions

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import org.mjdev.safedialer.service.media.MediaCommand
import org.mjdev.safedialer.service.media.MediaService

class MediaActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val commandName = parameters[KEY_COMMAND] ?: return
        val index : Int? = parameters[KEY_INDEX]?.let { idx ->
            runCatching { idx.toInt() }.getOrNull()
        }
        val command = MediaCommand.valueOf(commandName)
        sendCommand(context, command, index)
    }

    fun sendCommand(
        context: Context,
        command: MediaCommand,
        index: Int?
    ) {
        Intent(command.action).apply {
            index?.let { idx ->
                putExtra(MediaService.EXTRA_INDEX, idx)
            }
            context.sendBroadcast(this)
        }
    }
    
    companion object {
        val KEY_COMMAND = ActionParameters.Key<String>("command")
        val KEY_INDEX = ActionParameters.Key<String>("index")
    }
}