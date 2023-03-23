package com.example.lologin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button

class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        //Create val for timer_item.xml
        val timerView = layoutInflater.inflate(R.layout.activity_timers, null)

        val inputTimerHours = timerView.findViewById<EditText>(R.id.TimerHours)     //hours input
        val inputTimerMinutes = timerView.findViewById<EditText>(R.id.TimerMinutes) //minutes input
        val inputTimerSeconds = timerView.findViewById<EditText>(R.id.TimerSeconds) //seconds input

        val startTimerButton = timerView.findViewById<Button>(R.id.StartTimer)  //start timer button
    }
}