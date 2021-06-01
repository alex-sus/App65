package ru.yodata.java.interactors

import java.util.*

interface AlarmCalendarRepositoryInterface {

    fun now(): Calendar

    /*fun getAlarmStartMomentFor(
        contactBirthday: Calendar,
        today: Calendar = Calendar.getInstance()
    ): Calendar*/
}