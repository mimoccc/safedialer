package org.mjdev.safedialer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.navigation.Tabs
import org.mjdev.safedialer.ui.components.TabsState.Companion.rememberTabsState

@Previews
@Composable
fun TabsBottomBar(
    modifier: Modifier = Modifier,
    tabState: TabsState = rememberTabsState(Tabs.entries),
) = AnimatedVisibility(
    modifier = modifier,
    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
    visible = tabState.isVisible,
) {
    TabRow(
        containerColor = Color.Transparent,
        modifier = modifier,
        indicator = { tabPositions -> Box {} },
        divider = { Box {} },
        selectedTabIndex = tabState.currentTab?.ordinal ?: 0,
    ) {
        tabState.tabs.forEachIndexed { index, tab ->
            val selected = tabState.currentTab?.ordinal == index
            Tab(
                modifier =
                    Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (selected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            } else {
                                Color.Transparent
                            },
                        ),
                text = {
                    Text(
                        text = tab.toString(),
                        maxLines = 1,
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.primary,
                selected = selected,
                onClick = {
                    tabState.currentTab = tabState.tabs[index]
                },
            )
        }
    }
}

class TabsState(
    val tabs: List<Enum<*>>,
    startTab: Enum<*>?,
    isVisible: Boolean = true,
) {
    val currentTabState = mutableStateOf(startTab ?: tabs.firstOrNull())
    val visibleState = mutableStateOf(isVisible)
    var currentTab
        get() = currentTabState.value
        set(value) {
            currentTabState.value = value
        }
    var isVisible
        get() = visibleState.value
        set(value) {
            visibleState.value = value
        }

    companion object {
        @Composable
        fun rememberTabsState(
            tabs: List<Enum<*>> = emptyList(),
            startTab: Enum<*>? = tabs.firstOrNull(),
        ) = remember(tabs, startTab) {
            TabsState(tabs, startTab)
        }
    }
}
