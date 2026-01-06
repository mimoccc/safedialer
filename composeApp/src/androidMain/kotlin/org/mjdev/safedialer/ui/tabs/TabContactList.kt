package org.mjdev.safedialer.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mjdev.safedialer.extensions.ComposeExt.rememberViewModelSafe
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.ui.components.list.MappedList
import org.mjdev.safedialer.ui.theme.AppTheme
import org.mjdev.safedialer.viewmodel.MainViewModel

@Suppress("UNCHECKED_CAST")
@Previews
@Composable
fun TabContactList(
    scrollState: LazyListState = rememberLazyListState(),
    filterText: State<String> = remember { mutableStateOf("") },
) = AppTheme {
    val viewModel by rememberViewModelSafe { context ->
        MainViewModel(context, MockDataRepository(context))
    }
    val contactMap by viewModel.contactMap.collectAsState(LinkedHashMap())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MappedList(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            mapData = contactMap,
            scrollState = scrollState,
            filterText = filterText,
        )
    }
}
