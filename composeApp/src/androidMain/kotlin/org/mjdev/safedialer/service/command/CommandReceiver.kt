package org.mjdev.safedialer.service.command

import android.os.Bundle

interface CommandReceiver {
    fun onCommand(
        command: ServiceCommand?,
        data: Bundle?
    )
}
