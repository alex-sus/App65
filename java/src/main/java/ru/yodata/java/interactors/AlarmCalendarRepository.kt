package ru.yodata.java.interactors

import java.util.*

/*private const val DAY_OF_MONTH_29 = 29
private const val LEAP_YEAR_PERIOD = 4

// Для демонстрации напоминания о ДР: сдвиг вперед от текущего времени (в секундах)
private const val ALARM_SECOND_SHIFT = 30*/

class AlarmCalendarRepository : AlarmCalendarRepositoryInterface {

    override fun now(): Calendar = Calendar.getInstance()

    /*override fun getAlarmStartMomentFor(contactBirthday: Calendar, today: Calendar): Calendar {
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
    }*/
}