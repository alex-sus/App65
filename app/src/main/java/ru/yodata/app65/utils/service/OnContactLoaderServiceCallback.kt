package ru.yodata.app65.utils.service

import ru.yodata.app65.model.Contact

interface OnContactLoaderServiceCallback {

    fun isServiceBound(): Boolean

    suspend fun getContactList(): List<Contact>

    suspend fun getContactById(contactId: String): Contact

}