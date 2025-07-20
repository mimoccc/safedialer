package org.mjdev.safedialer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mjdev.safedialer.data.ContactsRepository
import org.mjdev.safedialer.data.model.ContactModel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("unused")
class MainViewModel(
    val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    private val _serverState = MutableStateFlow(false)
    val serverState: StateFlow<Boolean> = _serverState.asStateFlow()

    private val _isTabsVisible = MutableStateFlow(true)
    val isTabsVisible: StateFlow<Boolean> = _isTabsVisible.asStateFlow()

    val contactMap = contactsRepository.callLogMap
    val callLogMap = contactsRepository.callLogMap
    val messagesMap = contactsRepository.messagesMap

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
            contactsRepository.contacts.collect { contactsList ->
            }
        }
        runSafe {
            contactsRepository.calls.collect { callLogList ->
            }
        }
        runSafe {
            contactsRepository.sms.collect { messagesList ->
            }
        }
    }

    fun findContact(phoneNumber: String): ContactModel {
        return contactsRepository.findContact(phoneNumber)
    }
}