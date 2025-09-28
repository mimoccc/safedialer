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
//import kotlinx.coroutines.flow.shareIn
//import org.mjdev.safedialer.data.enums.CallType
//import org.mjdev.safedialer.data.lists.CallLogList
//import org.mjdev.safedialer.data.lists.ContactList
//import org.mjdev.safedialer.data.lists.EmailMessageList
//import org.mjdev.safedialer.data.lists.TextMessagesList
//import org.mjdev.safedialer.data.model.CallModel
//import org.mjdev.safedialer.data.model.ContactModel
//import org.mjdev.safedialer.data.repository.base.IDataRepository
//import org.mjdev.safedialer.helpers.Cache
//
//@Suppress("UNCHECKED_CAST", "DEPRECATION")
//class DataRepository(
//    context: Context,
//    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
//    cache: Cache = Cache(),
//) : IDataRepository(context, scope, cache) {
//
//    override fun getContacts(): Flow<ContactList> = flow {
//        ContactList().apply {
//            addAll(mockContacts)
//        }.apply {
//            emit(this)
//        }
//    }.flowOn(Dispatchers.IO).shareIn(scope, SharingStarted.Eagerly, 1)
//
//    override fun getCalls(): Flow<CallLogList> = flow {
//        CallLogList().apply {
//            addAll(mockCalls)
//        }.apply {
//            emit(this)
//        }
//    }.flowOn(Dispatchers.IO).shareIn(scope, SharingStarted.Eagerly, 1)
//
//    override fun getTextMessages(): Flow<TextMessagesList> = flow {
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
//            ContactModel(
//                phoneNumber = "+421 999 000 99$idx",
//                displayName = "John Doe $idx",
//                date = System.currentTimeMillis(),
//                contactId = idx.toLong(),
//                id = idx.toLong(),
//                photoThumbnailUri = "https://example.com/johndoe.jpg",
//                photoUri = null,
//                isBlocked = false,
//                isDanger = false,
//                isFine = true,
//                emails = listOf("john.doe$idx@example.com"),
//            )
//        }
//
//        val mockCalls = (1..8).map { idx ->
//            CallModel(
//                "+421 999 000 99$idx",
//                System.currentTimeMillis(),
//                idx.toLong(),
//                idx.toLong(),
//                1200,
//                CallType.INCOMING,
//                mockContacts.firstOrNull { fc -> fc.phoneNumber == "+421 999 000 999" },
//                ""
//            )
//        }
//    }
//}
