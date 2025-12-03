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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.components.DisplayInfo.Companion.applyIf
import org.mjdev.safedialer.ui.components.DisplayInfo.Companion.rememberDisplayInfo

@Previews
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    ratio: Float = 0.3f,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    portraitLeftMenu: (@Composable (DisplayInfo) -> Unit)? = {
        Box(
            modifier = Modifier
                .width(64.dp)
                .fillMaxHeight()
                .background(Color.Red)
        ) {}
    },
    landscapeBottomMenu: (@Composable (DisplayInfo) -> Unit)? = {
        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .background(Color.Red)
        ) {}
    },
    content: @Composable (DisplayInfo) -> Unit = {},
    preview: @Composable (DisplayInfo) -> Unit = {},
) = BoxWithConstraints(
    modifier.fillMaxSize()
) {
    val displayInfo = rememberDisplayInfo(constraints)
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
        ) {
            AnimatedVisibility(
                visible = displayInfo.isPortrait,
                enter = enter,
                exit = exit
            ) {
                portraitLeftMenu?.invoke(displayInfo)
            }
            Box(
                modifier = Modifier
                    .width(displayInfo.contentWidth)
                    .fillMaxHeight()
                    .applyIf(isInPreviewMode) {
                        background(Color.LightGray)
                    },
                content = {
                    content(displayInfo)
                },
            )
            AnimatedVisibility(
                visible = displayInfo.isLandscape,
                enter = enter,
                exit = exit
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(displayInfo.previewWidth)
                        .applyIf(isInPreviewMode) {
                            background(Color.DarkGray)
                        },
                    content = {
                        preview(displayInfo)
                    },
                )
            }
        }
        AnimatedVisibility(
            visible = displayInfo.isLandscape,
            enter = enter,
            exit = exit
        ) {
            landscapeBottomMenu?.invoke(displayInfo)
        }
    }
}

@Suppress("MemberVisibilityCanBePrivate")
data class DisplayInfo(
    val constraints: Constraints,
    val density: Density,
    val configuration: Configuration,
    val ratio: Float = 0.4f,
    private val isLandscapeFixed: Boolean? = null,
) {
    val width: Dp
        get() = with(density) {
            constraints.maxWidth.toDp()
        }

    val height: Dp
        get() = with(density) {
            constraints.maxHeight.toDp()
        }

    val isLandscape: Boolean
        get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isPortrait: Boolean
        get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val contentWidth: Dp
        get() = if (isLandscape) {
            (width.value * ratio).dp
        } else {
            width
        }

    val previewWidth: Dp
        get() = if (isLandscape) width - contentWidth else 0.dp

    companion object {
        @Composable
        fun rememberDisplayInfo(
            constraints: Constraints,
            density: Density = LocalDensity.current,
            configuration: Configuration = LocalConfiguration.current,
        ) = remember(
            constraints,
            density,
            configuration,
            configuration.orientation
        ) { DisplayInfo(constraints, density, configuration) }

        fun Modifier.applyIf(
            condition: Boolean,
            other: Modifier.() -> Modifier
        ): Modifier = if (condition) this.then(other()) else this
    }
}
