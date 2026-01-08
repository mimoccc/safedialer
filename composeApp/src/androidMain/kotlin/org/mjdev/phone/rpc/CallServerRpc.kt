package org.mjdev.phone.rpc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.nsd.NsdServiceInfo
import android.util.Log
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import org.mjdev.phone.rpc.CallAction.ActionCall
import org.mjdev.phone.helpers.ToolsJson.json
import org.mjdev.phone.nsd.device.NsdDevice
import org.mjdev.phone.nsd.device.NsdTypes
import org.mjdev.phone.nsd.device.nsdDeviceListFlow
import org.mjdev.phone.nsd.rpc.INsdServerRPC
import org.mjdev.phone.rpc.CallServerRpc.Companion.sendAction

@Suppress("unused", "RedundantSuspendModifier")
@OptIn(ExperimentalCoroutinesApi::class)
class CallServerRpc(
    context: Context,
    val port: Int = 8888,
    val onAction: (ActionCall) -> Unit = {}
) : INsdServerRPC(context) {
    @OptIn(ExperimentalSerializationApi::class)
    private val server by lazy {
        embeddedServer(CIO, port = port) {
            install(ContentNegotiation) {
                json(json)
            }
            routing {
                post("/action") {
                    runCatching {
                        call.receive<ActionCall>().also { action ->
                            onAction(action)
                        }
                    }.onFailure { e ->
                        call.respond(HttpStatusCode.InternalServerError, e)
                    }.onSuccess {
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }

    override suspend fun start() {
        server.start(wait = false)
    }

    override suspend fun stop() {
        server.stop(1000, 2000)
    }

    companion object {
        val TAG = NsdDevice::class.simpleName

        suspend inline fun <reified T : CallAction> NsdDevice.sendAction(
            action: T
        ) = runCatching {
            val address = address ?: return@runCatching
            val url = "http://$address:$port/action"
            val jsonString = json.encodeToString(action)
            val body = jsonString.toRequestBody("application/json".toMediaType())
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = Level.BODY
                })
                .build()
                .newCall(
                    Request.Builder()
                        .url(url)
                        .post(body)
                        .build()
                )
                .execute()
                .use { response ->
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "Failed to send action: ${response.code} to $address")
                        Log.e(TAG, "Url: $url")
                        Log.e(TAG, "Action: $action")
                        Log.e(TAG, response.message)
                    } else {
                        Log.d(TAG, "Action $action send to $address")
                        Log.d(TAG, "Data: $jsonString")
                    }
                }
        }.onFailure { e ->
            e.printStackTrace()
        }

        suspend fun NsdDevice.getFrame(): Bitmap? {
            val address = address ?: return null
            val url = "http://$address:$port/capture"
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = Level.BODY
                })
                .build()
                .newCall(
                    Request.Builder()
                        .url(url)
                        .build()
                ).execute().use { response ->
                    return if (!response.isSuccessful) {
                        Log.e(
                            NsdDevice.TAG,
                            "Failed to get frame from ($address): ${response.code}"
                        )
                        null
                    } else {
                        response.body.byteStream().let { stream ->
                            BitmapFactory.decodeStream(stream)
                        }
                    }
                }
        }

        suspend fun Context.sendActionToAll(
            types: List<NsdTypes> = listOf(NsdTypes.DOOR_BELL_CLIENT),
            onError: (Throwable) -> Unit = { e -> e.printStackTrace() },
            filter: (NsdServiceInfo) -> Boolean = { true },
            action: ActionCall,
        ) = nsdDeviceListFlow(this, types, onError, filter).collectLatest { devices ->
            devices.forEach { device ->
                device.sendAction(action)
            }
        }

        @Suppress("UnusedReceiverParameter")
        suspend fun Context.makeCall(
            caller: NsdDevice?,
            callee: NsdDevice,
        ) {
            callee.sendAction<ActionCall>(ActionCall(caller, callee))
        }
    }
}
