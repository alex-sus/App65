package ru.yodata.app65.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactDetailsBinding
import ru.yodata.app65.model.Contact
import ru.yodata.app65.utils.service.OnContactLoaderServiceCallback
import ru.yodata.app65.utils.Constants
import ru.yodata.app65.utils.Constants.SHOW_EMPTY_VALUE
import ru.yodata.app65.utils.Constants.TAG
import ru.yodata.app65.utils.alarmbroadcast.BirthdayAlarmReceiver
import java.util.*

private const val DAY_OF_MONTH_29 = 29

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {
    private var detailsFrag: FragmentContactDetailsBinding? = null

    private val alarmHelper = BirthdayAlarmManagerHelper()
    private var loaderCallback: OnContactLoaderServiceCallback? = null
    private lateinit var coroutineScope: CoroutineScope
    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }
    private val contResolver: ContentResolver by lazy { requireContext().contentResolver }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactLoaderServiceCallback) {
            loaderCallback = context
        } else throw ClassCastException(context.toString() +
                " must implement OnContactLoaderServiceCallback!")
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsFrag = FragmentContactDetailsBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.contact_details_fragment_title)
        loaderCallback?.run {
            coroutineScope.launch {
                while (!isServiceBound()) {}
                val curContact = getContactById(contResolver, contactId)
                try {
                    requireActivity().runOnUiThread {
                        showContactDetails(curContact)
                        detailsFrag?.remindBtn?.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) alarmHelper.setBirthdayAlarm(curContact)
                            else alarmHelper.cancelBirthdayAlarm(curContact)
                        }
                    }
                }
                catch (e: IllegalStateException) {
                    Log.d(TAG,"Исключение в ContactDetailsFragment: ")
                    Log.d(TAG, e.stackTraceToString())
                }
            }
        }
    }

    private fun showContactDetails(curContact: Contact) {
        with(curContact) {
            detailsFrag?.apply {
                fullNameTv.text = name
                birthdayTv.text = if (birthday != null)
                    birthday.get(Calendar.DAY_OF_MONTH).toString() + " " +
                    birthday.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                else SHOW_EMPTY_VALUE
                phone1Tv.text = phone1
                phone2Tv.text = phone2
                email1Tv.text = email1
                email2Tv.text = email2
                descriptionTv.text = description
                remindBtn.isChecked = if (birthday != null) {
                    alarmHelper.isBirthdayAlarmOn(curContact)
                }
                else false
                remindBtn.visibility = if (birthday != null) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        detailsFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.d(Constants.TAG,"Старт метода: ${this::class.java.simpleName}:" +
                "${object {}.javaClass.getEnclosingMethod().getName()}")
        loaderCallback = null
        // Убить все долгие работы по загрузке данных, даже если они еще продолжаются
        coroutineScope.cancel()
        super.onDetach()
    }

    companion object {

        private const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = ContactDetailsFragment::class.java.name
        @JvmStatic
        fun newInstance(contactId: String) =
            ContactDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }

    private inner class BirthdayAlarmManagerHelper {

        fun isBirthdayAlarmOn(curContact: Contact) = PendingIntent.getBroadcast(
                    context,
                    curContact.id.hashCode(),
                    Intent(context, BirthdayAlarmReceiver::class.java),
                    PendingIntent.FLAG_NO_CREATE
            ) != null

        fun setBirthdayAlarm(curContact: Contact) {
            if (curContact.birthday != null) {
                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmPendingIntent = Intent(context, BirthdayAlarmReceiver::class.java)
                        .let { intent ->
                            intent.putExtra(Constants.BIRTHDAY_MESSAGE,
                                    context?.getString(R.string.birthday_msg) + curContact.name
                            )
                            intent.putExtra(Constants.CONTACT_ID, curContact.id)
                            PendingIntent.getBroadcast(
                                    context,
                                    curContact.id.hashCode(),
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        }
                val today = Calendar.getInstance()
                val curYear = today.get(Calendar.YEAR)
                val alarmStartMoment: Calendar
                val alarmPeriodMs: Long
                if (curContact.birthday.get(Calendar.DAY_OF_MONTH) == DAY_OF_MONTH_29
                        && curContact.birthday.get(Calendar.MONTH) == Calendar.FEBRUARY) {
                    alarmStartMoment = nextFebruary29()
                    alarmPeriodMs = 1000 * 60 * 60 * 24 * 366 // кол-во миллисекунд в високосном году
                } else {
                    alarmStartMoment = curContact.birthday.apply { set(Calendar.YEAR, curYear) }
                    if (alarmStartMoment.before(today))
                        alarmStartMoment.set(Calendar.YEAR, curYear + 1)
                    alarmPeriodMs = 1000 * 60 * 60 * 24 * 365 // кол-во миллисекунд в обычном году
                }
                alarmStartMoment.apply {// время срабатывания устанавливается из настроек приложения
                    with(Constants.alarmStartTime) {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, second)
                    }
                }
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmStartMoment.timeInMillis,
                        alarmPeriodMs,
                        alarmPendingIntent
                )
                Log.d(Constants.TAG, "Аларм установлен на ${alarmStartMoment.toString()}")
                Toast.makeText(
                        context,
                        getString(R.string.set_alarm_msg),
                        Toast.LENGTH_LONG
                ).show()
            }
        }

        private fun nextFebruary29(): Calendar {
            // вычисляет, когда будет следующее 29 февраля после сегодняшней даты
            val result: Calendar
            val today = Calendar.getInstance()
            val curYear = today.get(Calendar.YEAR)
            val remainder = curYear % 4 // високосные годы делятся на 4 без остатка
            if (remainder == 0) { // если текущий год високосный
                result = today.apply {
                    set(Calendar.DAY_OF_MONTH, DAY_OF_MONTH_29)
                    set(Calendar.MONTH, Calendar.FEBRUARY)
                    set(Calendar.YEAR, curYear)
                }
                // если 29 февраля в этом году, но уже прошло, перенести его на 4 года
                if (result.before(today)) result.set(Calendar.YEAR, curYear + 4)
            } else result = today.apply {
                set(Calendar.DAY_OF_MONTH, DAY_OF_MONTH_29)
                set(Calendar.MONTH, Calendar.FEBRUARY)
                set(Calendar.YEAR, curYear + (4 - remainder))
            }
            return result
        }

        fun cancelBirthdayAlarm(curContact: Contact) {
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmPendingIntent = PendingIntent.getBroadcast(
                    context,
                    curContact.id.hashCode(),
                    Intent(context, BirthdayAlarmReceiver::class.java),
                    PendingIntent.FLAG_NO_CREATE
            )
            alarmManager.cancel(alarmPendingIntent)
            alarmPendingIntent.cancel()
            Toast.makeText(
                    context,
                    getString(R.string.cancel_alarm_msg),
                    Toast.LENGTH_LONG
            ).show()
        }
    }
}


