package org.mjdev.safedialer.ui.components.contact

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.extensions.ComposeExt.rememberImageLoader
import org.mjdev.safedialer.extensions.CustomExt.isPreview
import org.mjdev.safedialer.extensions.CustomExt.rememberAssetImage
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.components.image.ImageCard

@Suppress("ParamsComparedByRef")
@Previews
@Composable
fun ContactBackground(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(),
    contact: ListItem? = null,
    photo: Any? = contact?.itemPhoto,
    mainBckColor: Color = MaterialTheme.colorScheme.background,
    dominantColor: MutableState<Color> = remember { mutableStateOf(mainBckColor) },
    colorExtracted: (Color?) -> Unit = {},
    roundCornerSize: Dp = 0.dp,
    shape: Shape = RectangleShape,
    showBckImage: Boolean = false,
    showShadows: Boolean = false
) = Box(
    modifier = modifier
        .background(
            dominantColor.value,
            shape
        )
        .clip(shape)
) {
    val bitmap = if (isPreview) rememberAssetImage("avatar_yellow.png")
    else if (photo is ImageVector) rememberVectorPainter(photo)
    else rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(photo)
            .allowHardware(false)
            .build(),
        imageLoader = imageLoader,
        onSuccess = { state ->
            state.result.image
                .toBitmap()
                .copy(Bitmap.Config.ARGB_8888, true)
                .let { bitmap ->
                    Palette.from(bitmap).generate { palette ->
                        dominantColor.value = palette?.dominantSwatch
                            ?.rgb
                            ?.let {
                                Color(it)
                            } ?: mainBckColor
                        colorExtracted(dominantColor.value)
                    }
                }
        }
    )
    ImageCard(
        modifier = Modifier.fillMaxSize(),
        source = bitmap,
        roundCornerSize = roundCornerSize,
        title = null,
        subtitle = null,
        showImage = showBckImage,
        showShadows = showShadows,
    )
}
