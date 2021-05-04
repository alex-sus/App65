package ru.yodata.java.interactors.contacts

import ru.yodata.java.entities.BriefContact

interface ContactListInteractor {
    suspend fun getContactList(): List<BriefContact>
}