package org.mjdev.safedialer.ui.components.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun ContactInfo(
    modifier: Modifier = Modifier,
    title: String = "Contact name",
    subtitle: String = "Contact detail",
    fontFamily: FontFamily? = null
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(4.dp)
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontFamily = fontFamily,
        fontSize = 16.sp,
        maxLines = 1,
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.7f),
        fontFamily = fontFamily,
        fontSize = 12.sp,
        maxLines = 1,
    )
}