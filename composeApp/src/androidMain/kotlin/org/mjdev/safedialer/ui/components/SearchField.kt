/*
 * Copyright (c) Milan Jurkulák 2024.
 *  Contact:
 *  e: mimoccc@gmail.com
 *  e: mj@mjdev.org
 *  w: https://mjdev.org
 */

package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Preview
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    textState: MutableState<String> = mutableStateOf(""),
    textColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = TextStyle.Default,
    textSize: TextUnit = 13.sp,
    enabled: Boolean = true,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: @Composable (() -> Unit)? = null,
    onClearClick: () -> Unit = {},
) = SelectableOutlineEditText(
    value = textState.value,
    onValueChange = { v: String -> textState.value = v },
    modifier = modifier,
    enabled = enabled,
    singleLine = true,
    textStyle = textStyle,
    textSize = textSize,
    colors = outlinedTextFieldColors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor.copy(alpha = 0.3f),
        cursorColor = textColor,
        errorCursorColor = textColor,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        disabledBorderColor = Color.Transparent,
        errorBorderColor = Color.Transparent
    ),
    leadingIcon = leadingIcon,
    trailingIcon = {
        if (textState.value.isNotEmpty()) {
            Icon(
                modifier = Modifier
                    .padding(2.dp)
                    .size(24.dp)
                    .clickable {
                        textState.value = ""
                        onClearClick()
                    },
                imageVector = Icons.Filled.Clear,
                tint = textColor,
                contentDescription = ""
            )
        }
    }
)
