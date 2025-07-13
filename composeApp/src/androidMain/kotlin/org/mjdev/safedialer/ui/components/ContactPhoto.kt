package org.mjdev.safedialer.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.extensions.ComposeExt1.rememberImageLoader

@Preview
@Composable
fun ContactPhoto(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(context),
    contact: ListItem
) {
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context).data(
                    contact.photoUri ?: contact.photoThumbnailUri
                ).build(),
                imageLoader = imageLoader
            ),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}
