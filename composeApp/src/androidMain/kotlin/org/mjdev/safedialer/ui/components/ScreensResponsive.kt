@file:Suppress("unused")

package org.mjdev.safedialer.ui.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import org.mjdev.safedialer.data.custom.DisplayInfo
import org.mjdev.safedialer.data.custom.DisplayInfo.Companion.rememberDisplayInfo
import org.mjdev.safedialer.extensions.ComposeExt.applyIf
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

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
                .background(Color.DarkGray)
        ) {}
    },
    landscapeBottomMenu: (@Composable (DisplayInfo) -> Unit)? = {
        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .background(Color.DarkGray)
        ) {}
    },
    content: @Composable (DisplayInfo) -> Unit = {},
    preview : @Composable BoxScope.(item:Any?) -> Unit = {},
) = AppTheme {
    BoxWithConstraints(
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
                                background(Color.Gray)
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
}
