package org.mjdev.safedialer.ui.components.list

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import org.mjdev.safedialer.extensions.AppComposeExt.rememberMapFilter
import org.mjdev.safedialer.extensions.ComposeExt.rememberImageLoader
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.ui.components.contact.ContactDetail
import org.mjdev.safedialer.ui.theme.AppTheme

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
    imageLoader: ImageLoader = rememberImageLoader(),
    showDate: Boolean = false,
    scrollState: LazyListState = rememberLazyListState(),
    filterText: State<String> = remember { mutableStateOf("") },
    noItemsText: String = "List is empty"
) = AppTheme {
    val filteredData by rememberMapFilter(
        map = mapData,
        filterText = filterText.value,
    )
    val noItems by remember(filteredData) {
        derivedStateOf {
            filteredData.count() == 0
        }
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
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = noItems
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = noItemsText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
