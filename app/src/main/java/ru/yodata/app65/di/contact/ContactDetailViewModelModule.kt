package ru.yodata.app65.di.contact

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.yodata.app65.di.viewmodel_factory.ViewModelKey
import ru.yodata.app65.viewmodel.ContactDetailViewModel

@Module
abstract class ContactDetailViewModelModule {

    @ContactsDetailsScope
    @Binds
    @IntoMap
    @ViewModelKey(ContactDetailViewModel::class)
    abstract fun bindContactDetailViewModel(viewModel: ContactDetailViewModel): ViewModel
}