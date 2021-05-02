package ru.yodata.java.interactors.contacts

import ru.yodata.java.entities.BriefContact
import ru.yodata.java.interactors.ContactRepositoryInterface

class ContactListModel(private val repository: ContactRepositoryInterface) : ContactListInteractor {

    override suspend fun getContactList(): List<BriefContact> = repository.getContactList()
}