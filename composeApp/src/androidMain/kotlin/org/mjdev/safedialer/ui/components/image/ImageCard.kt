package org.mjdev.safedialer.ui.components.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mjdev.safedialer.extensions.CustomExt.isPreview
import org.mjdev.safedialer.extensions.CustomExt.rememberAssetImage
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Suppress("unused", "LocalVariableName", "ParamsComparedByRef")
@Previews
@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    source: Any? = rememberAssetImage("avatar_yellow.png"),
    title: String? = if (isPreview) "title" else null,
    subtitle: String? = if (isPreview) "subtitle" else null,
    titleFontSize: TextUnit = 20.sp,
    subtitleFontSize: TextUnit = 16.sp,
    roundCornerSize: Dp = 16.dp,
    textPadding: Dp = 12.dp,
    showImage: Boolean = true,
    lightAlpha: Float = 0.3f, // todo
    darkAlpha: Float = 0.3f,
    showShadows: Boolean= true, // todo
) = AppTheme {
    val ImageRendererBitmap: @Composable (ImageBitmap) -> Unit = { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    val ImageRendererVector: @Composable (ImageVector) -> Unit = { vector ->
        Image(
            imageVector = vector,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    val ImageRendererPainter: @Composable (Painter) -> Unit = { painter ->
        Image(
            painter = painter,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    // todo, more types
    val imageRenderer: @Composable (Any?) -> Unit = { src ->
        when (source) {
            null -> {}
            is ImageBitmap -> ImageRendererBitmap(source)
            is ImageVector -> ImageRendererVector(source)
            is Painter -> ImageRendererPainter(source)
            else -> {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = "No image renderer for type: ${source::class.simpleName}"
                )
            }
        }
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(roundCornerSize)
    ) {
        Box {
            if (showImage) {
                imageRenderer(source)
            }
            if(showShadows) Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.White.copy(alpha = 0.33f),
                                0.3f to Color.Transparent,
                                0.5f to Color.Transparent,
                                0.75f to Color.Black.copy(alpha = 0.5f),
                                1.0f to Color.Black.copy(alpha = 0.9f)
                            )
                        )
                        onDrawBehind {
                            drawRect(gradient)
                        }
                    }
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(textPadding)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = titleFontSize
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = subtitleFontSize
                    )
                }
            }
        }
    }
}
