package org.mjdev.safedialer.service.calls.command

import android.os.Bundle

interface CommandReceiver {
    fun onCommand(
        command: ServiceCommand?,
        data: Bundle?
    )
}
