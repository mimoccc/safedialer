package org.mjdev.safedialer.activity

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import org.mjdev.safedialer.extensions.ActivityExt.addLockScreenFlags
import org.mjdev.safedialer.helpers.PreferencesManager
import org.mjdev.safedialer.service.IncomingCallService
import org.mjdev.safedialer.ui.screen.MainScreen
import org.mjdev.safedialer.ui.screen.PermissionsScreen

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    val permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.MANAGE_OWN_CALLS,
        Manifest.permission.DISABLE_KEYGUARD,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_MMS,
    )
    val rationale =
        "Prosím povolte přístup k telefonním císlum, abychom mohli sledovat prichozí hovory."
    val options: Permissions.Options? = Permissions.Options()
        .setRationaleDialogTitle("Povolení")
        .setSettingsDialogTitle("Povolení")
        .setCreateNewTask(true)
    val preferencesManager by lazy {
        PreferencesManager(applicationContext)
            .setName("app_preferences")
            .setMode(MODE_PRIVATE)
            .init()
    }
    val wasPermissionsGranted
        get() = preferencesManager.getBoolean(PERMISSIONS_GRANTED, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        addLockScreenFlags()
        super.onCreate(savedInstanceState)
        setContent {
            if (wasPermissionsGranted) {
                MainScreen()
            } else {
                PermissionsScreen()
            }
        }
        checkPermissions()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    fun refreshUI() {
        if (wasPermissionsGranted.not()) {
            preferencesManager.putBoolean(PERMISSIONS_GRANTED, true)
            IncomingCallService.restart(this@MainActivity)
            Handler().postDelayed({
                recreate()
            }, 1000)
        }
    }

    @Suppress("DEPRECATION")
    fun checkPermissions() = Permissions.check(
        applicationContext,
        permissions,
        rationale,
        options,
        object : PermissionHandler() {
            override fun onGranted() {
                refreshUI()
            }

            override fun onDenied(
                context: Context?,
                deniedPermissions:
                ArrayList<String?>?
            ) {
                refreshUI()
            }
        }
    )

    companion object {
        const val PERMISSIONS_GRANTED = "permissions_granted"
    }
}
