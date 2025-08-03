package com.example.batteryreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.BatteryManager
import android.util.Log

class BatteryReceiver : BroadcastReceiver() {
    private var hasPlayed = false   // Track to avoid repeated alarm

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent == null || context == null) return

        val level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        Log.d("BatteryReceiver", "Level: $level, Status: $status")

        if(level == 100 && status == BatteryManager.BATTERY_STATUS_FULL && !hasPlayed){
//        if(level == 100 && status == BatteryManager.BATTERY_STATUS_FULL && !hasPlayed){
            try {
                Log.d("BatteryReceiver", "Battery is full. Playing alarm.")
                hasPlayed = true

                val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
                mediaPlayer?.start()

            }
            catch (e: Exception){
                Log.e("BatteryReceiver", "Error playing sound: ${e.message}")
            }
//            val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//            val r = RingtoneManager.getRingtone(context, ringtone)
//            r.play()
        }

        // Reset flag if level drops below 100
            if(level < 100) hasPlayed = false
    }

}