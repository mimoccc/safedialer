package org.mjdev.safedialer.di

import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.mjdev.safedialer.providers.custom.email.EmailsProvider
import org.mjdev.safedialer.providers.android.calendar.CalendarProvider
import org.mjdev.safedialer.providers.android.calllog.CallsProvider
import org.mjdev.safedialer.providers.android.contacts.ContactsProvider
import org.mjdev.safedialer.providers.android.dictionary.DictionaryProvider
import org.mjdev.safedialer.providers.android.media.MediaProvider
import org.mjdev.safedialer.providers.android.messages.MessagesProvider
import org.mjdev.safedialer.providers.android.telephony.TelephonyProvider
import org.mjdev.safedialer.providers.custom.ai.AIProvider
import org.mjdev.safedialer.providers.custom.auth.AuthProvider
import org.mjdev.safedialer.providers.custom.document.DocumentsProvider
import org.mjdev.safedialer.providers.custom.invoice.InvoicesProvider
import org.mjdev.safedialer.providers.custom.notes.NotesProvider
import org.mjdev.safedialer.providers.custom.task.TasksProvider

val providersModule = DI.Module("ProvidersModule") {
    bindProvider <CalendarProvider> {
        CalendarProvider(instance())
    }
    bindProvider<CallsProvider> {
        CallsProvider(instance())
    }
    bindProvider<ContactsProvider> {
        ContactsProvider(instance())
    }
    bindProvider<DictionaryProvider> {
        DictionaryProvider(instance())
    }
    bindProvider<MediaProvider> {
        MediaProvider(instance())
    }
    bindProvider<TelephonyProvider> {
        TelephonyProvider(instance())
    }
    bindProvider<EmailsProvider> {
        EmailsProvider(instance())
    }
    bindProvider<AIProvider> {
        AIProvider(instance())
    }
    bindProvider<AuthProvider> {
        AuthProvider(instance())
    }
    bindProvider<DocumentsProvider> {
        DocumentsProvider(instance())
    }
    bindProvider<MessagesProvider> {
        MessagesProvider(instance())
    }
    bindProvider<InvoicesProvider> {
        InvoicesProvider(instance())
    }
    bindProvider<NotesProvider> {
        NotesProvider(instance())
    }
    bindProvider<TasksProvider> {
        TasksProvider(instance())
    }
    // todo other stuffs, gallery, music, files, presentations, repository, videos, web
}
