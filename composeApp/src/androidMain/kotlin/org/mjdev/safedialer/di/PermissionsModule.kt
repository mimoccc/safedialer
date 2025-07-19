package org.mjdev.safedialer.di

import android.Manifest
import com.nabinbhandari.android.permissions.Permissions
import org.kodein.di.DI
import org.kodein.di.bindConstant

@Suppress("DEPRECATION")
val permissionsModule = DI.Module("PermissionsModule") {
    bindConstant<Array<String>>("permissions") {
        arrayOf(
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
    }
    bindConstant<Permissions.Options>("permissionOptions") {
        Permissions.Options()
            .setRationaleDialogTitle("Povolení")
            .setSettingsDialogTitle("Povolení")
            .setCreateNewTask(true)
    }
}