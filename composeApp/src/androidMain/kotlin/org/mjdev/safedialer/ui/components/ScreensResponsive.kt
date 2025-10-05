@file:Suppress("unused")

package org.mjdev.safedialer.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateDpAsState
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode
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
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val isLandscape by remember(configuration) {
        derivedStateOf { configuration.orientation == Configuration.ORIENTATION_LANDSCAPE }
    }
    val displayInfo by remember(constraints, density, isLandscape) {
        derivedStateOf {
            DisplayInfo(
                constraints = constraints,
                density = density,
                ratio = ratio,
                isLandscapeFixed = isLandscape,
            )
        }
    }
    val animatedContentWidth by animateDpAsState(targetValue = displayInfo.contentWidth, label = "contentWidth")
    val animatedPreviewWidth by animateDpAsState(targetValue = displayInfo.previewWidth, label = "previewWidth")
    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .width(animatedContentWidth)
                .fillMaxHeight()
                .apply {
                    if (isInPreviewMode) background(Color.LightGray)
                },
            content = {
                content(displayInfo)
            },
        )
        AnimatedVisibility(visible = displayInfo.isLandscape, enter = enter, exit = exit) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(animatedPreviewWidth)
                    .apply {
                        if (isInPreviewMode) background(Color.DarkGray)
                    },
                content = {
                    preview(displayInfo)
                },
            )
        }
    }
}

data class DisplayInfo(
    val constraints: Constraints,
    val density: Density,
    val ratio: Float = 0.4f,
    private val isLandscapeFixed: Boolean? = null,
) {
    val width: Dp
        get() = with(density) { constraints.maxWidth.toDp() }
    val height: Dp
        get() = with(density) { constraints.maxHeight.toDp() }

    // Use provided orientation when available (Android LocalConfiguration),
    // fallback to size-based orientation otherwise.
    val isLandscape: Boolean
        get() = isLandscapeFixed ?: (width > height)
    val isPortrait: Boolean
        get() = !isLandscape

    val contentWidth: Dp
        get() = if (isLandscape) {
            (width.value * ratio).dp
        } else {
            width
        }
    val previewWidth: Dp
        get() = if (isLandscape) width - contentWidth else 0.dp
}
