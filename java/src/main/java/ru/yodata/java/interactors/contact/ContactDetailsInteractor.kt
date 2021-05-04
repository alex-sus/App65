package ru.yodata.java.interactors.contact

import ru.yodata.java.entities.Contact

interface ContactDetailsInteractor {
    suspend fun getContactById(contactId: String): Contact
}