package org.mjdev.safedialer.ui.screen

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.mjdev.safedialer.extensions.ComposeExt.canScroll
import org.mjdev.safedialer.extensions.ComposeExt.isLandscape
import org.mjdev.safedialer.extensions.ComposeExt.rememberViewModelSafe
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.sync.SyncAccountTypes
import org.mjdev.safedialer.ui.components.dialer.FabState.Companion.rememberFabState
import org.mjdev.safedialer.ui.components.dialer.FloatButton
import org.mjdev.safedialer.ui.components.tabs.TabbedScreen
import org.mjdev.safedialer.ui.components.title.TitleBar
import org.mjdev.safedialer.ui.state.TabsState
import org.mjdev.safedialer.ui.state.TabsState.Companion.rememberTabsState
import org.mjdev.safedialer.ui.theme.AppTheme
import org.mjdev.safedialer.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Previews
@Composable
fun MainScreen(
    startTab: SyncAccountTypes = SyncAccountTypes.CALL_LOG, // todo from past
) = AppTheme {
    val viewModel by rememberViewModelSafe { context ->
        MainViewModel(context,MockDataRepository(context))
    }
    val isTabsVisible by viewModel.isTabsVisible.collectAsState()
    val filterText = remember { mutableStateOf("") }
    val fabState = rememberFabState(isTabsVisible)
    val scrollState = rememberLazyListState()
    val tabState: TabsState = rememberTabsState(startTab = startTab)
    val serverState = viewModel.serverState.collectAsState()
    val titleBarState: TopAppBarState = rememberTopAppBarState()
    val titleScrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = titleBarState,
            canScroll = {
                scrollState.canScroll && filterText.value.trim().isEmpty()
            },
        )
    val floatingActionIcon: @Composable () -> Unit = {
        FloatButton(
            modifier = Modifier.padding(
                bottom = if (isLandscape) 48.dp else 0.dp
            ),
            fabState = fabState,
            onClick = {
                fabState.isVisible = !fabState.isVisible
            },
        )
    }
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
                    filterText = filterText,
                    onServeClick = {
                        viewModel.toggleServerState()
                    },
                )
            },
            bottomBar = { },
            floatingActionButton = floatingActionIcon,
        ) { padding ->
            TabbedScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                scrollState = scrollState,
                tabState = tabState,
                fabState = fabState,
                filterText = filterText,
            )
        }
        ServerScreen(
            visibleState = serverState
        )
    }
    LaunchedEffect(fabState.isVisible) {
        tabState.isVisible = fabState.isVisible
        viewModel.setTabsVisible(fabState.isVisible)
    }
}
