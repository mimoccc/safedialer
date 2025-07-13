package org.mjdev.safedialer.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import org.mjdev.safedialer.data.ContactsRepository
import org.mjdev.safedialer.data.ContactsRepository.Companion.rememberContactsRepository
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.data.enums.CallType
import org.mjdev.safedialer.shapes.DottedShape
import org.mjdev.safedialer.service.IncomingCallService
import org.mjdev.safedialer.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mjdev.safedialer.data.Mapper.asListItem
import org.mjdev.safedialer.extensions.ComposeExt1.rememberImageLoader
import java.util.Date

@Suppress("DEPRECATION")
@Preview
@Composable
fun ContactDetail(
    modifier: Modifier = Modifier,
    caller: String? = null,
    contactsRepository: ContactsRepository? = rememberContactsRepository(),
    contact: ListItem = contactsRepository?.findContact(caller)?.asListItem() ?: ListItem(
        contactId = "",
        phoneNumber = caller ?: "+420702568909",
        displayName = "Milan Jurkulak"
    ),
    buttons: @Composable () -> Unit = { ContactButtonsDefault(contact) },
    context: Context = LocalContext.current,
    imageLoader: ImageLoader = rememberImageLoader(context),
    textStyle: TextStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary,
    ),
    fontFamily: FontFamily = FontFamily.Default,
    showCloseButton: Boolean = false,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    showDate: Boolean = false
) = AppTheme {
    val background = RoundedCornerShape(
        topStart = if (isFirst) 16.dp else 0.dp,
        topEnd = if (isFirst) 16.dp else 0.dp,
        bottomEnd = if (isLast) 16.dp else 0.dp,
        bottomStart = if (isLast) 16.dp else 0.dp
    )
    Box(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.background,
            shape = background
        ),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (contact.isBlocked || contact.isDanger) Color.Red.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    shape = background
                )
//                .liquidGlass(
//                    LiquidGlassStyle(
//                        shape = background,
//                        blurRadius = 18.dp,
//                        refractionHeight = 24.dp,
//                        refractionAmount = (-64).dp,
//                        whitePoint = if (isDark) -0.25f else 0.25f,
//                        chromaMultiplier = 1.5f
//                    )
//                )
                .padding(start = 0.dp, end = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Box {
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
                                    when (contact.type) {
                                        CallType.BLOCKED -> Color.Red
                                        CallType.MISSED -> Color.Red
                                        CallType.REJECTED -> Color.Red
                                        CallType.VOICEMAIL -> Color.Blue
                                        CallType.OUTGOING -> Color.Green
                                        else -> when {
                                            contact.isStored -> Color.Green
                                            else -> Color.White.copy(alpha = 0.5f)
                                        }
                                    },
                                    CircleShape
                                )
                        ) {
                            ContactPhoto(
                                contact = contact,
                                imageLoader = imageLoader,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, true)
                                .padding(
                                    bottom = 20.dp,
                                    top = 16.dp
                                )
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = contact.displayName.ifEmpty { "contact name" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                style = textStyle,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = contact.phoneNumber.ifEmpty { "contact phone" },
                                fontSize = 14.sp,
                                style = textStyle,
                                fontFamily = fontFamily,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.outline
                            )
                            if (showDate) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = contact.date?.takeIf { d -> d != 0L }?.let { d ->
                                        val date = Date(d)
                                        val hours = date.hours.toString().padStart(2, '0')
                                        val minutes = date.minutes.toString().padStart(2, '0')
                                        val seconds = date.seconds.toString().padStart(2, '0')
                                        "${hours}:${minutes}:${seconds}"
                                    } ?: "-",
                                    fontSize = 14.sp,
                                    style = textStyle,
                                    fontFamily = fontFamily,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                                .padding(top = 4.dp),
                            onClick = {
                                // todo generate qr code
                            }
                        ) {
                            Image(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp),
                                imageVector = Icons.Filled.QrCode,
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        if (showCloseButton) {
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                onClick = {
                                    IncomingCallService.hideAlert(context)
                                }
                            ) {
                                Image(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "",
                                    modifier = Modifier.padding(4.dp),
                                    colorFilter = ColorFilter.tint(
                                        color = Color.White
                                    )
                                )
                            }
                        }
                        buttons()
                    }
                }
                if (!isLast) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = 80.dp, bottom = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = DottedShape(5.dp)
                            ),
                        color = Color.Transparent,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}
