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
import org.mjdev.safedialer.data.ContactsRepository.Companion.rememberContactsRepository
import org.mjdev.safedialer.data.model.MessageModel
import org.mjdev.safedialer.extensions.MapFilter
import org.mjdev.safedialer.data.list.IListItem
import org.mjdev.safedialer.ui.components.MappedList
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.Date

@Suppress("DEPRECATION", "UNCHECKED_CAST")
@Preview
@Composable
fun TabMessages(
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
    contactsRepository: ContactsRepository? = rememberContactsRepository(),
) {
    val messageMap = contactsRepository?.messagesMap?.collectAsState(LinkedHashMap())
    val filter: MapFilter<MessageModel> = remember {
        { m, s ->
            m.values.flatten().filter { i ->
                i.displayName.contains(s, true)
            }.groupBy { c ->
                Date(c.date).let {
                    "${it.date}.${it.month + 1}.${it.year + 1900}"
                }
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
            mapData = messageMap?.value ?: ContactsRepository.Companion.EmptyMap,
            showDate = true,
            scrollState = scrollState,
            filterText = filterText,
            filter = filter as MapFilter<IListItem>
        )
    }
}
