package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleSearch(
    titleBarState: TopAppBarState = rememberTopAppBarState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    SearchField(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(1f - titleBarState.collapsedFraction)
            .size((48.dp.value * (1f - titleBarState.collapsedFraction)).dp)
            .wrapContentHeight()
            .focusRequester(focusRequester),
        textState = filterText,
        onClearClick = { focusRequester.freeFocus() },
        unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer.copy(
            alpha = 0.3f
        ),
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        leadingIcon = {
            Image(
                imageVector = Icons.Filled.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    )
}