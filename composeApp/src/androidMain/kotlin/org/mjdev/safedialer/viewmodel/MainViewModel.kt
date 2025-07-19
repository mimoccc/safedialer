package org.mjdev.safedialer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mjdev.safedialer.data.repository.DataRepository
import org.mjdev.safedialer.data.model.ContactModel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("unused")
class MainViewModel(
    val dataRepository: DataRepository
) : ViewModel() {

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    private val _serverState = MutableStateFlow(false)
    val serverState: StateFlow<Boolean> = _serverState.asStateFlow()

    private val _isTabsVisible = MutableStateFlow(true)
    val isTabsVisible: StateFlow<Boolean> = _isTabsVisible.asStateFlow()

    val contactMap = dataRepository.contactsMap
    val callLogMap = dataRepository.callLogMap
    val messagesMap = dataRepository.messagesMap
    val emailMessages = dataRepository.emailsMap

    init {
        loadData()
    }

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

    private fun loadData() {
        runSafe {
            dataRepository.contacts.collect { contactsList ->
            }
        }
        runSafe {
            dataRepository.calls.collect { callLogList ->
            }
        }
        runSafe {
            dataRepository.sms.collect { messagesList ->
            }
        }
    }

    fun findContact(phoneNumber: String): ContactModel {
        return dataRepository.findContactByPhone(phoneNumber)
    }
}