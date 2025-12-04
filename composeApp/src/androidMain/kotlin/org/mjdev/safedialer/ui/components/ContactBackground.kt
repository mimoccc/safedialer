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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.extensions.ComposeExt1.rememberImageLoader
import org.mjdev.safedialer.extensions.CustomExt.isPreview

@Preview
@Composable
fun ContactBackground(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(context),
    shape: Shape = RectangleShape,
    shadingAlpha: Float = 0.6f,
    contact: ListItem? = null,
    colorExtracted: (Color?) -> Unit = {}
) = Box(
    modifier = modifier
) {
    var dominantColor by remember { mutableStateOf<Color?>(Color.Black) }
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
                    dominantColor = palette?.dominantSwatch?.rgb?.let { Color(it) }
                    colorExtracted(dominantColor)
                }
            }
        }
    )
    Image(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape),
        painter = painter,
        colorFilter = dominantColor?.let { tintColor ->
            ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToSaturation(0f)
                }
            ).let {
                ColorFilter.tint(tintColor.copy(alpha = 0.7f), BlendMode.Modulate)
            }
        } ?: ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
        contentDescription = "",
        contentScale = ContentScale.Crop,
    )
    GradientBox(
        modifier = Modifier.fillMaxSize(),
        startColor = dominantColor?.copy(alpha = shadingAlpha) ?: Color.Transparent,
        endColor = dominantColor?.copy(alpha = shadingAlpha) ?: Color.Transparent,
        shape = shape
    )
}

@Composable
fun GradientBox(
    modifier: Modifier = Modifier,
    startColor: Color = Color.Transparent,
    endColor: Color = Color.Black,
    shape: Shape = RectangleShape,
//    radialAlpha: Float = 0.1f,
    verticalAlpha: Float = 0.2f,
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        startColor.copy(alpha = verticalAlpha),
                        Color.Transparent,
                        Color.Transparent,
                        endColor.copy(alpha = verticalAlpha),
                        endColor.copy(alpha = verticalAlpha),
                    )
                ),
                shape = shape
            )
    )
//    Box(
//        modifier = modifier
//            .background(
//                Brush.radialGradient(
//                    colors = listOf(
//                        Color.Transparent,
//                        Color.Transparent,
//                        Color.Transparent,
//                        Color.Transparent,
//                        Color.Transparent,
//                        Color.Transparent,
//                        endColor.copy(alpha = 0.1f),
//                        endColor.copy(alpha = 0.3f),
//                        endColor.copy(alpha = 0.5f),
//                    ),
//                ),
//                shape = shape
//            ).alpha(radialAlpha)
//    )
}