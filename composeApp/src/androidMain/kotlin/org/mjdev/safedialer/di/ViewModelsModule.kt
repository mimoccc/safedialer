package org.mjdev.safedialer.di

import android.content.Context
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.mjdev.safedialer.repository.DataRepository
import org.mjdev.safedialer.repository.base.IDataRepository
import org.mjdev.safedialer.viewmodel.MainViewModel

val viewModelsModule = DI.Module("ViewModelsModule") {
    bindProvider<MainViewModel> {
        val context : Context = instance()
        val dataRepository : IDataRepository = instance()
        MainViewModel(
            context = context,
            dataRepository = dataRepository
        )
    }
}