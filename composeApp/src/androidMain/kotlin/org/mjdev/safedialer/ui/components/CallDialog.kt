package org.mjdev.safedialer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.components.ContactDetails.Companion.rememberContactDetails
import org.mjdev.safedialer.ui.theme.AppTheme

@Previews
@Composable
fun CallDialog(
    phoneNumber: String? = "+420702568909",
    info: String?,
    contactDetails: ContactDetails = rememberContactDetails(phoneNumber),
) = AppTheme {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = if (info == null || info.isEmpty()) MaterialTheme.colorScheme.background
                else Color.Red,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        ContactDetail(
            caller = contactDetails.phoneNumber,
            showCloseButton = true,
            isFirst = true,
            isLast = false,
            buttons = {},
            showDivider = true,
        )
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(96.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
//                ),
//        ) {
//        }
    }
}

class ContactDetails(
    val phoneNumber: String? = "+420702568909",
) {
    companion object {
        @Composable
        fun rememberContactDetails(phoneNumber: String? = "+420702568909") =
            remember(phoneNumber) {
                ContactDetails(phoneNumber)
            }
    }
}
