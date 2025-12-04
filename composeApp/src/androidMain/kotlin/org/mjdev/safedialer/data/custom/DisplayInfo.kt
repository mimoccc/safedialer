package org.mjdev.safedialer.data.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.content.res.Configuration

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
    }
}
