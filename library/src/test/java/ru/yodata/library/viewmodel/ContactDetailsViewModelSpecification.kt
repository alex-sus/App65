package ru.yodata.library.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.contact.ContactDetailsInteractor
import java.util.*

private const val DAY_OF_MONTH_8 = 8
private const val YEAR_1980 = 1980

// Интеграционные тесты на Spek

@ExperimentalCoroutinesApi
class ContactDetailsViewModelSpecification : Spek({

    val testDispatcher = TestCoroutineDispatcher()

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

    lateinit var testInteractor: ContactDetailsInteractor
    lateinit var testViewModel: ContactDetailsViewModel
    lateinit var contact: Contact

    fun setup() {
        Dispatchers.setMain(testDispatcher)
        testInteractor = mock()
        testViewModel = ContactDetailsViewModel(testInteractor)
    }

    /*fun finish() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }*/


    Feature("Установка и удаление уведомления о дне рождения контакта") {
        setup()

        Scenario("Установка напоминания") {
            Given("Есть контакт Иван Иванович") {
                contact = ivanContact
            }
            When("Устанавливается напоминание") {
                runBlocking {
                testViewModel.setBirthdayAlarm(contact).join()
                }
            }
            Then("Напоминание установлено") {
                runBlocking {
                    testInteractor.setBirthdayAlarm(eq(contact))
                }
            }
        }
        Scenario("Отмена напоминания") {
            Given("Есть контакт Иван Иванович") {
                contact = ivanContact
            }
            When("Напоминание отменяется") {
                runBlocking {
                    testViewModel.cancelBirthdayAlarm(contact).join()
                }
            }
            Then("Напоминание отменено") {
                runBlocking {
                    verify(testInteractor).cancelBirthdayAlarm(eq(contact))
                }
            }
        }
        Scenario("Проверка напоминания") {
            Given("Есть контакт Иван Иванович") {
                contact = ivanContact
            }
            When("Напоминание проверяется") {
                runBlocking {
                    testViewModel.isBirthdayAlarmOn(contact)
                }
            }
            Then("Напоминание проверено") {
                runBlocking {
                    verify(testInteractor).isBirthdayAlarmOn(contact)
                }
            }
        }

    }
})