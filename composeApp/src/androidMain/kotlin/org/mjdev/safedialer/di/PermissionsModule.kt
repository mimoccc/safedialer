package org.mjdev.safedialer.di

import android.Manifest
import android.content.Context
import com.nabinbhandari.android.permissions.Permissions
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.mjdev.safedialer.R

@Suppress("DEPRECATION")
val permissionsModule = DI.Module("PermissionsModule") {
    bindProvider <Array<String>>("permissions") {
        arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.MANAGE_OWN_CALLS,
            Manifest.permission.DISABLE_KEYGUARD,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.POST_NOTIFICATIONS,
//            Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL, // todo ?
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_MMS,
        )
    }
    bindProvider<Permissions.Options>("permissionOptions") {
        val context = instance<Context>()
        val title = context.getString(R.string.title_permissions)
        Permissions.Options()
            .setRationaleDialogTitle(title)
            .setSettingsDialogTitle(title)
            .setCreateNewTask(true)
    }
}