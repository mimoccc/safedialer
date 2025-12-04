package org.mjdev.safedialer.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.sync.SyncAccountTypes

class TabsState(
    val tabs: List<Enum<*>>,
    startTab: Enum<*>?,
    isVisible: Boolean = true,
) {
    private val currentTabState = mutableStateOf(startTab ?: tabs.firstOrNull())
    private val visibleState = mutableStateOf(isVisible)
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
            tabs: List<Enum<*>> = SyncAccountTypes.entries.toList().filter { tab ->
                val isServer = BuildConfig.SERVER.isNotEmpty()
                val isUser = BuildConfig.SERVER_UNAME.isNotEmpty()
                val isPass = BuildConfig.SERVER_UPASS.isNotEmpty()
                val isLoggedIn = isServer && isUser && isPass
                if (tab.needLogon) isLoggedIn else true
            },
            startTab: Enum<*>? = tabs.firstOrNull(),
        ) = remember(tabs, startTab) {
            TabsState(tabs, startTab)
        }
    }
}