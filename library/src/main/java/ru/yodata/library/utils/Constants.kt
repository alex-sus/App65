package ru.yodata.library.utils

import android.net.Uri
import java.util.*

object Constants {

    const val TAG = "App65"
    const val CONTACT_ID = "id"
    const val BIRTHDAY_MESSAGE = "b-msg"
    const val ALARM_SECOND_SHIFT = 50 // Для отладки: сдвиг от текущего времени в секундах
    const val CHANNEL_ID = "ru.yodata.app65" // id канала нотификаций
    const val ANDROID_RESOURCE = "android.resource://"
    const val EMPTY_VALUE = "-"
    lateinit var notificationSound: Uri // звук нотификации

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