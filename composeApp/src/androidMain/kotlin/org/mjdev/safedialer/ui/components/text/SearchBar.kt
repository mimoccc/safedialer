package org.mjdev.safedialer.ui.components.text

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Previews
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    filterText: MutableState<String> = remember { mutableStateOf("") },
    focusRequester: FocusRequester = remember { FocusRequester() },
    textSize: TextUnit = 14.sp,
) = AppTheme {
    SearchField(
        modifier = modifier.focusRequester(focusRequester),
        textState = filterText,
        textSize = textSize,
        onClearClick = { focusRequester.freeFocus() },
        unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer.copy(
            alpha = 0.3f
        ),
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        leadingIcon = { size ->
            Image(
                imageVector = Icons.Filled.Search,
                contentDescription = "",
                modifier = Modifier.size(size),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    )
}
