package ru.yodata.app65.di.map.contact

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.yodata.app65.di.viewmodel_factory.ViewModelKey
import ru.yodata.library.viewmodel.ContactLocationsViewModel

@Module
abstract class ContactLocationsViewModelModule {
    @ContactMapScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactLocationsViewModel::class)
    abstract fun bindContactLocationsViewModel(viewModel: ContactLocationsViewModel): ViewModel
}
