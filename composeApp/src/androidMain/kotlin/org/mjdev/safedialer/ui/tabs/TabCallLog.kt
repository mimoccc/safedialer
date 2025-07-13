package org.mjdev.safedialer.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mjdev.safedialer.data.ContactsRepository
import org.mjdev.safedialer.data.ContactsRepository.Companion.EmptyMap
import org.mjdev.safedialer.data.ContactsRepository.Companion.rememberContactsRepository
import org.mjdev.safedialer.data.model.CallModel
import org.mjdev.safedialer.extensions.MapFilter
import org.mjdev.safedialer.data.list.IListItem
import org.mjdev.safedialer.ui.components.MappedList
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.Date

@Suppress("DEPRECATION", "UNCHECKED_CAST")
@Preview
@Composable
fun TabCallLog(
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
    contactsRepository: ContactsRepository? = rememberContactsRepository(),
) {
    val callLogMap = contactsRepository?.callLogMap?.collectAsState(LinkedHashMap())
    val filter: MapFilter<CallModel> = remember {
        { m, s ->
            m.values.flatten().filter { item ->
                item.displayName.contains(s, true)
            }.groupBy { c ->
                Date(c.date).let {
                    "${it.date}.${it.month + 1}.${it.year + 1900}"
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MappedList(
            modifier = Modifier.fillMaxSize(),
            mapData = callLogMap?.value ?: EmptyMap,
            showDate = true,
            scrollState = scrollState,
            filterText = filterText,
            filter = filter as MapFilter<IListItem>
        )
    }
}
