package org.mjdev.safedialer.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.webdav.WebDavClient

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
    canExpand: Boolean = true,
    visible: Boolean = true,
    context: Context = LocalContext.current,
    onNavigationIconClick: () -> Unit = {},
    onServeClick: () -> Unit = {}
) {
    if (!visible) return
    val collapsed = remember(titleBarState.collapsedFraction) {
        (titleBarState.collapsedFraction == 1f && canExpand).not()
    }
    var userPic: ImageBitmap? by remember { mutableStateOf(null) }
    Box {
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
                if (collapsed) SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    textSize = 20.sp,
                    filterText = filterText,
                    focusRequester = focusRequester
                )
            },
            navigationIcon = {
                NavigationIcon(
                    modifier = Modifier.padding(start = 8.dp),
                    size = 64.dp,
                    imageBitmap = userPic
                ) {
                    onNavigationIconClick()
                }
            },
            actions = {
                NavigationActions(
                    showActions,
                    onServeClick = onServeClick
                )
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 80.dp, end = 4.dp, top = 10.dp)
        ) {
            TitleText()
            SubTitleText()
        }
    }
    // todo move to view model
    LaunchedEffect(visible) {
        runCatching {
            // todo remove and use di
            WebDavClient(context).userPicture.collectLatest { pic ->
                userPic = pic
            }
        }.onFailure { e ->
            e.printStackTrace()
        }
    }
}
