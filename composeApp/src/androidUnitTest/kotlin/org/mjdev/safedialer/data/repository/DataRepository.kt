//package org.mjdev.safedialer.data.repository
//
//import android.content.Context
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.flow.internal.NopCollector.emit
//import kotlinx.coroutines.flow.shareIn
//import org.mjdev.safedialer.data.repository.base.IDataRepository
//import org.mjdev.safedialer.providers.android.calllog.Call
//import org.mjdev.safedialer.providers.android.contacts.Contact
//
//class DataRepository(
//    context: Context,
//    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
//) : IDataRepository(context, scope) {
//
//    val contacts: Flow<List<Contact>> = flow {
//        emit(mockContacts)
//    }.flowOn(Dispatchers.IO).shareIn(scope, SharingStarted.Eagerly, 1)
//
//    val calls: Flow<List<Call>> = flow {
//        emit(mockCalls)
//    }.flowOn(Dispatchers.IO).shareIn(scope, SharingStarted.Eagerly, 1)
//
//    val : Flow<TextMessagesList> = flow {
//        TextMessagesList().apply {
//            // todo
//        }.apply {
//            emit(this)
//        }
//    }.flowOn(
//        Dispatchers.IO
//    ).shareIn(scope, SharingStarted.Eagerly, 1)
//
//    override fun getEmails(): Flow<EmailMessageList> = flow {
//        EmailMessageList().apply {
//            // todo
//        }.apply {
//            emit(this)
//        }
//    }.flowOn(
//        Dispatchers.IO
//    ).shareIn(scope, SharingStarted.Eagerly, 1)
//
//    companion object {
//        val mockContacts = (1..8).map { idx ->
//            Contact(
//                phone = "+421 999 000 99$idx",
//                displayName = "John Doe $idx",
//                normalizedPhone = "+421 999 000 99$idx",
//                contactId = idx.toLong(),
//                id = idx.toLong(),
//                emails = listOf("john.doe$idx@example.com"),
//            )
//        }
//
//        val mockCalls = (1..8).map { idx ->
//            Call(
//                id = idx.toLong(),
//                name = "John Doe $idx",
//                callDate = System.currentTimeMillis(),
//                duration = 1000,
//                isRead = false,
//                number = "+421 999 000 99$idx",
//                type = Call.CallType.INCOMING,
//                contact = mockContacts.firstOrNull { fc -> fc.phone == "+421 999 000 99$idx" },
//            )
//        }
//    }
//}
