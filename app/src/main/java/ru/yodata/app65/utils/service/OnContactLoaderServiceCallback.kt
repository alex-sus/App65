package ru.yodata.app65.utils.service

import android.content.ContentResolver
import ru.yodata.app65.model.BriefContact
import ru.yodata.app65.model.Contact

interface OnContactLoaderServiceCallback {

    fun isServiceBound(): Boolean

    fun getContactList(contResolver: ContentResolver): List<BriefContact>

    fun getContactById(contResolver: ContentResolver, contactId: String): Contact

}