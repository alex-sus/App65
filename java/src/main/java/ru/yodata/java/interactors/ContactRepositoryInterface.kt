package ru.yodata.java.interactors

import ru.yodata.java.entities.BriefContact
import ru.yodata.java.entities.Contact

interface ContactRepositoryInterface {

    suspend fun getContactList(): List<BriefContact>
    suspend fun getBriefContactById(contactId: String): BriefContact?
    suspend fun getContactById(contactId: String): Contact
}