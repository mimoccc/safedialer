package org.mjdev.safedialer.service.calls

interface CallListener {
    fun onCallEnded(incomingNumber: String?)
    fun onCallAccepted(incomingNumber: String?)
    fun onIncomingCall(incomingNumber: String?)
}
