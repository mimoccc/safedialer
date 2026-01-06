package org.mjdev.safedialer.ui.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import org.mjdev.safedialer.extensions.ComposeExt.applyIf
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.sync.SyncAccountTypes
import org.mjdev.safedialer.ui.components.custom.ResponsiveContainer
import org.mjdev.safedialer.ui.components.dialer.DialPad
import org.mjdev.safedialer.ui.components.dialer.FabState
import org.mjdev.safedialer.ui.components.dialer.FabState.Companion.rememberFabState
import org.mjdev.safedialer.ui.state.TabsState
import org.mjdev.safedialer.ui.state.TabsState.Companion.rememberTabsState
import org.mjdev.safedialer.ui.theme.AppTheme

@Suppress("unused")
@Previews
@Composable
fun TabbedScreen(
    modifier: Modifier = Modifier,
    startTab: SyncAccountTypes = SyncAccountTypes.CALL_LOG, // todo last item
    scrollState: LazyListState = rememberLazyListState(),
    filterText: State<String> = remember { mutableStateOf("") },
    tabState: TabsState = rememberTabsState(startTab = startTab),
    fabState: FabState = rememberFabState(),
    hazeState: HazeState = remember { HazeState() },
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    shape: RoundedCornerShape = RoundedCornerShape(50),
    useBlur: Boolean = false,
    createPreview: @Composable BoxScope.(item: Any?) -> Unit = {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Todo preview here",
        )
    }
) = AppTheme {
    Box(
        modifier = modifier
            .padding(2.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        val phoneNumber: MutableState<String> = remember { mutableStateOf("") }
        // todo dialPad
//        val dialPadVisible = remember(fabState.isVisible) { fabState.isVisible }
        ResponsiveContainer(
            modifier = Modifier.background(backgroundColor),
            ratio = 0.4f,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .applyIf(useBlur) {
                            haze(hazeState)
                        },
                ) {
                    (tabState.currentTab as? SyncAccountTypes)
                        ?.content
                        ?.invoke(scrollState, filterText)
                }
            },
            preview = createPreview,
            landscapeBottomMenu = {
                TabsBottomBar(
                    tabState = tabState,
                    shape = shape,
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth()
                        .clip(shape)
                        .applyIf(useBlur) {
                            hazeChild(
                                state = hazeState,
                                shape = shape,
                                style = HazeStyle(
                                    tint = backgroundColor,
                                    blurRadius = 4.dp,
                                    noiseFactor = 0f,
                                ),
                            )
                        }
                        .applyIf(!useBlur) {
                            background(
                                backgroundColor,
                                shape
                            )
                        }
                )
            },
            portraitLeftMenu = {
                TabsLeftBar(
                    tabState = tabState,
                    shape = shape,
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxHeight()
                        .clip(shape)
                        .let { m ->
                            if (useBlur) m.hazeChild(
                                state = hazeState,
                                shape = shape,
                                style = HazeStyle(
                                    tint = backgroundColor,
                                    blurRadius = 4.dp,
                                    noiseFactor = 0f,
                                ),
                            ) else m.background(
                                backgroundColor,
                                shape
                            )
                        }
                )
            }
        )
        DialPad(
            modifier = Modifier
                .padding(end = 4.dp, start = 4.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            phoneNumber = phoneNumber,
//            visible = !dialPadVisible,
        )
    }
}
