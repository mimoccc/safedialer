package org.mjdev.safedialer.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.extensions.ComposeExt1.canScroll
import org.mjdev.safedialer.extensions.ComposeExt1.diViewModel
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.navigation.Tabs
import org.mjdev.safedialer.service.IncomingCallService
import org.mjdev.safedialer.viewmodel.MainViewModel
import org.mjdev.safedialer.ui.components.FabState.Companion.rememberFabState
import org.mjdev.safedialer.ui.components.FloatButton
import org.mjdev.safedialer.ui.components.TabbedScreen
import org.mjdev.safedialer.ui.components.TabsState
import org.mjdev.safedialer.ui.components.TabsState.Companion.rememberTabsState
import org.mjdev.safedialer.ui.components.TitleBar

@OptIn(ExperimentalMaterial3Api::class)
@Previews
@Composable
fun MainScreen(
    context: Context = LocalContext.current,
    startTab: Tabs = Tabs.CallLog,
) {
    val viewModel: MainViewModel = diViewModel()
    val isTabsVisible by viewModel.isTabsVisible.collectAsState()
    val fabState = rememberFabState(isTabsVisible)
    val scrollState = rememberLazyListState()
    val tabState: TabsState = rememberTabsState(Tabs.entries, startTab)
    val filterText by viewModel.filterText.collectAsState()
    val serverState by viewModel.serverState.collectAsState()
    val titleBarState: TopAppBarState = rememberTopAppBarState()
    val titleScrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = titleBarState,
            canScroll = {
                scrollState.canScroll && filterText.trim().isEmpty()
            },
        )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .nestedScroll(fabState.nestedScrollConnection)
            .nestedScroll(titleScrollBehavior.nestedScrollConnection),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .background(MaterialTheme.colorScheme.background),
            topBar = {
                TitleBar(
                    showActions = true,
                    titleBarState = titleBarState,
                    scrollBehavior = titleScrollBehavior,
                    filterText = remember { mutableStateOf(filterText) }.apply {
                        value = filterText
                    },
                    onServeClick = {
                        viewModel.toggleServerState()
                    },
                )
            },
            bottomBar = { },
            floatingActionButton = {
                FloatButton(
                    modifier = Modifier.padding(bottom = 48.dp),
                    fabState = fabState,
                    onClick = {
                        IncomingCallService.showAlert(
                            context,
                            "+420702568909",
                        )
                    },
                )
            },
        ) { padding ->
            TabbedScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                scrollState = scrollState,
                tabState = tabState,
                filterText = remember {
                    mutableStateOf(filterText)
                }.apply { value = filterText },
            )
        }
        ServerScreen(
            serverState = remember {
                mutableStateOf(serverState)
            }.apply { value = serverState },
        )
    }
    LaunchedEffect(fabState.isVisible) {
        tabState.isVisible = fabState.isVisible
        viewModel.setTabsVisible(fabState.isVisible)
    }
}
