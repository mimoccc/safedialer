package org.mjdev.safedialer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.navigation.Tabs
import org.mjdev.safedialer.ui.components.TabsState.Companion.rememberTabsState

@Previews
@Composable
fun TabsBottomBar(
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    shape: Shape = RoundedCornerShape(50),
    tabState: TabsState = rememberTabsState(),
) = AnimatedVisibility(
    modifier = modifier,
    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
    visible = tabState.isVisible,
) {
    Row(
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = shape,
            )
            .fillMaxWidth()
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabState.tabs.forEachIndexed { index, tab ->
            val selected = tabState.currentTab?.ordinal == index
            Column(
                modifier = if (selected) {
                    Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        .height(iconSize)
                        .weight(1f)
                } else {
                    Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Transparent)
                        .size(iconSize)
                },
            ) {
                Row(
                    modifier = Modifier.apply {
                        if (selected) padding(start = 4.dp)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .clickable {
                                tabState.currentTab = tabState.tabs[index]
                            }
                            .background(
                                color = if (!selected)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    Color.Transparent,
                                shape = CircleShape,
                            )
                            .size(iconSize)
                            .padding(8.dp),
                        // todo generalize
                        imageVector = (tab as? Tabs)?.icon ?: Icons.Default.Apps,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(
                            color = MaterialTheme.colorScheme.primary,
                        )
                    )
                    if (selected) {
                        Text(
                            modifier = Modifier,
                            text = tab.toString(),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
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
            tabs: List<Enum<*>> = Tabs.entries.toMutableList().apply {
                val isServer = BuildConfig.SERVER.isNotEmpty()
                val isUser = BuildConfig.SERVER_UNAME.isNotEmpty()
                val isPass = BuildConfig.SERVER_UPASS.isNotEmpty()
                if (!(isServer && isUser && isPass)) remove(Tabs.Emails)
            },
            startTab: Enum<*>? = tabs.firstOrNull(),
        ) = remember(tabs, startTab) {
            TabsState(tabs, startTab)
        }
    }
}
