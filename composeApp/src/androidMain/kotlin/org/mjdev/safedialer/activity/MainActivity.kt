package org.mjdev.safedialer.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import org.kodein.di.DIAware
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import org.mjdev.phone.nsd.service.CallNsdService.Companion.start
import org.mjdev.safedialer.R
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.ActivityExt.addLockScreenFlags
import org.mjdev.safedialer.extensions.ActivityExt.enableEdgeToEdge
import org.mjdev.safedialer.extensions.ActivityExt.stringResource
import org.mjdev.safedialer.extensions.DiExt.closestDI
import org.mjdev.safedialer.helpers.PreferencesManager
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.phone.WifiPhoneService
import org.mjdev.safedialer.service.calls.IncomingCallService
import org.mjdev.safedialer.service.calls.IncomingCallService.Companion.showAlert
import org.mjdev.safedialer.service.media.MediaService
import org.mjdev.safedialer.sync.SyncManager
import org.mjdev.safedialer.ui.screen.MainScreen
import org.mjdev.safedialer.ui.screen.PermissionsScreen
import org.mjdev.safedialer.ui.theme.AppTheme
import org.mjdev.safedialer.ui.theme.backgroundDark

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity(), DIAware {
    override val di by closestDI { mainDI(this) }

    private val permissions by instance<Array<String>>("permissions")
    private val options by instance<Permissions.Options>("permissionOptions")
    private val preferencesManager by instance<PreferencesManager>()

    private val rationale by stringResource(R.string.permissions_rationale)

    private val wasPermissionsGranted
        get() = preferencesManager.getBoolean(PERMISSIONS_GRANTED, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            backgroundDark,
            backgroundDark
        )
        addLockScreenFlags()
        initializeLLM()
        super.onCreate(savedInstanceState)
        setContent {
            withDI(di) {
                AppTheme {
                    if (wasPermissionsGranted) {
                        MainScreen()
                    } else {
                        PermissionsScreen()
                    }
                }
            }
        }
        checkPermissions()
        checkFullFileAccessPermission()
        MediaService.start(this)
        IncomingCallService.start(this)
        start<WifiPhoneService>()
        SyncManager.ensureAccount(this)
    }

    override fun onStart() {
        super.onStart()
        SyncManager.requestImmediateSync(this)
        showAlert(this, getString(R.string.test_cell_number))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun refreshUI() {
        if (wasPermissionsGranted.not()) {
            preferencesManager.putBoolean(PERMISSIONS_GRANTED, true)
            IncomingCallService.restart(this@MainActivity)
            Handler().postDelayed({
                recreate()
            }, 400L)
        }
    }

    private fun initializeLLM() = runCatching {
//        CactusContextInitializer.initialize(this)
    }.onFailure { e ->
        Log.e(TAG, e.message, e)
    }

    private fun checkFullFileAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }

    private fun checkPermissions() = Permissions.check(
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
                deniedPermissions: ArrayList<String?>?,
            ) {
                refreshUI()
            }
        }
    )

    companion object {
        private val TAG = MainActivity::class.simpleName

        const val PERMISSIONS_GRANTED = "permissions_granted"
    }
}

@Previews
@Composable
fun PreviewMainActivity(
    wasPermissionsGranted: Boolean = true,
) {
    AppTheme {
        if (wasPermissionsGranted) {
            MainScreen()
        } else {
            PermissionsScreen()
        }
    }
}
