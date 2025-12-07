package org.mjdev.safedialer.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.extensions.ColorExt.darker
import org.mjdev.safedialer.extensions.ColorExt.lighter
import org.mjdev.safedialer.extensions.ComposeExt1.rememberImageLoader
import org.mjdev.safedialer.extensions.CustomExt.isPreview
import org.mjdev.safedialer.helpers.Previews

@Previews
@Composable
fun ContactBackground(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(context),
    shape: Shape = RectangleShape,
    shadingAlpha: Float = 0.6f,
    contact: ListItem? = null,
    dominantColor  : MutableState<Color> = remember { mutableStateOf(Color.Transparent) },
    colorExtracted: (Color?) -> Unit = {}
) = Box(
    modifier = modifier//.background(dominantColor.value, shape)
) {
    val photo: Any? = if (isPreview) Icons.Default.AccountCircle else contact?.itemPhoto
    val painter = if (photo is ImageVector) rememberVectorPainter(photo)
    else rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(photo)
            .allowHardware(false)
            .build(),
        imageLoader = imageLoader,
        onSuccess = { state ->
            (state.result.drawable as? BitmapDrawable)?.bitmap?.copy(
                Bitmap.Config.ARGB_8888,
                true
            )?.let { bitmap ->
                Palette.from(bitmap).generate { palette ->
                    dominantColor.value = palette?.dominantSwatch?.rgb?.let {
                        Color(it)
                    } ?: Color.Transparent
                    colorExtracted(dominantColor.value)
                }
            }
        }
    )
    Image(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape),
        painter = painter,
        colorFilter = dominantColor.value.let { tintColor ->
            ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(0f)
                }
            ).let {
                ColorFilter.tint(tintColor.copy(alpha = 0.7f), BlendMode.Modulate)
            }
        },
        contentDescription = "",
        contentScale = ContentScale.Crop,
    )
    PhotoGradientBox(
        modifier = Modifier.fillMaxSize(),
        startColor = dominantColor.value.lighter(0.5f),
        endColor = dominantColor.value.darker(0.5f),
        shape = shape
    )
}
