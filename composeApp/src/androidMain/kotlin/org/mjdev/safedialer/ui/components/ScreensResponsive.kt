@file:Suppress("unused")

package org.mjdev.safedialer.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    ratio: Float = 0.3f,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    content: @Composable (DisplayInfo) -> Unit = {},
    preview: @Composable (DisplayInfo) -> Unit = {},
) = BoxWithConstraints(
    modifier.fillMaxSize()
) {
    val displayInfo by remember(constraints) {
        derivedStateOf {
            DisplayInfo(
                constraints,
                ratio,
            )
        }
    }
    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .width(displayInfo.contentWidth)
                .fillMaxHeight(),
            content = {
                content(displayInfo)
            },
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(displayInfo.previewWidth),
            content = {
                preview(displayInfo)
            },
        )
    }
}

data class DisplayInfo(
    val constraints: Constraints,
    val ratio: Float = 0.4f,
) {
    val width: Dp
        get() = constraints.maxWidth.dp
    val height: Dp
        get() = constraints.maxHeight.dp
    val isLandscape: Boolean
        get() = width > height
    val isPortrait: Boolean
        get() = height > width
    val contentWidth: Dp
        get() = if (isLandscape) {
            (width.value * ratio).dp
        } else {
            width
        }
    val previewWidth: Dp
        get() = width - contentWidth
}
