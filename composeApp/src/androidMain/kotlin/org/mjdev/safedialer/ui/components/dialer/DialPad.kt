package org.mjdev.safedialer.ui.components.dialer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Previews
@Composable
fun DialPad(
    modifier: Modifier = Modifier,
    phoneNumber: MutableState<String> = remember { mutableStateOf("") },
    visible: Boolean = isInPreviewMode,
    context: Context = LocalContext.current,
    onNumberPressed: (String) -> Unit = { digit ->
        phoneNumber.value += digit
    },
    onDialPressed: (String) -> Unit = {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("call:${phoneNumber.value}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
) = AppTheme {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(animationSpec = tween(300)) { fullHeight -> fullHeight },
            exit = slideOutVertically(animationSpec = tween(300)) { fullHeight -> fullHeight }
        ) {
            val shape =RoundedCornerShape(32.dp)
            Box(
                modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.background, shape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, shape)
                    .padding(12.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = phoneNumber.value,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { key ->
                                TextButton(
                                    onClick = {
                                        onNumberPressed(key)
                                    },
                                    modifier = Modifier.size(64.dp),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.headlineMedium,
                                        )
                                        val subText = when (key) {
                                            "2" -> "ABC"
                                            "3" -> "DEF"
                                            "4" -> "GHI"
                                            "5" -> "JKL"
                                            "6" -> "MNO"
                                            "7" -> "PQRS"
                                            "8" -> "TUV"
                                            "9" -> "WXYZ"
                                            else -> ""
                                        }
                                        if (subText.isNotEmpty()) {
                                            Text(
                                                text = subText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 66.dp, end = 66.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (phoneNumber.value.isNotEmpty()) {
                                    phoneNumber.value = phoneNumber.value.dropLast(1)
                                }
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.Default.ContactPhone,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = {
                                onDialPressed(phoneNumber.value)
                                phoneNumber.value = ""
                            },
                            modifier = Modifier.size(74.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF12B76A)
                            )
                        ) {
                            Icon(
                                modifier = Modifier.size(64.dp),
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                if (phoneNumber.value.isNotEmpty()) {
                                    phoneNumber.value = phoneNumber.value.dropLast(1)
                                }
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
