package com.example.lologin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

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

    }
}