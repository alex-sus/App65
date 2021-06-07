package ru.yodata.library.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.contact.ContactDetailsInteractor
import java.util.*

private const val DAY_OF_MONTH_8 = 8
private const val DAY_OF_MONTH_29 = 29
private const val YEAR_1980 = 1980

class ContactDetailsViewModelTest {

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

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
    private lateinit var testInteractor: ContactDetailsInteractor
    private lateinit var testViewModel: ContactDetailsViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun before() {
        Dispatchers.setMain(testDispatcher)
        testInteractor = mock()
        testViewModel = ContactDetailsViewModel(testInteractor)
    }

    @ExperimentalCoroutinesApi
    @After
    fun after() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun setBirthdayAlarm() = runBlocking {
        testViewModel.setBirthdayAlarm(ivanContact).join()
        verify(testInteractor).setBirthdayAlarm(eq(ivanContact))
    }

    @Test
    fun cancelBirthdayAlarm() = runBlocking {
        testViewModel.cancelBirthdayAlarm(ivanContact).join()
        verify(testInteractor).cancelBirthdayAlarm(eq(ivanContact))
    }

    @Test
    fun isBirthdayAlarmOn() {
        testViewModel.isBirthdayAlarmOn(ivanContact)
        verify(testInteractor).isBirthdayAlarmOn(ivanContact)
    }
}