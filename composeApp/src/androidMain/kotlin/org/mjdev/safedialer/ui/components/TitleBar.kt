package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import org.mjdev.safedialer.helpers.Previews

@OptIn(ExperimentalMaterial3Api::class)
@Previews
@Composable
fun TitleBar(
    showActions: Boolean = true,
    titleBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        titleBarState
    ),
    filterText: MutableState<String> = remember { mutableStateOf("") },
    focusRequester: FocusRequester = remember { FocusRequester() },
    expanded: Boolean = true,
    onServeClick: () -> Unit = {}
) {
    if (expanded) {
        LargeTopAppBar(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background,
            ),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = MaterialTheme.colorScheme.primary
            ),
            scrollBehavior = scrollBehavior,
            title = {
                Column {
                    TitleText()
                    TitleSearch(
                        titleBarState = titleBarState,
                        filterText = filterText,
                        focusRequester = focusRequester
                    )
                }
            },
            navigationIcon = {
                NavigationIcon()
            },
            actions = {
                NavigationActions(
                    showActions,
                    onServeClick = onServeClick
                )
            }
        )
    } else {
        TopAppBar(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background,
            ),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = MaterialTheme.colorScheme.primary
            ),
            scrollBehavior = scrollBehavior,
            title = {
                TitleText()
            },
            navigationIcon = {
                NavigationIcon()
            },
            actions = {
                NavigationActions(
                    showActions,
                    onServeClick = onServeClick
                )
            }
        )
    }
}
