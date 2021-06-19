package ru.yodata.library.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.BirthdayAlarmRepositoryInterface
import ru.yodata.library.R
import ru.yodata.library.utils.Constants
import ru.yodata.library.utils.Constants.TAG
import ru.yodata.library.utils.alarmbroadcast.BirthdayAlarmReceiver
import java.util.*
import javax.inject.Inject

private const val LEAP_YEAR_MILLISECONDS =
    1000 * 60 * 60 * 24 * 366L // кол-во миллисекунд в високосном году
private const val NORMAL_YEAR_MILLISECONDS =
    1000 * 60 * 60 * 24 * 365L // кол-во миллисекунд в обычном году
private const val LEAP_YEAR_PERIOD = 4

class BirthdayAlarmRepository @Inject constructor(
    private val appContext: Context
) : BirthdayAlarmRepositoryInterface {

    override fun isBirthdayAlarmOn(curContact: Contact) = PendingIntent.getBroadcast(
        appContext,
        curContact.id.hashCode(),
        Intent(appContext, BirthdayAlarmReceiver::class.java),
        PendingIntent.FLAG_NO_CREATE
    ) != null

    override fun setBirthdayAlarm(curContact: Contact, alarmStartMoment: Calendar) {
        if (curContact.birthday != null) {
            val birthday = curContact.birthday as Calendar
            val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmPendingIntent = Intent(appContext, BirthdayAlarmReceiver::class.java)
                .let { intent ->
                    intent.putExtra(
                        Constants.BIRTHDAY_MESSAGE,
                        appContext.getString(R.string.birthday_msg) + curContact.name
                    )
                    intent.putExtra(Constants.CONTACT_ID, curContact.id)
                    PendingIntent.getBroadcast(
                        appContext,
                        curContact.id.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartMoment.timeInMillis,
                if (isLeapYear(birthday)) LEAP_YEAR_MILLISECONDS else NORMAL_YEAR_MILLISECONDS,
                alarmPendingIntent
            )
            Log.d(TAG, "Аларм установлен на $alarmStartMoment")
            Toast.makeText(
                appContext,
                appContext.getString(R.string.set_alarm_msg),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun cancelBirthdayAlarm(curContact: Contact) {
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmPendingIntent = PendingIntent.getBroadcast(
            appContext,
            curContact.id.hashCode(),
            Intent(appContext, BirthdayAlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )
        alarmManager.cancel(alarmPendingIntent)
        alarmPendingIntent.cancel()
        Toast.makeText(
            appContext,
            appContext.getString(R.string.cancel_alarm_msg),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun isLeapYear(date: Calendar) = date[Calendar.YEAR] % LEAP_YEAR_PERIOD == 0

}