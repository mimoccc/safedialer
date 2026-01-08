package org.mjdev.phone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mjdev.phone.extensions.CustomExtensions.rememberAssetImage
import org.mjdev.phone.helpers.Previews

@Previews
@Suppress("unused")
@Composable
fun BackgroundLayout(
    modifier: Modifier = Modifier
) = Box(modifier = modifier) {
    Image(
        modifier = Modifier.fillMaxSize(),
        bitmap = rememberAssetImage("avatar_transparent.png"),
        contentDescription = "",
    )
    BrushedBox(
        modifier = Modifier.fillMaxSize(),
    )
}