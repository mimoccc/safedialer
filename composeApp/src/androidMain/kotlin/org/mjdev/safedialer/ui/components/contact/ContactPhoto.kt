package org.mjdev.safedialer.ui.components.contact

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.extensions.ComposeExt.rememberImageLoader
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Previews
@Composable
fun ContactPhoto(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(),
    shape: Shape = CircleShape,
    contact: ListItem? = null,
    onClick: () -> Unit = {}
) = AppTheme() {
    Box(
        modifier = modifier.clickable { onClick() }
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
        if (contact?.itemPhoto != null) Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context).data(
                    contact.itemPhoto
                ).build(),
                imageLoader = imageLoader
            ),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}
