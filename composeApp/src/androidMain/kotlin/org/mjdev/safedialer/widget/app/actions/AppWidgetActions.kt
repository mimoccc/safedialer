package org.mjdev.safedialer.widget.app.actions

import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.action.actionRunCallback
import org.mjdev.safedialer.activity.MainActivity

object AppWidgetActions {
    fun selectItem(
        index: Int
    ) = actionRunCallback<SelectItemAction>(
        parameters = actionParametersOf(
            ActionParameters.Key<Int>("track_index") to index
        )
    )
    fun openApp() = actionStartActivity<MainActivity>()
}