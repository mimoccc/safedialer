package org.mjdev.safedialer.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mjdev.safedialer.data.custom.MessageThread
import org.mjdev.safedialer.extensions.ComposeExt1.rememberViewModelSafe
import org.mjdev.safedialer.extensions.DateExt.formatDate
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.ui.components.MapFilter
import org.mjdev.safedialer.ui.components.MappedList
import org.mjdev.safedialer.viewmodel.MainViewModel
import java.util.Date

@Suppress("DEPRECATION", "UNCHECKED_CAST")
@Preview
@Composable
fun TabMessages(
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
) {
    val viewModel by rememberViewModelSafe { context ->
        MainViewModel(MockDataRepository(context))
    }
    val messagesMap by viewModel.messagesMap.collectAsState(LinkedHashMap())
    val filter: MapFilter<MessageThread> = remember {
        { m, s ->
            m.values.flatten().filter { i ->
                i.displayName?.contains(s, true) ?: false
            }.groupBy { c ->
                c.date.formatDate()
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MappedList(
            modifier = Modifier.fillMaxSize(),
            mapData = messagesMap,
            showDate = true,
            scrollState = scrollState,
            filterText = filterText,
            filter = filter as MapFilter<Entity>
        )
    }
}
