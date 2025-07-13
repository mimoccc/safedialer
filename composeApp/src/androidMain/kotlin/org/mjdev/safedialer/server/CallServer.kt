package org.mjdev.safedialer.server

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.serialization.gson.gson
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.identity
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.websocket.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.event.Level
import java.net.Inet4Address
import kotlin.time.Duration.Companion.seconds

@Suppress("DEPRECATION")
class CallServer(
    val context: Context,
    val port: Int = 0,
) {
    val connectivityManager by lazy {
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val messageResponseFlow = MutableSharedFlow<String>()
    val sharedFlow = messageResponseFlow.asSharedFlow()
    val server by lazy {
        embeddedServer(
            Netty,
            port = port,
            module = {
                install(DefaultHeaders)
                install(CallLogging) {
                    level = Level.INFO
                    // You can customize logging further, e.g., filter requests:
                    // filter { call -> call.request.path().startsWith("/api") }
                }
                install(ContentNegotiation) {
                    gson {
                        setPrettyPrinting()
                        // disableHtmlEscaping()
                    }
                }
                install(WebSockets) {
                    pingPeriod = 15.seconds
                    timeout = 15.seconds
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
                install(StatusPages) {
                    exception<Throwable> { call, cause ->
                        // Log the exception
                        call.application.environment.log.error("Unhandled exception", cause)
                        // Respond with a generic error message
                        call.respondText(
                            "Internal Server Error: ${cause.localizedMessage}",
                            ContentType.Text.Plain,
                            InternalServerError
                        )
                    }
                    // You can add more specific exception handlers here, e.g.:
                    // status(io.ktor.http.HttpStatusCode.NotFound) { call, status ->
                    //    call.respondText("Custom 404: Not Found", ContentType.Text.Plain, status)
                    // }
                }
                install(CORS) {
                    allowMethod(HttpMethod.Options)
                    allowMethod(HttpMethod.Get)
                    allowMethod(HttpMethod.Post)
                    allowMethod(HttpMethod.Put)
                    allowMethod(HttpMethod.Delete)
                    allowMethod(HttpMethod.Patch)
                    allowHeader(HttpHeaders.Authorization)
                    allowHeader(HttpHeaders.ContentType)
                    anyHost() // In production, you should restrict this to specific hosts
                    // allowCredentials = true // If you need to send cookies or use authentication headers
                    // allowNonSimpleContentTypes = true
                }
                install(Authentication) {
                    // Configure authentication providers here, e.g., basic, JWT, OAuth
                    // basic("myBasicAuth") {
                    //    realm = "Ktor Server"
                    //    validate { credentials ->
                    //        if (credentials.name == "test" && credentials.password == "password") {
                    //            UserIdPrincipal(credentials.name)
                    //        } else {
                    //            null
                    //        }
                    //    }
                    // }
                }
                install(Compression) {
                    gzip {
                        priority = 1.0
                    }
                    deflate {
                        priority = 10.0
                    }
                    identity {
                        // To disable compression for certain content types if needed
                    }
                }
                routing {
                    webSocket("/api") {
                        send("HELO")
                        val job = launch {
                            sharedFlow.collect { message ->
                                send(message)
                            }
                        }
                        runCatching {
                            incoming.consumeEach { frame ->
                                if (frame is Frame.Text) {
                                    val receivedText = frame.readText()
                                    val messageResponse = receivedText
                                    messageResponseFlow.emit(messageResponse)
                                }
                            }
                        }.onFailure { exception ->
                            println("WebSocket exception: ${exception.localizedMessage}")
                        }.also {
                            job.cancel()
                        }
                    }
                    staticResources(
                        "/",
                        "server-files",
                        index = "index.html",
                    ) {
                        // todo modify page content, replace server tags
//                        modify { res, call ->
//                        }
                        // todo extensions
//                        extensions()
                    }
                    // todo or static zip if exists sdcard folder
                    // or an app
                }
            }
        )
    }
    val applicationEngine: ApplicationEngine
        get() = server.engine

    private fun wifiIpAddress(): String? {
        val network = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return null
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            val linkProperties = connectivityManager.getLinkProperties(network) ?: return null
            for (linkAddress in linkProperties.linkAddresses) {
                val address = linkAddress.address
                if (address is Inet4Address) {
                    return address.hostAddress
                }
            }
        }
        return null
    }

    suspend fun startServer(
        onError: (Throwable) -> Unit = {},
        onStarted: (CallServer, String) -> Unit = { _, _ -> }
    ) = runCatching {
        server.start()
        onStarted(this, getHttpAddress())
    }.onFailure { e ->
        e.printStackTrace()
        onError(e)
    }

    suspend fun stopServer(
        onError: (Throwable) -> Unit = {},
        onStopped: (CallServer) -> Unit = {}
    ) = runCatching {
        server.stop()
        onStopped(this)
    }.onFailure { e ->
        e.printStackTrace()
        onError(e)
    }

    suspend fun getHttpAddress(): String {
        val activePort = applicationEngine.resolvedConnectors().first().port
        val activeIp = wifiIpAddress()
        return "http://$activeIp:$activePort"
    }

    companion object {
        @Composable
        fun rememberCallServer(
            context: Context = LocalContext.current
        ): CallServer = remember() {
            CallServer(context)
        }
    }
}
