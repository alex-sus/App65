package ru.yodata.app65.model

interface ContactRepositoryInterface {

    suspend fun getContactList(): List<BriefContact>
    fun getContactById(contactId: String): Contact
}