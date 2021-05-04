package ru.yodata.java.interactors.contact

import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.ContactRepositoryInterface

class ContactDetailsModel(private val repository: ContactRepositoryInterface) :
    ContactDetailsInteractor {

    override suspend fun getContactById(contactId: String): Contact =
        repository.getContactById(contactId)
}