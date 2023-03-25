/* TimerActivity.kt
   Initiates the timers page and handles setting, starting, stopping, and creating and using Tiemrs.
   Created by Michael Astfalk
   Created: 3/17/2023
   Updated: 3/24/2023
 */


package com.example.lologin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File


private lateinit var auth: FirebaseAuth
class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        //initializing Firebase
        auth = Firebase.auth

        //create timer storage
        createTimerStorage()


        //Create val for timer_item.xml
        val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)

        val inputTimerHours = activityTimerLayout.findViewById<EditText>(R.id.TimerHours)     //hours input
        val inputTimerMinutes = activityTimerLayout.findViewById<EditText>(R.id.TimerMinutes) //minutes input
        val inputTimerSeconds = activityTimerLayout.findViewById<EditText>(R.id.TimerSeconds) //seconds input

        val startTimerButton = activityTimerLayout.findViewById<Button>(R.id.StartTimer)  //start timer button

        //create values to hold input time
        val timerHours = inputTimerHours.text.toString().toIntOrNull()
        val timerMinutes = inputTimerMinutes.text.toString().toIntOrNull()
        val timerSeconds = inputTimerSeconds.text.toString().toIntOrNull()

        //call TimerDisplay to format entered time
        //TimerDisplay(timerView, timerHours, timerMinutes, timerSeconds)

        //create a value for the start button
        val startTimer = activityTimerLayout.findViewById<Button>(R.id.StartTimer)

        //if startTimer button is pressed, start the countdown
        startTimer.setOnClickListener {
            startTimer.text = "Stop"
        }
    }

    /*Precondition: tView is the view of the timer page
                    tHours is an integer value
                    tMinutes is an integer value
                    tSeconds is an integer value
      Postcondition: formats the time in hours, minutes and seconds with a 0 in front of any single
                     digits.
     */
    private fun TimerDisplay(tView: View, tHours: Int?, tMinutes: Int?, tSeconds: Int?) {
        //Checking for valid timer values
        if (tHours != null && tHours in 0..99 && tMinutes != null && tMinutes in 0..59 && tSeconds != null && tSeconds in 0..59) {
            //UserInput of Timer Time into Layout
            var textViewString = ""
            //TODO: not sure what existing_alarm_time is supposed to be
            val timeTextView = tView.findViewById<TextView>(R.id.existing_alarm_time)
            if ((tHours in 0..9) && (tMinutes > 9) && (tSeconds > 9)) {
                textViewString = "0$tHours:$tMinutes:$tSeconds"
                timeTextView.text = textViewString
            } else if ((tHours in 0..9) && (tMinutes in 0..9) && (tSeconds > 9)) {
                textViewString = "0$tHours:0$tMinutes:$tSeconds"
                timeTextView.text = textViewString
            } else if ((tHours in 0..9) && (tMinutes > 9) && (tSeconds in 0..9)) {
                textViewString = "0$tHours:$tMinutes:0$tSeconds"
                timeTextView.text = textViewString
            } else if ((tHours > 9) && (tMinutes in 0..9) && (tSeconds > 9)) {
                textViewString = "$tHours:0$tMinutes:$tSeconds"
                timeTextView.text = textViewString
            } else if ((tHours > 9) && (tMinutes in 0..9) && (tSeconds in 0..9)) {
                textViewString = "$tHours:0$tMinutes:$tSeconds"
                timeTextView.text = textViewString
            } else if ((tHours > 9) && (tMinutes > 9) && (tSeconds in 0..9)) {
                textViewString = "$tHours:$tMinutes:0$tSeconds"
                timeTextView.text = textViewString
            } else if ((tHours in 0..9) && (tMinutes in 0..9) && (tSeconds in 0..9)) {
                textViewString = "0$tHours:0$tMinutes:0$tSeconds"
                timeTextView.text = textViewString
            } else {
                textViewString = "$tHours:$tMinutes:$tSeconds"
                timeTextView.text = textViewString
            }
        }
    }

    /* Precondition: none
       Postcondition: checks if timer storage already exists and creates a storage file if there is none
     */
    private fun createTimerStorage() {
        val timerStorage = File(this.filesDir, "alarmStorage.txt")
        val timerStorageExists = timerStorage.exists()

        if (timerStorageExists) {
            Log.w(AlarmActivity.TAG, "Alarm Storage file exists")
        } else {
            //creates file if doesn't exists
            timerStorage.createNewFile()
            Log.w(AlarmActivity.TAG, "Alarm Storage file created")
        }
    }
}