package ru.yodata.java.interactors.contact

import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.argWhere
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
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

// Unit-тестирование на Spek
class ContactDetailsModelSpecification : Spek( {

    val birthdayAlarmRepository: BirthdayAlarmRepositoryInterface = mock()

    val ivanContact = Contact(
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
    val pavelContact = Contact(
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
    lateinit var testModel:  ContactDetailsInteractor
    lateinit var contact: Contact

    Feature("Функционирование напоминания о дне рождения контакта") {

        fun mockBirthdayAlarmSetting(
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

        Scenario("Успешное добавление напоминания на следующий год") {
            Given(" Текущий год - 1999(не високосный) 9 сентября") {
                testModel = mockBirthdayAlarmSetting(
                    curDay = DAY_OF_MONTH_9,
                    curMonth = Calendar.SEPTEMBER,
                    curYear = YEAR_1999
                )
            }
            And("Есть контакт Иван Иванович с датой рождения 8 сентября") {
                contact = ivanContact
            }
            When("Пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович") {
                runBlocking {
                    testModel.setBirthdayAlarm(contact)
                }
            }
            Then("Происходит успешное добавление напоминания на 2000 год 8 сентября") {
                verify(birthdayAlarmRepository).setBirthdayAlarm(
                    curContact = eq(contact),
                    alarmStartMoment = argWhere {
                        it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_8 &&
                                it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                                it[Calendar.YEAR] == YEAR_2000
                    })
            }

        }
        Scenario("Успешное добавление напоминания, ДР еще в текущем году не было") {
            Given("Текущий год - 1999(не високосный) 7 сентября") {
                testModel = mockBirthdayAlarmSetting(
                    curDay = DAY_OF_MONTH_7,
                    curMonth = Calendar.SEPTEMBER,
                    curYear = YEAR_1999
                )
            }
            And("Есть контакт Иван Иванович с датой рождения 8 сентября") {
                contact = ivanContact
            }
            When("Пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович") {
                runBlocking {
                    testModel.setBirthdayAlarm(contact)
                }
            }
            Then("Происходит успешное добавление напоминания на 1999 год 8 сентября") {
                verify(birthdayAlarmRepository).setBirthdayAlarm(
                    curContact = eq(contact),
                    alarmStartMoment = argWhere {
                        it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_8 &&
                                it[Calendar.MONTH] == Calendar.SEPTEMBER &&
                                it[Calendar.YEAR] == YEAR_1999
                    })
            }
        }
        Scenario("Успешное удаление напоминания") {
            Given("Текущий год - 1999(не високосный)") {
                testModel = mockBirthdayAlarmSetting(
                    curDay = DAY_OF_MONTH_9,
                    curMonth = Calendar.SEPTEMBER,
                    curYear = YEAR_1999
                )
            }
            And("Есть контакт Иван Иванович с датой рождения 8 сентября") {
                contact = ivanContact
            }
            And("И для него включено напоминание на 2000 год 8 сентября") {
                runBlocking {
                    testModel.setBirthdayAlarm(contact)
                }
            }
            When("Пользователь кликает на кнопку напоминания в детальной информации контакта Иван Иванович") {
                runBlocking {
                    testModel.cancelBirthdayAlarm(contact)
                }
            }
            Then("Происходит успешное удаление напоминания") {
                verify(birthdayAlarmRepository).cancelBirthdayAlarm(eq(contact))
            }
        }
        Scenario("Добавление напоминания для контакта родившегося 29 февраля") {
            Given("Текущий год - 1999(не високосный), следующий 2000(високосный) 2 марта") {
                testModel = mockBirthdayAlarmSetting(
                    curDay = DAY_OF_MONTH_2,
                    curMonth = Calendar.MARCH,
                    curYear = YEAR_1999
                )
            }
            And("Есть контакт Павел Павлович с датой рождения 29 февраля") {
                contact = pavelContact
            }
            When("Пользователь кликает на кнопку напоминания в детальной информации контакта Павел Павлович") {
                runBlocking {
                    testModel.setBirthdayAlarm(contact)
                }
            }
            Then("Происходит успешное добавление напоминания на 2000 год 29 февраля") {
                verify(birthdayAlarmRepository).setBirthdayAlarm(
                    curContact = eq(contact),
                    alarmStartMoment = argWhere {
                        it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_29 &&
                                it[Calendar.MONTH] == Calendar.FEBRUARY &&
                                it[Calendar.YEAR] == YEAR_2000
                    })

            }
        }
        Scenario("Добавление напоминания для контакта родившегося 29 февраля в високосный год") {
            Given("Текущий год - 2000(високосный) 1 марта") {
                testModel = mockBirthdayAlarmSetting(
                    curDay = DAY_OF_MONTH_1,
                    curMonth = Calendar.MARCH,
                    curYear = YEAR_2000
                )
            }
            And("Есть контакт Павел Павлович с датой рождения 29 февраля") {
                contact = pavelContact
            }
            When("Пользователь кликает на кнопку напоминания в детальной информации контакта Павел Павлович") {
                runBlocking {
                    testModel.setBirthdayAlarm(contact)
                }
            }
            Then("Происходит успешное добавление напоминания на 2004 год 29 февраля") {
                verify(birthdayAlarmRepository).setBirthdayAlarm(
                    curContact = eq(contact),
                    alarmStartMoment = argWhere {
                        it[Calendar.DAY_OF_MONTH] == DAY_OF_MONTH_29 &&
                                it[Calendar.MONTH] == Calendar.FEBRUARY &&
                                it[Calendar.YEAR] == YEAR_2004
                    })
            }
        }
    }
} )