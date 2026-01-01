package org.mjdev.safedialer.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun VideoView(
    modifier: Modifier.Companion = Modifier
) = Box(
    modifier = modifier
        .background(
            Color.Black,
            RoundedCornerShape(8.dp)
        )
) {
    // todo
}