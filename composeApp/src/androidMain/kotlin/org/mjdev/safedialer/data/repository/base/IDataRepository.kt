package org.mjdev.safedialer.data.repository.base

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.mjdev.safedialer.data.model.CallModel
import org.mjdev.safedialer.data.model.ContactModel
import org.mjdev.safedialer.data.model.EmailMessageModel
import org.mjdev.safedialer.data.model.TextMessageModel
import org.mjdev.safedialer.data.repository.DataRepository
import org.mjdev.safedialer.helpers.Cache
import java.util.Date

@Suppress("DEPRECATION")
abstract class IDataRepository(
    val context: Context,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
    val cache: Cache = Cache(),
) : DIAware {
    override val di: DI by closestDI(context)

    val pnu: PhoneNumberUtil by instance()

//    private val dao: DAO by instance()
//    private val phoneLookup by instance<PhoneLookup>()
    var contactSearchMap = mapOf<String, ContactModel>()

    init {
        runSafeTask {
            getContacts().collectLatest { contacts ->
                contactSearchMap = contacts.associateBy { c -> c.phoneNumber }
                // phoneLookup.updateDetails(contacts)
                getCalls().collectLatest { //calls ->
                    // phoneLookup.updateDetails(calls)
                }
                getTextMessages().collectLatest { //sms ->
                    // phoneLookup.updateDetails(sms)
                }
                getEmails().collectLatest {
                    // phoneLookup.updateDetails(sms)
                }
            }
        }
    }

    abstract fun getContacts(): Flow<List<ContactModel>>
    abstract fun getCalls(): Flow<List<CallModel>>
    abstract fun getTextMessages(): Flow<List<TextMessageModel>>
    abstract fun getEmails(): Flow<List<EmailMessageModel>>

    val contactsMap = getContacts().map { cl ->
        cl.groupBy { c ->
            c.displayName.firstOrNull()?.uppercase() ?: ""
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, SharingStarted.Companion.Eagerly, 1)

    val callLogMap = getCalls().map { cl ->
        cl.groupBy { c ->
            Date(c.date).let {
                "${it.date}.${it.month + 1}.${it.year + 1900}"
            }
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, SharingStarted.Companion.Eagerly, 1)

    val messagesMap = getTextMessages().map { m ->
        m.groupBy { c ->
            Date(c.date).let {
                "${it.date}.${it.month + 1}.${it.year + 1900}"
            }
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, SharingStarted.Eagerly, 1)

    val emailsMap = getEmails().map { email ->
        email.groupBy { em ->
            em.mailboxName
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, SharingStarted.Eagerly, 1)

    fun runSafeTask(
        onError: (Throwable) -> Unit = { e -> e.printStackTrace() },
        block: suspend () -> Unit
    ): Job? = runCatching {
        Job().let { job ->
            scope.launch(Dispatchers.IO + job) {
                runCatching {
                    block()
                }.onFailure { e ->
                    onError(e)
                }
            }
            job
        }
    }.onFailure { e ->
        onError(e)
    }.getOrNull()

    companion object {
        @Volatile
        private var instance: IDataRepository? = null

        private fun getInstance(
            context: Context
        ): IDataRepository = synchronized(this) {
            instance ?: DataRepository(context).also { instance = it }
        }

        @Composable
        fun rememberContactsRepository(
            context: Context = LocalContext.current
        ) = remember {
            runCatching {
                getInstance(context)
            }.getOrElse { e ->
                throw (e)
            }
        }
    }
}