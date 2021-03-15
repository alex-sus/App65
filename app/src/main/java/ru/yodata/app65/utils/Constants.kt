package ru.yodata.app65.utils

import android.net.Uri
import ru.yodata.app65.model.Contact
import java.util.*
import kotlin.time.hours

object Constants {

    const val TAG = "App65"
    const val CONTACT_ID = "id"
    const val BIRTHDAY_MESSAGE = "b-msg"
    const val ALARM_SECOND_SHIFT = 50 // Для отладки: сдвиг от текущего времени в секундах
    const val CHANNEL_ID = "ru.yodata.app65" // id канала нотификаций
    const val ANDROID_RESOURCE = "android.resource://"
    lateinit var notificationSound: Uri // звук нотификации

    val contactList: List<Contact> = listOf(Contact(
            id = "1",
            name = "Суслопаров Алексей Владимирович",
            birthday = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.MONTH, 0)
                set(Calendar.YEAR, 1900)
            },
            phone1 = "+7-909-111-22-33",
            phone2 = "(3412)77-88-99",
            email1 = "one@gmail.com",
            email2 = "two@gmail.com",
            description = "Стиль — это один или несколько сгруппированных атрибутов " +
                "форматирования, которые отвечают за внешний вид и поведение элементов или окна. " +
                "Стиль может задавать такие свойства, как ширину, отступы, цвет текста, " +
                "размер шрифта, цвет фона и так далее.",
            photo = ""
        )
    )

    // Время, в которое будет стартовать напоминание о дне рождения.
    // Время автоматически задается текущее плюс количество секунд в ALARM_SECOND_SHIFT
    var alarmStartTime = Calendar.getInstance().apply { add(Calendar.SECOND, ALARM_SECOND_SHIFT) }
        .let { time ->
            AlarmStartTime(
                hour = time.get(Calendar.HOUR),
                minute = time.get(Calendar.MINUTE),
                second = time.get(Calendar.SECOND)
            )
    }

}

data class AlarmStartTime(
    val hour: Int,
    val minute: Int,
    val second: Int
)