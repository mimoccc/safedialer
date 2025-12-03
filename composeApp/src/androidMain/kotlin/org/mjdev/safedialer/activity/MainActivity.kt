package org.mjdev.safedialer.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import org.kodein.di.DIAware
import org.mjdev.safedialer.extensions.CustomExt.closestDI
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.ActivityExt.addLockScreenFlags
import org.mjdev.safedialer.helpers.PreferencesManager
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.service.IncomingCallService
import org.mjdev.safedialer.sync.SyncManager
import org.mjdev.safedialer.ui.screen.MainScreen
import org.mjdev.safedialer.ui.screen.PermissionsScreen
import org.mjdev.safedialer.ui.theme.AppTheme

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity(), DIAware {
    override val di by closestDI { mainDI(this) }

    private val permissions by instance<Array<String>>("permissions")
    private val options by instance<Permissions.Options>("permissionOptions")
    private val preferencesManager by instance<PreferencesManager>()

    // todo translations
    private val rationale = "Prosím povolte přístup k telefonním císlum, " +
            "abychom mohli sledovat prichozí hovory."
    private val wasPermissionsGranted
        get() = preferencesManager.getBoolean(PERMISSIONS_GRANTED, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
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
        SyncManager.ensureAccount(this)
        checkPermissions()
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
