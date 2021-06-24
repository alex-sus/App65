package ru.yodata.java.interactors.contact

import kotlinx.coroutines.flow.Flow
import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData
import java.util.*

interface ContactDetailsInteractor {

    suspend fun getContactById(contactId: String): Flow<Contact>

    suspend fun getLocationDataById(contactId: String): Flow<LocationData?>

    suspend fun deleteLocationDataById(contactId: String)

    suspend fun setBirthdayAlarm(curContact: Contact)

    suspend fun cancelBirthdayAlarm(curContact: Contact)

    fun isBirthdayAlarmOn(curContact: Contact): Boolean

    fun getAlarmStartMomentFor(contactBirthday: Calendar): Calendar
}