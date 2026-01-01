package org.mjdev.safedialer.widget.app.helpers

object Constants {
    val allItems: List<Pair<String, String>> = (1..32).map { idx ->
        "Item title $idx" to "Item $idx"
    }

    const val CURRENT_SELECTION = "current_selection"
}