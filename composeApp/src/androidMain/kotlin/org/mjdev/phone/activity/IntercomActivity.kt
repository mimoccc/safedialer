package org.mjdev.phone.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mjdev.phone.activity.VideoCallActivity.Companion.startCall
import org.mjdev.phone.activity.base.UnlockedActivity
import org.mjdev.phone.extensions.PermissionsExt.LaunchPermissions
import org.mjdev.phone.helpers.Previews
import org.mjdev.phone.nsd.device.NsdTypes
import org.mjdev.phone.service.CallNsdService
import org.mjdev.phone.ui.BackgroundLayout
import org.mjdev.phone.ui.NsdList

@Suppress("AssignedValueIsNeverRead")
class IntercomActivity : UnlockedActivity() {
    companion object {
        private val TAG = IntercomActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
        CallNsdService.start(this)
    }

    @Previews
    @Composable
    fun MainScreen()  {
        var arePermissionsGranted by remember { mutableStateOf(false) }
        if (arePermissionsGranted) {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxSize()
            ) {
                NsdList(
                    modifier = Modifier.fillMaxSize(),
                    types = listOf(NsdTypes.DOOR_BELL_ASSISTANT, NsdTypes.DOOR_BELL_CLIENT),
                    onError = { e -> Log.e(TAG, e.message, e) },
                    onCallClick = { nsdDevice ->
                        this@IntercomActivity.startCall(nsdDevice)
                    },
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .displayCutoutPadding()
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BackgroundLayout(
                    modifier = Modifier.fillMaxSize()
                )
                // todo permissions screen
            }
            LaunchPermissions(
                onPermissionsResult = { pms ->
                    arePermissionsGranted = pms.any { p -> p.value }
                    if (arePermissionsGranted.not()) recreate()
                },
                onAllPermissionsGranted = {
                    arePermissionsGranted = true
                }
            )
        }
    }
}
