package ru.yodata.java.interactors.contact

import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData
import java.util.*

interface ContactDetailsInteractor {

    suspend fun getContactById(contactId: String): Contact

    suspend fun getLocationDataById(contactId: String): LocationData?

    suspend fun deleteLocationDataById(contactId: String)

    suspend fun setBirthdayAlarm(curContact: Contact)

    suspend fun cancelBirthdayAlarm(curContact: Contact)

    fun isBirthdayAlarmOn(curContact: Contact): Boolean

    fun getAlarmStartMomentFor(
        contactBirthday: Calendar,
        //today: Calendar = Calendar.getInstance()
    ): Calendar
}