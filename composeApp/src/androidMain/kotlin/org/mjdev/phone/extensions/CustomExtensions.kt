package org.mjdev.phone.extensions

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mjdev.safedialer.extensions.ColorExt.toColorInt
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import kotlin.coroutines.CoroutineContext

@Suppress("UnusedReceiverParameter", "DEPRECATION", "unused")
object CustomExtensions {

    @SuppressLint("NewApi")
    fun ComponentActivity.dismissKeyguard() {
        runCatching {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }.onFailure { e ->
            e.printStackTrace()
        }
        runCatching {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    fun ComponentActivity.setFullScreen() {
        runCatching {
            setTurnScreenOn(true)
        }.onFailure { e ->
            e.printStackTrace()
        }
        runCatching {
            setShowWhenLocked(true)
        }.onFailure { e ->
            e.printStackTrace()
        }
        runCatching {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    fun ComponentActivity.turnDisplayOn() {
        runCatching {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }.onFailure { e ->
            e.printStackTrace()
        }
        runCatching {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }.onFailure { e ->
            e.printStackTrace()
        }
        runCatching {
            setTurnScreenOn(true)
        }.onFailure { e ->
            e.printStackTrace()
        }
        runCatching {
            setShowWhenLocked(true)
        }.onFailure { e ->
            e.printStackTrace()
        }
    }

    fun ComponentActivity.hideSystemBars() = runCatching {
        WindowCompat.getInsetsController(window, window.decorView).also { windowInsetsController ->
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }.onFailure { e ->
        e.printStackTrace()
    }

    val Context.ANDROID_ID: String
        @SuppressLint("HardwareIds")
        get() {
            return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }

    @Suppress("DEPRECATION")
    val Context.currentWifiSSID: String
        get() = run {
            val wifiManager =
                (applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager)
            var ssid = wifiManager?.connectionInfo?.ssid?.replace("\"", "")
            if (ssid == null || ssid == "<unknown ssid>" || ssid == "unknown") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val connectivityManager = getSystemService(
                        Context.CONNECTIVITY_SERVICE
                    ) as? ConnectivityManager
                    val network = connectivityManager?.activeNetwork
                    val capabilities = connectivityManager?.getNetworkCapabilities(network)
                    if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                        val wifiInfo = capabilities.transportInfo as? WifiInfo
                        ssid = wifiInfo?.ssid?.replace("\"", "")
                    }
                }
            }
            if (ssid == null || ssid == "<unknown ssid>") {
                "unknown"
            } else {
                ssid
            }
        }

    val Context.currentWifiIP: String
        get() = NetworkInterface.getNetworkInterfaces()
            .toList()
            .firstOrNull { n ->
                n.name.startsWith("wlan") && n.isUp
            }
            ?.inetAddresses
            ?.toList()
            ?.filterIsInstance<Inet4Address>()
            ?.firstOrNull { n ->
                n.isSiteLocalAddress
            }?.hostAddress ?: "..."


    val Context.currentSystemUser: String
        get() = try {
            @Suppress("DEPRECATION")
            Settings.Secure.getString(contentResolver, "user_name") ?: "Unknown user"
        } catch (e: Exception) {
            "System User"
        }

    inline fun <reified T> Context.intent(
        block: Intent.() -> Unit
    ): Intent = Intent(applicationContext, T::class.java).apply(block)

    fun String.toInetAddress(): InetAddress? = when {
        isValidIpAddress() -> InetAddress.getByAddress(toByteArray())
        else -> {
            Exception("Invalid ip address: $this.").printStackTrace()
            null
        }
    }

    fun String.isValidIpAddress(): Boolean = split(".").let { parts ->
        parts.size == 4 && parts.all { it.toIntOrNull() in 0..255 }
    }

    fun LifecycleOwner.launchOnLifecycle(
        scope: LifecycleCoroutineScope = lifecycleScope,
        context: CoroutineContext = Dispatchers.Main,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(
        context = context,
        block = block
    )

    fun Modifier.applyIf(
        condition: Boolean,
        other: Modifier.() -> Modifier
    ): Modifier = if (condition) this.then(other()) else this

    fun ComponentActivity.enableEdgeToEdge(
        statusBarColor: Color = Color.DarkGray,
        navigationBarColor: Color = Color.DarkGray,
    ) = enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(statusBarColor.toColorInt()),
        navigationBarStyle = SystemBarStyle.dark(navigationBarColor.toColorInt())
    )

    val isPreview
        get() = isLayoutLib()

    val isInPreviewMode: Boolean
        get() = isLayoutLib()

    fun isLayoutLib(): Boolean {
        val device = Build.DEVICE
        val product = Build.PRODUCT
        return device == "layoutlib" || product == "layoutlib"
    }

    @Composable
    fun rememberAssetImage(
        name: String = "avatar1.png",
    ): ImageBitmap {
        val context: Context = LocalContext.current
        return remember {
            context.assets.open(name).use { inputStream ->
                BitmapFactory.decodeStream(inputStream).asImageBitmap()
            }
        }
    }

}
