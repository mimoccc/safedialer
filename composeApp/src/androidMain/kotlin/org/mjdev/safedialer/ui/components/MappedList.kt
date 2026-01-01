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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import org.mjdev.safedialer.extensions.ComposeExt.rememberImageLoader
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.ui.components.contact.ContactDetail
import org.mjdev.safedialer.ui.theme.AppTheme

typealias MapFilter<T> = (Map<String, List<T>>, String) -> Map<String, List<T>>

@Previews
@Composable
fun MappedList(
    modifier: Modifier = Modifier.fillMaxSize(),
    mapData: Map<String, List<Entity>> = emptyMap(),
    textStyle: TextStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp
    ),
    fontFamily: FontFamily = FontFamily.Default,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(),
    showDate: Boolean = false,
    scrollState: LazyListState = rememberLazyListState(),
    filterText: MutableState<String> = remember { mutableStateOf("") },
    filter: MapFilter<Entity> = { m, s -> m },
) = AppTheme {
    val filteredData = remember(filterText.value, mapData) {
        if (filterText.value.trim().isNotEmpty()) {
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
                    item = entry.value[index],
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
