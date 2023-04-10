package com.example.lologin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import java.time.ZoneId
import android.content.Intent
import android.util.Log
import com.example.lologin.AlarmActivity.Companion.TAG
import java.util.*

class AndroidAlarmScheduler(
    private val context:Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem, daysOfWeek: List<Int>) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", item.message)
        }
        Log.w(TAG, "${item.time} is the current time being set")

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, item.time.hour)
            set(Calendar.MINUTE, item.time.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        calendar.timeZone = TimeZone.getDefault()

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Duration is negative, adding 1 day for alarm")
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }



        if (daysOfWeek.isEmpty()) {
            // Set the alarm to fire once on the specified date and time
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
//                item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    item.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            // Set the alarm to repeat on the specified days

            for (dayOfWeek in daysOfWeek) {
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                var alarmTime = calendar.timeInMillis
                if (alarmTime < System.currentTimeMillis()) {
                    // If the alarm time has already passed for this week, set it for next week instead
                    alarmTime += AlarmManager.INTERVAL_DAY * 7
                }
                else if (dayOfWeek == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                    //If today is one of the selected days and the alarm time has not yet passed, also schedule alarm for today
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        PendingIntent.getBroadcast(
                            context,
                            item.hashCode(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                }
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    AlarmManager.INTERVAL_DAY * 7,
                    PendingIntent.getBroadcast(
                        context,
                        item.hashCode() + dayOfWeek,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        )
    }

}