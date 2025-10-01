package org.mjdev.safedialer.providers.android.calllog

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import org.mjdev.safedialer.providers.core.AbstractProvider

@TargetApi(Build.VERSION_CODES.KITKAT)
class CallsProvider(
    context: Context
) : AbstractProvider(context) {
    fun getCalls(): List<Call>? {
        return getContentTableData(Call.uri, Call::class.java)?.getList()
    }
}
