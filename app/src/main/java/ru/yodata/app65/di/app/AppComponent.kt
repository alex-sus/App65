package ru.yodata.app65.di.app

import dagger.Component
import ru.yodata.app65.di.contact.ContactDetailViewModelComponent
import ru.yodata.app65.di.contacts.ContactListViewModelComponent
import ru.yodata.app65.di.viewmodel_factory.ViewModelFactoryModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelFactoryModule::class])
interface AppComponent {
    fun plusContactListViewModelComponent(): ContactListViewModelComponent
    fun plusContactDetailViewModelComponent(): ContactDetailViewModelComponent
}