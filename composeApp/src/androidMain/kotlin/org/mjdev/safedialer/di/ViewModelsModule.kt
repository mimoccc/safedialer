package org.mjdev.safedialer.di
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.mjdev.safedialer.viewmodel.MainViewModel

val viewModelsModule = DI.Module("ViewModelsModule") {
    bindProvider<MainViewModel> {
        MainViewModel(
            contactsRepository = instance()
        )
    }
}