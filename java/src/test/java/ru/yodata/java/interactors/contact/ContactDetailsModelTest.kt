package ru.yodata.java.interactors.contact

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.argWhere
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.BirthdayAlarmRepositoryInterface
import java.util.*

private const val DAY_OF_MONTH_1 = 1
private const val DAY_OF_MONTH_2 = 2
private const val DAY_OF_MONTH_7 = 7
private const val DAY_OF_MONTH_8 = 8
private const val DAY_OF_MONTH_9 = 9
private const val DAY_OF_MONTH_29 = 29
private const val YEAR_1980 = 1980
private const val YEAR_1999 = 1999
private const val YEAR_2000 = 2000
private const val YEAR_2004 = 2004

class ContactDetailsModelTest {

    private val birthdayAlarmRepository: BirthdayAlarmRepositoryInterface = mock()

    //private val alarmCalendarRepository = AlarmCalendarRepository()
    private val ivanContact = Contact(
        id = "1",
        name = "Иван Иванович",
        birthday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, DAY_OF_MONTH_8)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.YEAR, YEAR_1980)
        },
        phone1 = "",
        phone2 = "",
        email1 = "",
        email2 = "",
        description = "",
        bigPhotoUri = null
    )
    private val pavelContact = Contact(
        id = "2",
        name = "Павел Павлович",
        birthday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, DAY_OF_MONTH_29)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.YEAR, YEAR_1980)
        },
        phone1 = "",
        phone2 = "",
        email1 = "",
        email2 = "",
        description = "",
        bigPhotoUri = null
    )

    /*
    Сценарий: успешное добавление напоминания
    Текущий год - 1999(не високосный) 9 сентября
    Есть контакт Иван Иванович с датой рождения 8 сентября
    И напоминание для этого контакта отсутствует
    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович
    Тогда происходит успешное добавление напоминания на 2000 год 8 сентября
    */
    @Test
    fun setAlarmAfterBirthday() {
        val testModel = mockBirthdayAlarmSetting(
            curDay = DAY_OF_MONTH_9,
            curMonth = Calendar.SEPTEMBER,
            curYear = YEAR_1999
        )
        runBlocking {
            testModel.setBirthdayAlarm(ivanContact)
        }
        verify(birthdayAlarmRepository).setBirthdayAlarm(
            curContact = eq(ivanContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_8 &&
                        it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                        it[Calendar.YEAR] == YEAR_2000
            })
    }

    /*
    Сценарий: успешное добавление напоминания, ДР еще в текущем году не было
    Текущий год - 1999(не високосный) 7 сентября
    Есть контакт Иван Иванович с датой рождения 8 сентября
    И напоминание для этого контакта отсутствует
    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович
    Тогда происходит успешное добавление напоминания на 1999 год 8 сентября
    */
    @Test
    fun setAlarmBeforeBirthday() {
        val testModel = mockBirthdayAlarmSetting(
            curDay = DAY_OF_MONTH_7,
            curMonth = Calendar.SEPTEMBER,
            curYear = YEAR_1999
        )
        runBlocking {
            testModel.setBirthdayAlarm(ivanContact)
        }
        verify(birthdayAlarmRepository).setBirthdayAlarm(
            curContact = eq(ivanContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_8 &&
                        it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                        it[Calendar.YEAR] == YEAR_1999
            })
    }

    /*
    Сценарий: успешное удаление напоминания
    Текущий год - 1999(не високосный)
    Есть контакт Иван Иванович с датой рождения 8 сентября
    И для него включено напоминание на 2000 год 8 сентября
    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович
    Тогда происходит успешное удаление напоминания
    */
    @Test
    fun cancelExistingAlarm() {
        val testModel = mockBirthdayAlarmSetting(
            curDay = DAY_OF_MONTH_7,
            curMonth = Calendar.SEPTEMBER,
            curYear = YEAR_1999
        )
        runBlocking {
            testModel.setBirthdayAlarm(ivanContact)
            testModel.cancelBirthdayAlarm(ivanContact)
        }
        verify(birthdayAlarmRepository).cancelBirthdayAlarm(eq(ivanContact))
    }

    /*
    Сценарий: добавление напоминания для контакта родившегося 29 февраля
    Текущий год - 1999(не високосный), следующий 2000(високосный) 2 марта
    Есть контакт Павел Павлович с датой рождения 29 февраля
    И напоминание для этого контакта отсутствует
    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Павел Павлович
    Тогда происходит успешное добавление напоминания на 2000 год 29 февраля
    */
    @Test
    fun setAlarmBeforeLeapYear() {
        val testModel = mockBirthdayAlarmSetting(
            curDay = DAY_OF_MONTH_2,
            curMonth = Calendar.MARCH,
            curYear = YEAR_1999
        )
        runBlocking {
            testModel.setBirthdayAlarm(pavelContact)
        }
        verify(birthdayAlarmRepository).setBirthdayAlarm(
            curContact = eq(pavelContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_29 &&
                        it[Calendar.MONTH] == Calendar.FEBRUARY &&
                        it[Calendar.YEAR] == YEAR_2000
            })
    }

    /*
    Сценарий: добавление напоминания для контакта родившегося 29 февраля в високосный год
    Текущий год - 2000(високосный) 1 марта
    Есть контакт Павел Павлович с датой рождения 29 февраля
    И напоминание для этого контакта отсутствует
    Когда пользователь кликает на кнопку напоминания в детальной информации контакта Павел Павлович
    Тогда происходит успешное добавление напоминания на 2004 год 29 февраля(т.е. пропускаем 4 года,
    представим, что бедняги празднуют ДР раз в 4 года)
    */
    @Test
    fun setAlarmAfterLeapYear() {
        val testModel = mockBirthdayAlarmSetting(
            curDay = DAY_OF_MONTH_1,
            curMonth = Calendar.MARCH,
            curYear = YEAR_2000
        )
        runBlocking {
            testModel.setBirthdayAlarm(pavelContact)
        }
        verify(birthdayAlarmRepository).setBirthdayAlarm(
            curContact = eq(pavelContact),
            alarmStartMoment = argWhere {
                it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_29 &&
                        it[Calendar.MONTH] == Calendar.FEBRUARY &&
                        it[Calendar.YEAR] == YEAR_2004
            })
    }

    private fun mockBirthdayAlarmSetting(
        curDay: Int,
        curMonth: Int,
        curYear: Int
    ): ContactDetailsInteractor {
        return ContactDetailsModel(
            contactRepository = mock(),
            locationRepository = mock(),
            alarmRepository = birthdayAlarmRepository,
            calendarRepository = mock {
                on { now() }.then {
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, curDay)
                        set(Calendar.MONTH, curMonth)
                        set(Calendar.YEAR, curYear)
                    }
                }
            }
        )
    }

    /*private fun mockBirthdayAlarmSetting(
        curDay: Int,
        curMonth: Int,
        curYear: Int
    ): ContactDetailsInteractor {
        return ContactDetailsModel(
            contactRepository = mock(),
            locationRepository = mock(),
            alarmRepository = birthdayAlarmRepository,
            calendarRepository = mock {
                on { getAlarmStartMomentFor(any(), any()) }.then {
                    alarmCalendarRepository.getAlarmStartMomentFor(
                        contactBirthday = it.arguments[0] as Calendar,
                        today = Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_MONTH, curDay)
                            set(Calendar.MONTH, curMonth)
                            set(Calendar.YEAR, curYear)
                        }
                    )
                }
            }
        )
    }*/
}