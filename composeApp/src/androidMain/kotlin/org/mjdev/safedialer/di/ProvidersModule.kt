package org.mjdev.safedialer.di

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.mjdev.safedialer.providers.custom.email.EmailsProvider
import org.mjdev.safedialer.providers.android.calendar.CalendarProvider
import org.mjdev.safedialer.providers.android.calllog.CallsProvider
import org.mjdev.safedialer.providers.android.contacts.ContactsProvider
import org.mjdev.safedialer.providers.android.dictionary.DictionaryProvider
import org.mjdev.safedialer.providers.android.media.MediaProvider
import org.mjdev.safedialer.providers.android.telephony.TelephonyProvider

val providersModule = DI.Module("ProvidersModule") {
    bindSingleton<CalendarProvider> {
        CalendarProvider(instance())
    }
    bindSingleton<CallsProvider> {
        CallsProvider(instance())
    }
    bindSingleton<ContactsProvider> {
        ContactsProvider(instance())
    }
    bindSingleton<DictionaryProvider> {
        DictionaryProvider(instance())
    }
    bindSingleton<MediaProvider> {
        MediaProvider(instance())
    }
    bindSingleton<TelephonyProvider> {
        TelephonyProvider(instance())
    }
    bindSingleton<EmailsProvider> {
        EmailsProvider(instance())
    }
}
