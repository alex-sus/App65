package ru.yodata.app65.di.contact

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.yodata.app65.di.viewmodel_factory.ViewModelKey
import ru.yodata.library.viewmodel.ContactDetailsViewModel

@Module
abstract class ContactDetailsViewModelModule {

    @ContactsDetailsScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactDetailsViewModel::class)
    abstract fun bindContactDetailsViewModel(viewModel: ContactDetailsViewModel): ViewModel
}