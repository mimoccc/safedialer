package org.mjdev.phone.service

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import org.mjdev.phone.activity.VideoCallActivity.Companion.startCall
import org.mjdev.phone.nsd.device.NsdTypes
import org.mjdev.phone.nsd.rpc.INsdServerRPC
import org.mjdev.phone.nsd.service.NsdService
import org.mjdev.phone.rpc.CallAction
import org.mjdev.phone.rpc.CallServerRpc

// todo automatic user login with wifi access
class CallNsdService : NsdService() {
    override val port: Int = 8888

    override val serviceType: NsdTypes
        get() = NsdTypes.SAFEDIALER

    override val rpcServer: INsdServerRPC by lazy {
        CallServerRpc(
            context = baseContext,
            port = port,
            onAction = ::onRpcAction
        )
    }

    override fun onCreate() {
        isRunning.value = true
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning.value = false
    }

    fun onRpcAction(
        action: CallAction
    ) {
        when (action) {
            is CallAction.ActionCall -> {
                baseContext.startCall(null, caller = action.caller)
            }
            else -> {
                // omit
            }
        }
    }

    companion object {
        private val isRunning = mutableStateOf(false)

        fun start(
            context: Context
        ) = runCatching {
            if (isRunning.value.not()) Intent(
                context,
                CallNsdService::class.java
            ).also { intent ->
                context.startForegroundService(intent)
            }
        }.onFailure { e ->
            e.printStackTrace()
        }

        fun stop(
            context: Context
        ) = runCatching {
            if (isRunning.value) Intent(
                context,
                CallNsdService::class.java
            ).also { intent ->
                context.stopService(intent)
            }
        }.onFailure { e ->
            e.printStackTrace()
        }
    }
}