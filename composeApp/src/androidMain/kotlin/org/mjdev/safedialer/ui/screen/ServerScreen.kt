package org.mjdev.safedialer.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mjdev.safedialer.data.repository.DataRepository
import org.mjdev.safedialer.extensions.ComposeExt1.rememberViewModelSafe
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.server.ManagementServer
import org.mjdev.safedialer.server.ManagementServer.Companion.rememberCallServer
import org.mjdev.safedialer.ui.components.TitleBar
import org.mjdev.safedialer.ui.theme.AppTheme
import org.mjdev.safedialer.viewmodel.MainViewModel
import kotlin.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Previews
@Composable
fun ServerScreen(
    textStyle: TextStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary,
    ),
    fontFamily: FontFamily = FontFamily.Default,
    visibleState: State<Boolean> = remember { mutableStateOf(true) },
) {
    val server: ManagementServer = rememberCallServer()
    val context: Context = LocalContext.current
    val viewModel by rememberViewModelSafe {
        MainViewModel(DataRepository(context))
    }
    var httpAddress by remember { mutableStateOf("") }
    AppTheme {
        AnimatedVisibility(
            visible = visibleState.value,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
        ) {
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
                            showActions = true,
                            canExpand = false,
                            onServeClick = {
                                viewModel.toggleServerState()
                            }
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
                                text = "PC management",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                style = textStyle,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Je nastarovano sdileni na PC.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                style = textStyle,
                                fontFamily = fontFamily,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = httpAddress,
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
            // todo check if works
            LaunchedEffect(visibleState.value) {
                when (visibleState.value) {
                    true -> server.startServer { server, address ->
                        Log.d("MainActivity", "Server started at : $address")
                        httpAddress = address
                    }

                    false -> server.stopServer {
                        Log.d("MainActivity", "Server stopped")
                    }
                }
            }
        }
    }
}
