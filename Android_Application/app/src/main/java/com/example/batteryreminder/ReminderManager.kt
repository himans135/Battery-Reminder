package com.example.batteryreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.util.Calendar

object ReminderManager {
    fun  scheduleReminder(context: Context, calendar: android.icu.util.Calendar, message: String, phoneNumber: String? = null) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    Log.w("ReminderManager", "Requested exact alarm permission")
                    return
                }
            }

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("reminderMessage", message)
                if (phoneNumber != null) {
                    putExtra("phoneNumber", phoneNumber)
                }
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                calendar.timeInMillis.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        } catch (e: SecurityException) {
            Log.e("ReminderManager", "Permission denied: ${e.message}")
        } catch (e: Exception) {
            Log.e("ReminderManager", "Unexpected error: ${e.message}")
        }
    }
}
