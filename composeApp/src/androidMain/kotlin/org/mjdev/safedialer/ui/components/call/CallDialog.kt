package org.mjdev.safedialer.ui.components.call

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import org.mjdev.safedialer.data.mapper.ContactMapper.Companion.asListItem
import org.mjdev.safedialer.extensions.ComposeExt.collectAsState
import org.mjdev.safedialer.extensions.ComposeExt.rememberImageLoader
import org.mjdev.safedialer.extensions.ViewModelExt.rememberViewModelSafe
import org.mjdev.safedialer.extensions.ModifierExt.applyIf
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.ui.components.contact.ContactBackground
import org.mjdev.safedialer.ui.components.call.ContactDetails.Companion.rememberContactDetails
import org.mjdev.safedialer.ui.components.contact.ContactDetail
import org.mjdev.safedialer.ui.theme.AppTheme
import org.mjdev.safedialer.viewmodel.MainViewModel
import kotlin.getValue

@Previews
@Composable
fun CallDialog(
    caller: String? = "+420702568909",
    info: String? = null, // todo
    contactDetails: ContactDetails = rememberContactDetails(caller),
    useBlur: Boolean = true,
    hazeState: HazeState = remember { HazeState() },
    imageLoader: ImageLoader = rememberImageLoader(),
) = AppTheme {
    val viewModel by rememberViewModelSafe { context ->
        MainViewModel(context, MockDataRepository(context))
    }
    val contact by collectAsState(caller) {
        viewModel.findContactByPhone(caller)
            ?: viewModel.findContactBySender(caller)
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {
        ContactBackground(
            modifier = Modifier
                .matchParentSize()
                .applyIf(useBlur) {
                    haze(hazeState)
                },
            imageLoader = imageLoader,
            showBckImage = false,
            showShadows = false,
            contact = contact?.asListItem(),
            shape = RoundedCornerShape(16.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            ContactDetail(
                caller = contactDetails.phoneNumber,
                showCloseButton = true,
                isLast = false,
                showBckImage = false,
                showShadows = false,
                mainBckColor = Color.Transparent,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
            ) {
                Text(
                    text = info ?: ""
                )
            }
        }
    }
}

class ContactDetails(
    val phoneNumber: String? = "+420702568909",
) {
    companion object {
        // todo
        @Composable
        fun rememberContactDetails(
            phoneNumber: String? = "+420702568909"
        ) = remember(phoneNumber) {
            ContactDetails(phoneNumber)
        }
    }
}
