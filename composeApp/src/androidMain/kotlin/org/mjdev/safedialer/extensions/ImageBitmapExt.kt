package org.mjdev.safedialer.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import androidx.core.graphics.scale

@Suppress("unused")
object ImageBitmapExt {

    fun ImageBitmap.resize(
        width: Dp,
        height: Dp
    ): ImageBitmap {
        val bitmap = asAndroidBitmap()
        val w = if (width.isSpecified) width.value.toInt() else bitmap.width
        val h = if (height.isSpecified) height.value.toInt() else bitmap.height
        return bitmap.scale(w, h).asImageBitmap()
    }

}