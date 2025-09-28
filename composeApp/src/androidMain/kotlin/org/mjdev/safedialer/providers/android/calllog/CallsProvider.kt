package org.mjdev.safedialer.providers.android.calllog

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data

@TargetApi(Build.VERSION_CODES.KITKAT)
class CallsProvider(
    context: Context
) : AbstractProvider(context) {
    fun getCalls(): Data<Call>? {
        return getContentTableData(Call.uri, Call::class.java)
    }
}
