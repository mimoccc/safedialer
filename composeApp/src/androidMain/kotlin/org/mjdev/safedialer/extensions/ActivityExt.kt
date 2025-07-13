package org.mjdev.safedialer.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.view.WindowManager

object ActivityExt {

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
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

}
