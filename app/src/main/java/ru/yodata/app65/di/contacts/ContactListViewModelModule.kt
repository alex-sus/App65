package ru.yodata.app65.di.contacts

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.yodata.app65.di.viewmodel_factory.ViewModelKey
import ru.yodata.app65.viewmodel.ContactListViewModel

@Module
abstract class ContactListViewModelModule {

    @ContactsListScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    abstract fun bindContactListViewModel(viewModel: ContactListViewModel): ViewModel
}