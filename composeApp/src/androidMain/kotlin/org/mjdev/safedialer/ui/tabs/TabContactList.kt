package org.mjdev.safedialer.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mjdev.safedialer.data.ContactsRepository
import org.mjdev.safedialer.data.ContactsRepository.Companion.EmptyMap
import org.mjdev.safedialer.data.ContactsRepository.Companion.rememberContactsRepository
import org.mjdev.safedialer.data.model.ContactModel
import org.mjdev.safedialer.extensions.MapFilter
import org.mjdev.safedialer.data.list.IListItem
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mjdev.safedialer.extensions.ComposeExt1.diViewModel
import org.mjdev.safedialer.ui.components.MappedList
import org.mjdev.safedialer.viewmodel.MainViewModel
import kotlin.collections.LinkedHashMap

@Suppress("UNCHECKED_CAST")
@Preview
@Composable
fun TabContactList(
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
) {
    val viewModel: MainViewModel = diViewModel()
    val contactMap = viewModel.contactMap.collectAsState(LinkedHashMap())
    val filter: MapFilter<ContactModel> = remember {
        { m, s ->
            m.values.flatten().filter { i ->
                i.displayName.contains(s, true)
            }.groupBy { c ->
                c.displayName.firstOrNull()?.uppercase() ?: ""
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
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            mapData = contactMap?.value ?: EmptyMap,
            showDate = false,
            scrollState = scrollState,
            filterText = filterText,
            filter = filter as MapFilter<IListItem>
        )
    }
}
