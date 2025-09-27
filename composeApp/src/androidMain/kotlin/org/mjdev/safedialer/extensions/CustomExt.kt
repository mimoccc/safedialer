package org.mjdev.safedialer.extensions

import android.net.Uri
import org.mjdev.safedialer.service.IncomingCallService

object CustomExt {

    val IS_DEBUG: Boolean = IncomingCallService.isStarted.not()

    val SMS_URI: Uri = Uri.parse("content://sms/")

}