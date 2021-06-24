package ru.yodata.java.interactors.contact

import kotlinx.coroutines.flow.Flow
import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData
import ru.yodata.java.interactors.AlarmCalendarRepositoryInterface
import ru.yodata.java.interactors.BirthdayAlarmRepositoryInterface
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import java.util.*

private const val DAY_OF_MONTH_29 = 29
private const val LEAP_YEAR_PERIOD = 4

// Для демонстрации напоминания о ДР: сдвиг вперед от текущего времени (в секундах)
private const val ALARM_SECOND_SHIFT = 20

class ContactDetailsModel(
    private val contactRepository: ContactRepositoryInterface,
    private val locationRepository: ContactLocationRepositoryInterface,
    private val alarmRepository: BirthdayAlarmRepositoryInterface,
    private val calendarRepository: AlarmCalendarRepositoryInterface
) : ContactDetailsInteractor {

    override suspend fun getContactById(contactId: String): Flow<Contact> =
        contactRepository.getContactById(contactId)

    override suspend fun getLocationDataById(contactId: String): Flow<LocationData?> =
        locationRepository.getLocationDataById(contactId)

    override suspend fun deleteLocationDataById(contactId: String) {
        locationRepository.deleteLocationDataById(contactId)
    }

    override fun isBirthdayAlarmOn(curContact: Contact): Boolean =
        alarmRepository.isBirthdayAlarmOn(curContact)

    override suspend fun setBirthdayAlarm(curContact: Contact) {
        if (curContact.birthday != null) {
            alarmRepository.setBirthdayAlarm(
                curContact,
                getAlarmStartMomentFor(curContact.birthday)
            )
        }
    }

    override suspend fun cancelBirthdayAlarm(curContact: Contact) {
        alarmRepository.cancelBirthdayAlarm(curContact)
    }

    override fun getAlarmStartMomentFor(contactBirthday: Calendar/*, today: Calendar*/): Calendar {
        val today = calendarRepository.now()
        val curYear = today[Calendar.YEAR]
        val alarmStartMoment = today.clone() as Calendar
        if (contactBirthday[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_29
            && contactBirthday[Calendar.MONTH] == Calendar.FEBRUARY
        ) { // ДР контакта = 29 февраля
            val remainder = curYear % 4 // високосные годы делятся на 4 без остатка
            if (remainder == 0) { // если текущий год високосный,
                alarmStartMoment[Calendar.DAY_OF_MONTH] = DAY_OF_MONTH_29
                alarmStartMoment[Calendar.MONTH] = Calendar.FEBRUARY
                //alarmStartMoment[Calendar.YEAR] = curYear
                //}
                // если 29 февраля в этом году, но уже прошло, перенести его на 4 года вперед
                if (alarmStartMoment.before(today)) alarmStartMoment[Calendar.YEAR] = curYear + 4
            } else {
                // если текущий год не високосный, вычислить ближайший високосный
                alarmStartMoment[Calendar.DAY_OF_MONTH] = DAY_OF_MONTH_29
                alarmStartMoment[Calendar.MONTH] = Calendar.FEBRUARY
                alarmStartMoment[Calendar.YEAR] = curYear + (LEAP_YEAR_PERIOD - remainder)
            }
        } else { // ДР контакта - не 29 февраля
            alarmStartMoment[Calendar.DAY_OF_MONTH] = contactBirthday[Calendar.DAY_OF_MONTH]
            alarmStartMoment[Calendar.MONTH] = contactBirthday[Calendar.MONTH]
            alarmStartMoment[Calendar.YEAR] = curYear
            // Для демонстрации время срабатывания устанавливается текущее + ALARM_SECOND_SHIFT
            alarmStartMoment.add(Calendar.SECOND, ALARM_SECOND_SHIFT)
            // Если ДР в этом году, но уже прошло, перенести напоминание на следующий год
            if (alarmStartMoment.before(today))
                alarmStartMoment[Calendar.YEAR] = curYear + 1
        }
        return alarmStartMoment
    }
}