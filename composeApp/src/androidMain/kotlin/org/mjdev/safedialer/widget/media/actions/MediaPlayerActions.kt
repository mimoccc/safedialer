package org.mjdev.safedialer.widget.media.actions

import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.actionRunCallback
import org.mjdev.safedialer.service.media.MediaCommand
import org.mjdev.safedialer.widget.base.actions.CustomAction
import org.mjdev.safedialer.widget.media.actions.MediaActionCallback.Companion.KEY_COMMAND
import org.mjdev.safedialer.widget.media.actions.MediaActionCallback.Companion.KEY_INDEX

object MediaPlayerActions {
    fun play() = actionRunCallback<MediaActionCallback>(
        actionParametersOf(
            KEY_COMMAND to MediaCommand.PLAY.name
        )
    )

    fun pause() = actionRunCallback<MediaActionCallback>(
        actionParametersOf(
            KEY_COMMAND to MediaCommand.PAUSE.name
        )
    )

    fun previous() = actionRunCallback<MediaActionCallback>(
        actionParametersOf(
            KEY_COMMAND to MediaCommand.PREVIOUS.name
        )
    )

    fun next() = actionRunCallback<MediaActionCallback>(
        actionParametersOf(
            KEY_COMMAND to MediaCommand.NEXT.name
        )
    )

    fun selectTrack(
        index: Int
    ) = actionRunCallback<MediaActionCallback>(
        actionParametersOf(
            KEY_COMMAND to MediaCommand.PLAY_INDEX.name,
            KEY_INDEX to index.toString()
        )
    )

    fun toggleShuffle() = actionRunCallback<MediaActionCallback>(
        actionParametersOf(
            KEY_COMMAND to MediaCommand.TOGGLE_SHUFFLE.name
        )
    )

    // todo
    fun shareItem() = actionRunCallback<CustomAction>()
}