package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SelectableOutlineEditText(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    textSize: TextUnit = TextUnit.Unspecified,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = RoundedCornerShape(16.dp),
    colors: TextFieldColors = outlinedTextFieldColors()
) = SelectionContainer(
    modifier = modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(fontSize = textSize),
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        minLines = minLines,
        shape = shape,
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("unused")
@Composable
fun SelectableOutlineEditText(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = RoundedCornerShape(16.dp),
    colors: TextFieldColors = outlinedTextFieldColors()
) = SelectionContainer(
    modifier = modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        minLines = minLines,
        shape = shape,
        colors = colors
    )
}
