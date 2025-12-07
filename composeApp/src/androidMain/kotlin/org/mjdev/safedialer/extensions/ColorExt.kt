package org.mjdev.safedialer.extensions

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

object ColorExt {

    fun Color.lighter(
        @FloatRange(from = 0.0, to = 1.0)
        ratio: Float
    ): Color {
        val r = red
        val g = green
        val b = blue
        val a = alpha
        val k = ratio.coerceIn(0f, 1f)
        val nr = r + (1f - r) * k
        val ng = g + (1f - g) * k
        val nb = b + (1f - b) * k
        return Color(nr, ng, nb, a)
    }

    fun Color.darker(
        @FloatRange(from = 0.0, to = 1.0)
        ratio: Float
    ): Color {
        val r = red
        val g = green
        val b = blue
        val a = alpha
        val k = ratio.coerceIn(0f, 1f)
        val nr = r * (1f - k)
        val ng = g * (1f - k)
        val nb = b * (1f - k)
        return Color(nr, ng, nb, a)
    }

}