package org.mjdev.safedialer.extensions

import android.app.Application
import android.content.Context

@Suppress("unused")
object ContextExt {

    val Context.application: Application
        get() = this.applicationContext as Application

}
