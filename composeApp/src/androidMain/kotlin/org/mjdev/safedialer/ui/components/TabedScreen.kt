package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import org.mjdev.safedialer.navigation.Tabs
import org.mjdev.safedialer.ui.components.TabsState.Companion.rememberTabsState

@Preview
@Composable
fun TabbedScreen(
    modifier: Modifier = Modifier,
    startTab: Tabs = Tabs.CallLog,
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
    tabState: TabsState = rememberTabsState(Tabs.asList(), startTab),
    hazeState: HazeState = remember { HazeState() },
    backgroundColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
    shape: RoundedCornerShape = RoundedCornerShape(50),
    useBlur: Boolean = false
) = Box(
    modifier = modifier
        .padding(2.dp)
        .fillMaxSize(),
    contentAlignment = Alignment.BottomCenter
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .let { m ->
                if (useBlur) m.haze(hazeState)
                else m
            }
    ) {
        tabState.currentTab?.content(scrollState, filterText)
    }
    TabsBottomBar(
        tabState = tabState,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                2.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape
            )
            .let { m ->
                if (useBlur) m.hazeChild(
                    state = hazeState,
                    shape = shape,
                    style = HazeStyle(
                        tint = backgroundColor,
                        blurRadius = 4.dp,
                        noiseFactor = 0f,
                    )
                ) else m.background(
                    backgroundColor,
                    shape
                )
            }
    )
}
