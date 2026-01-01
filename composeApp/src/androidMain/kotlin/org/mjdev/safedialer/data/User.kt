package org.mjdev.safedialer.data

import androidx.compose.ui.graphics.ImageBitmap
import org.mjdev.safedialer.BuildConfig

@Suppress("unused")
data class User (
    val name: String = "",
    val picture: ImageBitmap? = null,
    val emails: List<String> = emptyList(),
) {
    val appName: String = BuildConfig.APP_NAME
    val email: String = BuildConfig.SERVER_UNAME
}