package com.example.batteryreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val message = intent?.getStringExtra("reminderMessage") ?: "Reminder"
        val number = intent?.getStringExtra("phoneNumber")

        if (!number.isNullOrEmpty()){
            SmsManager.getDefault().sendTextMessage(number, null, message, null, null)
        }

     //   val phoneNumber = "+918059005454"
        val phoneNumber = "+919467157575"

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("ReminderReceiver", "SMS sent to $phoneNumber with message: $message")
        } catch (e: Exception) {
            Log.e("ReminderReceiver", "Error sending SMS: ${e.message}")
        }
        Log.d("ReminderReceiver", "Reminder received with message: $message")

        val channelId = "reminder_channel"
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Reminder")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}