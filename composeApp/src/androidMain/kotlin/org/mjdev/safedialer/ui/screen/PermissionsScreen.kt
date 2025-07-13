package org.mjdev.safedialer.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mjdev.safedialer.ui.components.TitleBar
import org.mjdev.safedialer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PermissionsScreen(
    textStyle: TextStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary,
    ),
    fontFamily: FontFamily = FontFamily.Default,
) = AppTheme {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .background(MaterialTheme.colorScheme.background),
            topBar = {
                TitleBar(
                    showActions = false,
                    expanded = false
                )
            },
            bottomBar = { }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(64.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .size(128.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .align(Alignment.CenterHorizontally)
                            .padding(4.dp),
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vitejte",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        style = textStyle,
                        fontFamily = fontFamily,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aplikace potřebuje oprávnění, aby mohla fungovat správně.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        style = textStyle,
                        fontFamily = fontFamily,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
