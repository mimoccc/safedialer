package org.mjdev.safedialer.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.mjdev.safedialer.ui.tabs.TabCallLog
import org.mjdev.safedialer.ui.tabs.TabContactList
import org.mjdev.safedialer.ui.tabs.TabMessages

enum class Tabs(
    val title: String,
    val content: @Composable (scrollState: LazyListState, filterText: MutableState<String>) -> Unit
) {
    CallLog("Calls", { ss, ft -> TabCallLog(ss, ft) }),
    Contacts("Contacts", { ss, ft -> TabContactList(ss, ft) }),
    Messages("Messages", { ss, ft -> TabMessages(ss, ft) });

    companion object {
        fun asList(): List<Tabs> = entries
    }
}
