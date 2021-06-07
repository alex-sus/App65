package ru.yodata.java.interactors

import ru.yodata.java.entities.Contact
import java.util.*

interface BirthdayAlarmRepositoryInterface {

    fun isBirthdayAlarmOn(curContact: Contact): Boolean

    fun setBirthdayAlarm(curContact: Contact, alarmStartMoment: Calendar)

    fun cancelBirthdayAlarm(curContact: Contact)
}