package org.mjdev.safedialer.ui.components.title

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.mjdev.safedialer.R
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Previews
@Composable
fun TitleText() = AppTheme {
    Text(
        text = stringResource(id = R.string.app_name),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}