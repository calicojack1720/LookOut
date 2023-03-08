package com.example.lologin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.os.Build
import android.provider.Settings

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        Creates the Audio - ABD
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mediaPlayer.setAudioAttributes(attributes)

        mediaPlayer.setOnCompletionListener { player -> player.release() }
        mediaPlayer.start()

//        Message to the console

        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        println("Alarm triggered: $message")

        //TODO: Need to display notification to User on AlarmReceived
        val notificationManager = ContextCompat.getSystemService(context!!,
        NotificationManager::class.java
        ) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(context, "my_channel_id")
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle("Alarm Triggered: $message")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

//        Creating the Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val description = "Channel for Alarms App"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("my_channel_id", name, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)

        }

//        Show the notification
        notificationManager.notify(0, notificationBuilder.build())

    }
}