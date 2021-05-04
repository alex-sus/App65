package ru.yodata.library.utils.alarmbroadcast

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.yodata.library.R
import ru.yodata.library.utils.Constants.BIRTHDAY_MESSAGE
import ru.yodata.library.utils.Constants.CHANNEL_ID
import ru.yodata.library.utils.Constants.CONTACT_ID
import ru.yodata.library.utils.Constants.TAG
import ru.yodata.library.utils.Constants.notificationSound
import ru.yodata.library.view.MainActivity

class BirthdayAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundleData = intent.extras
        if (bundleData != null) {
            val contactId = bundleData.getString(CONTACT_ID)
            val notificationIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(CONTACT_ID, contactId)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val notificationPendingIntent: PendingIntent = PendingIntent.getActivity(
                    context,
                    contactId.hashCode(),
                    notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT
            )
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(bundleData.getString(BIRTHDAY_MESSAGE))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // показать на lockscreen
                    .setSound(notificationSound)
                    .setContentIntent(notificationPendingIntent)
                    .setAutoCancel(true)
            // Запустить отображение нотификации на экране
            NotificationManagerCompat.from(context).
            notify(TAG, contactId.hashCode(), notificationBuilder.build())

        }

        Toast.makeText(
            context,
            context.getString(R.string.notification_let_see_msg),
            Toast.LENGTH_LONG
        ).show()
    }
}