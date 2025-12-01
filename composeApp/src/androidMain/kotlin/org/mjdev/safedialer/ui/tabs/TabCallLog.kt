package org.mjdev.safedialer.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mjdev.safedialer.extensions.ComposeExt1.rememberViewModelSafe
import org.mjdev.safedialer.extensions.DateExt.formatDate
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.ui.components.MapFilter
import org.mjdev.safedialer.ui.components.MappedList
import org.mjdev.safedialer.viewmodel.MainViewModel

@Suppress( "UNCHECKED_CAST")
@Previews
@Composable
fun TabCallLog(
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
) {
    val viewModel by rememberViewModelSafe { context ->
        MainViewModel(MockDataRepository(context))
    }
    val callsMap by viewModel.callsMap.collectAsState(LinkedHashMap())
    val filter: MapFilter<Call> = remember {
        { m, s ->
            m.values.flatten().filter { i ->
                i.toString().contains(s, true)
            }.groupBy { c ->
                c.callDate.formatDate()
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MappedList(
            modifier = Modifier.fillMaxSize(),
            mapData = callsMap,
            showDate = true,
            scrollState = scrollState,
            filterText = filterText,
            filter = filter as MapFilter<Entity>
        )
    }
}
