package org.mjdev.safedialer.service.media

data class PlaybackState(
    var isPlaying: Boolean = false,
    var currentMediaIndex: Int = 0,
    var currentPosition: Long = 0,
    var duration: Long = 0,
    var isShuffleEnabled : Boolean = false,
)