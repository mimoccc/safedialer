package org.mjdev.phone.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import org.mjdev.phone.extensions.CustomExtensions.isPortrait
import org.mjdev.phone.extensions.CustomExtensions.rememberAssetImagePainter
import org.mjdev.phone.helpers.Previews
import org.mjdev.phone.ui.theme.base.PhoneTheme
import org.mjdev.phone.ui.theme.base.phoneColors

@Previews
@Suppress("unused")
@Composable
fun BackgroundLayout(
    modifier: Modifier = Modifier,
    assetImageFile: String = "avatar_transparent.png",
    imagePainter: Painter = rememberAssetImagePainter(assetImageFile = assetImageFile),
    contentDescription: String = "",
) = PhoneTheme {
    Box(
        modifier = modifier.background(phoneColors.colorBackground)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = imagePainter,
            contentDescription = contentDescription,
            contentScale = if (isPortrait) ContentScale.Inside else ContentScale.FillHeight
        )
        BrushedBox(
            modifier = Modifier.fillMaxSize(),
        )
    }
}
