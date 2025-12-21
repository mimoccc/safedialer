package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.runBlocking
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.data.mapper.EntityMapper.asListItem
import org.mjdev.safedialer.extensions.ComposeExt1.applyIf
import org.mjdev.safedialer.extensions.ComposeExt1.rememberImageLoader
import org.mjdev.safedialer.extensions.ComposeExt1.rememberViewModelSafe
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.providers.android.calllog.CallType
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.service.IncomingCallService
import org.mjdev.safedialer.ui.shapes.DottedShape
import org.mjdev.safedialer.viewmodel.MainViewModel
import java.util.Date

@Suppress("DEPRECATION")
@Previews
@Composable
fun ContactDetail(
    modifier: Modifier = Modifier,
    caller: String? = null,
    item: Entity? = null,
    details: String? = null,
    buttons: @Composable (item: ListItem?) -> Unit = { i -> ContactButtonsDefault(i) },
    imageLoader: ImageLoader = rememberImageLoader(),
    textStyle: TextStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
    fontFamily: FontFamily = FontFamily.Default,
    showCloseButton: Boolean = false,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    showDate: Boolean = false,
    showDivider: Boolean = true,
    showBckImage: Boolean = true,
    hazeState: HazeState = remember { HazeState() },
    useBlur: Boolean = true,
    backgroundAlpha: Float = 0.8f,
) {
    val viewModel by rememberViewModelSafe { context ->
        MainViewModel(MockDataRepository(context))
    }
    val context = LocalContext.current
    val mainBckColor = MaterialTheme.colorScheme.background
    val secondaryColor = MaterialTheme.colorScheme.secondaryContainer
    val sBckColor = MaterialTheme.colorScheme.secondaryContainer
    var secondaryBckColor by remember { mutableStateOf(sBckColor) }
    val primaryColor = MaterialTheme.colorScheme.primary
//    val outlineColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.primary
    val background = RoundedCornerShape(
        topStart = if (isFirst) 16.dp else 0.dp,
        topEnd = if (isFirst) 16.dp else 0.dp,
        bottomEnd = if (isLast) 16.dp else 0.dp,
        bottomStart = if (isLast) 16.dp else 0.dp,
    )
    val contentBackground = RoundedCornerShape(
        topStart = if (isFirst) 16.dp else 0.dp,
        topEnd = if (isFirst) 16.dp else 0.dp,
        bottomEnd = 0.dp,
        bottomStart = 0.dp,
    )
    val backgroundDetails = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomEnd = if (isLast) 16.dp else 0.dp,
        bottomStart = if (isLast) 16.dp else 0.dp,
    )
    // todo remove blocking call
    val contact = runBlocking {
        item
            ?: viewModel.findContactByPhone(caller)
            ?: viewModel.findContactBySender(caller)
    }
    val listItem = contact?.asListItem()
    Box(
        modifier = modifier
            .wrapContentHeight()
            .background(
                color = mainBckColor,
                shape = background,
            ),
    ) {
        Box(
            modifier = Modifier.background(
                color = secondaryColor.copy(alpha = 0.3f),
                shape = background,
            ),
        ) {
            // todo custom image
            if (showBckImage) {
                ContactBackground(
                    modifier = Modifier
                        .matchParentSize()
                        .applyIf(useBlur) {
                            haze(hazeState)
                        },
                    imageLoader = imageLoader,
                    contact = listItem,
                    shape = background,
                    shadingAlpha = backgroundAlpha,
                    colorExtracted = { color ->
                        secondaryBckColor = color ?: sBckColor
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (listItem?.isBlocked == true || listItem?.isDanger == true) {
                                Color.Red.copy(alpha = 0.3f)
                            } else {
                                Color.Transparent
                            },
                            shape = contentBackground,
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    listItem?.itemCallType?.color ?: CallType.UNKNOW.color,
                                    CircleShape,
                                ),
                        ) {
                            ContactPhoto(
                                contact = listItem,
                                imageLoader = imageLoader,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxSize()
                                    .clip(CircleShape),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, true)
                                .padding(
                                    bottom = 16.dp,
                                    top = 16.dp,
                                ),
                        ) {
                            if (listItem?.itemName != null) Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = listItem.itemName!!.ifEmpty { "-" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                style = textStyle,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                color = textColor,
                            )
                            if (listItem?.itemPhone != null) Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = listItem.itemPhone!!.ifEmpty { "-" },
                                fontSize = 14.sp,
                                style = textStyle,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                color = textColor,
                            )
                            if (showDate && listItem?.itemDate != null) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = listItem.itemDate.takeIf { d ->
                                        d != 0L
                                    }?.let { d ->
                                        // todo formats from system
                                        val date = Date(d)
                                        val hours = date.hours.toString().padStart(2, '0')
                                        val minutes = date.minutes.toString().padStart(2, '0')
                                        val seconds = date.seconds.toString().padStart(2, '0')
                                        "$hours:$minutes:$seconds"
                                    } ?: "-",
                                    fontSize = 14.sp,
                                    style = textStyle,
                                    fontFamily = fontFamily,
                                    maxLines = 1,
                                    color = textColor,
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        if (showCloseButton.not()) {
                            IconButton(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .padding(top = 4.dp),
                                onClick = {
                                    // todo generate qr code
                                },
                            ) {
                                Image(
                                    modifier = Modifier
                                        .background(
                                            color = primaryColor.copy(alpha = 0.2f),
                                            shape = CircleShape,
                                        )
                                        .padding(4.dp),
                                    imageVector = Icons.Filled.QrCode,
                                    contentDescription = "",
                                    colorFilter =
                                        ColorFilter.tint(
                                            color = primaryColor,
                                        ),
                                )
                            }
                        }
                        if (showCloseButton) {
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .padding(top = 4.dp),
                                onClick = {
                                    IncomingCallService.hideAlert(context)
                                },
                            ) {
                                Image(
                                    modifier = Modifier
                                        .background(
                                            color = primaryColor.copy(alpha = 0.2f),
                                            shape = CircleShape,
                                        )
                                        .padding(4.dp),
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(
                                        color = primaryColor,
                                    ),
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .applyIf(useBlur) {
                            hazeChild(
                                state = hazeState,
                                shape = backgroundDetails,
                                style = HazeStyle(
//                                    tint = secondaryBckColor.copy(alpha = backgroundAlpha),
                                    blurRadius = 4.dp,
                                    noiseFactor = 0.1f,
                                ),
                            )
                        }
                ) {
                    if (details != null || listItem?.details != null) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = sBckColor.copy(alpha = 0.8f)
                                )
                                .padding(8.dp),
                            text = details ?: listItem?.details ?: "",
                            fontSize = 13.sp,
                            style = textStyle,
                            fontFamily = fontFamily,
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        buttons(listItem)
                    }
                }
                if (!isLast && showDivider) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = 80.dp)
                            .background(
                                color = primaryColor.copy(alpha = 0.6f),
                                shape = DottedShape(8.dp),
                            ),
                        color = Color.Transparent,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}
