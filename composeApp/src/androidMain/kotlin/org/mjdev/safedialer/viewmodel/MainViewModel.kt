package org.mjdev.safedialer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.repository.IDataRepository
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("unused")
class MainViewModel(
    val dataRepository: IDataRepository
) : ViewModel() {

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    private val _serverState = MutableStateFlow(false)
    val serverState: StateFlow<Boolean> = _serverState.asStateFlow()

    private val _isTabsVisible = MutableStateFlow(true)
    val isTabsVisible: StateFlow<Boolean> = _isTabsVisible.asStateFlow()

    val contactMap = dataRepository.contactsMap
    val callsMap = dataRepository.callsMap
    val messagesMap = dataRepository.messagesMap
    val messageThreads = dataRepository.emailThreads

    fun toggleServerState() {
        _serverState.value = !_serverState.value
    }

    fun setTabsVisible(visible: Boolean) {
        _isTabsVisible.value = visible
    }

    fun runSafe(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context, start, block)

    suspend fun findContactByPhone(
        phoneNumber: String?
    ): Contact? = runCatching {
        dataRepository.findContactByPhone(phoneNumber)
    }.getOrNull()

    suspend fun findContactBySender(
        email: String? = null,
        senderName: String? = null
    ): Contact? = runCatching {
        dataRepository.findContactBySender(email, senderName)
    }.getOrNull()
}