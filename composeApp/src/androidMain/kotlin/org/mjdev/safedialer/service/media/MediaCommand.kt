package org.mjdev.safedialer.service.media;

import org.mjdev.safedialer.BuildConfig


enum class MediaCommand(
    val action: String
) {
    PLAY("${BuildConfig.APP_ID}.PLAY"),
    PAUSE("${BuildConfig.APP_ID}.PAUSE"),
    NEXT("${BuildConfig.APP_ID}.NEXT"),
    PREVIOUS("${BuildConfig.APP_ID}.PREVIOUS"),
    STOP("${BuildConfig.APP_ID}.STOP"),
    PLAY_INDEX("${BuildConfig.APP_ID}.PLAY_INDEX"),
    TOGGLE_SHUFFLE("${BuildConfig.APP_ID}.TOGGLE_SHUFFLE"),
//    RELOAD_MEDIA("${BuildConfig.APP_ID}.RELOAD_MEDIA"),
    ;

    companion object {
        fun fromAction(
            action: String?
        ): MediaCommand? = entries.find { it.action == action }
    }
}