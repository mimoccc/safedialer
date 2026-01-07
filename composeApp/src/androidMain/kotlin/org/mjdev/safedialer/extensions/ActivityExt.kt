package org.mjdev.safedialer.extensions

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import org.mjdev.safedialer.extensions.ColorExt.toColorInt

@Suppress("DEPRECATION", "unused")
object ActivityExt {

    fun Activity.addLockScreenFlags() {
        if (isOreoMr1Plus()) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        if (isOreoPlus()) {
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).requestDismissKeyguard(
                this,
                null
            )
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
    }

    fun isOreoMr1Plus(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    }

    fun isOreoPlus(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun Activity.stringResource(
        @StringRes id: Int
    ) :  Lazy<String> = lazy{
        baseContext.resources.getString(id)
    }

    fun ComponentActivity.enableEdgeToEdge(
        statusBarColor: Color = Color.DarkGray,
        navigationBarColor: Color = Color.DarkGray,
    ) = enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(statusBarColor.toColorInt()),
        navigationBarStyle = SystemBarStyle.dark(navigationBarColor.toColorInt())
    )

}
