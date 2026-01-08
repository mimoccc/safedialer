package org.mjdev.safedialer.phone

import org.mjdev.phone.nsd.device.NsdTypes
import org.mjdev.phone.nsd.service.CallNsdService

class WifiPhoneService : CallNsdService() {
    override val serviceType: NsdTypes = NsdTypes.SAFE_DIALER
}
