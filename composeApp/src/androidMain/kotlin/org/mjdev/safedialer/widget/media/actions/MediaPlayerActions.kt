package org.mjdev.safedialer.widget.media.actions

import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.actionRunCallback

object MediaPlayerActions {
    fun playPause() = actionRunCallback<PlayPauseAction>()
    fun previous() = actionRunCallback<PreviousAction>()
    fun next() = actionRunCallback<NextAction>()
    fun toggleShuffle() = actionRunCallback<ToggleShuffleAction>()
    fun openQueue() = actionRunCallback<OpenQueueAction>()
    fun selectTrack(
        index: Int
    ) = actionRunCallback<SelectTrackAction>(
        parameters = actionParametersOf(
            ActionParameters.Key<Int>("track_index") to index
        )
    )
}