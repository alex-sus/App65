package ru.yodata.java.interactors

import java.util.*

class AlarmCalendarRepository : AlarmCalendarRepositoryInterface {

    override fun now(): Calendar = Calendar.getInstance()

}