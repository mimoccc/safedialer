package org.mjdev.phone.rpc

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.mjdev.phone.nsd.device.NsdDevice

// todo rename & permutations
@Suppress("unused")
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("type")
@Serializable
sealed class CallAction {

    @Serializable
    @SerialName("DoorBellActionCall")
    class DoorBellActionCall(
         caller: NsdDevice?,
         callee: NsdDevice?
    ) : ActionCall(caller, callee)

    @Serializable
    @SerialName("DoorBellActionCall")
    open class ActionCall(
        val caller: NsdDevice?,
        val callee: NsdDevice?
    ) : CallAction()

}
