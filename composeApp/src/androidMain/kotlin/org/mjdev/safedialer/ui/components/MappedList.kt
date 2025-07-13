package org.mjdev.safedialer.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import org.mjdev.safedialer.data.ContactsRepository.Companion.EmptyMap
import org.mjdev.safedialer.data.list.IListItem
import org.mjdev.safedialer.data.Mapper.asListItem
import org.mjdev.safedialer.extensions.ComposeExt1.rememberImageLoader
import org.mjdev.safedialer.extensions.MapFilter

@Preview
@Composable
fun MappedList(
    modifier: Modifier = Modifier.fillMaxSize(),
    mapData: Map<String, List<IListItem>> = EmptyMap,
    textStyle: TextStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp
    ),
    fontFamily: FontFamily = FontFamily.Default,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(context),
    showDate: Boolean = false,
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
    filter: MapFilter<IListItem> = { m, s -> m },
) {
    val filteredData = remember(filterText.value, mapData) {
        if(filterText.value.trim().length > 0) {
            filter(mapData, filterText.value)
        } else mapData
    }
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = 8.dp,
                end = 2.dp
            ),
        state = scrollState,
    ) {
        filteredData.map { entry ->
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 12.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Text(
                        text = entry.key,
                        style = textStyle,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                    )
                }
            }
            items(
                entry.value.size
            ) { index ->
                ContactDetail(
                    modifier = Modifier.fillMaxWidth(),
                    contact = entry.value[index].asListItem(),
                    textStyle = textStyle,
                    fontFamily = fontFamily,
                    imageLoader = imageLoader,
                    isFirst = index == 0,
                    isLast = index == entry.value.size - 1,
                    showDate = showDate
                )
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.0f))
                    .height(64.dp)
            )
        }
    }
}
