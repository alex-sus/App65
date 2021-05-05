package ru.yodata.app65.di.contact

import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.java.interactors.contact.ContactDetailsInteractor
import ru.yodata.java.interactors.contact.ContactDetailsModel

@Module
class ContactDetailsModule {
    @ContactsDetailsScope
    @Provides
    fun providesContactDetailsInteractor(
            contactRepository: ContactRepositoryInterface,
            locRepository: ContactLocationRepositoryInterface
    ): ContactDetailsInteractor =
            ContactDetailsModel(contactRepository, locRepository)
}