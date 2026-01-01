package org.mjdev.safedialer.widget.media.helpers

object Constants {
    val allTracks: List<Pair<String, String>> = (1..32).map { idx ->
        "Track title $idx" to "Artist $idx"
    }


    const val TRACK_INDEX = "track_index"
    const val CURRENT_TRACK = "current_track"
    const val IS_PLAYING = "is_playing"
    const val IS_SHUFFLE = "is_shuffle"
}