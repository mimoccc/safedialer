package org.mjdev.safedialer.viewmodel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.kodein.di.DIAware
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.ContextExt.application
import org.mjdev.safedialer.extensions.DiExt.closestDI
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.repository.base.IDataRepository

class MainViewModel(
    context: Context,
    val dataRepository: IDataRepository
) : AndroidViewModel(context.application) , DIAware {

    override val di by context.closestDI { mainDI(context) }

    private val _serverState = MutableStateFlow(false)
    val serverState: StateFlow<Boolean> = _serverState.asStateFlow()

    private val _isTabsVisible = MutableStateFlow(true)
    val isTabsVisible: StateFlow<Boolean> = _isTabsVisible.asStateFlow()

    val contacts = dataRepository.contacts

    val contactMap = dataRepository.contactsMap
    val callsMap = dataRepository.callsMap
    val messagesMap = dataRepository.messagesMap
    val messageThreads = dataRepository.emailsMap
    val aiMessages = dataRepository.aiMap

    fun toggleServerState() {
        _serverState.value = !_serverState.value
    }

    fun setTabsVisible(visible: Boolean) {
        _isTabsVisible.value = visible
    }

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
