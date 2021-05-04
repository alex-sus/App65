package ru.yodata.app65.di.contacts

import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.java.interactors.contacts.ContactListInteractor
import ru.yodata.java.interactors.contacts.ContactListModel

@Module
class ContactListModule {
    @ContactsListScope
    @Provides
    fun provideContactListInteractor(repository: ContactRepositoryInterface): ContactListInteractor = ContactListModel(repository)

}