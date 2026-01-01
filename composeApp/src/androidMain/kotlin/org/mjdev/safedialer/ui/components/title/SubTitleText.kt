package org.mjdev.safedialer.ui.components.title

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Previews
@Composable
fun SubTitleText(
    userName: String = BuildConfig.SERVER_UNAME
) = AppTheme {
    val isVisible: Boolean = remember(BuildConfig.SERVER) {
        BuildConfig.SERVER.isNotEmpty()
    }
    if (isVisible) Text(
        text = userName,
        color = MaterialTheme.colorScheme.primaryContainer,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
}