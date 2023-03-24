package com.example.lologin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private lateinit var auth: FirebaseAuth
class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        //initializing Firebase
        auth = Firebase.auth


        //Create val for timer_item.xml
        val timerView = layoutInflater.inflate(R.layout.activity_timers, null)

        val inputTimerHours = timerView.findViewById<EditText>(R.id.TimerHours)     //hours input
        val inputTimerMinutes = timerView.findViewById<EditText>(R.id.TimerMinutes) //minutes input
        val inputTimerSeconds = timerView.findViewById<EditText>(R.id.TimerSeconds) //seconds input

        val startTimerButton = timerView.findViewById<Button>(R.id.StartTimer)  //start timer button

        //create values to hold input time
        val timerHours = inputTimerHours.text.toString().toIntOrNull()
        val timerMinutes = inputTimerMinutes.text.toString().toIntOrNull()
        val timerSeconds = inputTimerSeconds.text.toString().toIntOrNull()

        //Checking for valid timer values
        if (timerHours != null && timerHours in 0..99 && timerMinutes != null && timerMinutes in 0..59 && timerSeconds != null && timerSeconds in 0..59) {
            //UserInput of Timer Time into Layout
            var textViewString = ""
            //TODO: not sure what existing_alarm_time is supposed to be
            val timeTextView = timerView.findViewById<TextView>(R.id.existing_alarm_time)
            if ((timerHours in 0..9) && (timerMinutes > 9) && (timerSeconds > 9)) {
                textViewString = "0$timerHours:$timerMinutes:$timerSeconds"
                timeTextView.text = textViewString
            }
            else if((timerHours in 0..9) && (timerMinutes in 0..9) && (timerSeconds > 9)) {
                textViewString = "0$timerHours:0$timerMinutes:$timerSeconds"
                timeTextView.text = textViewString
            }
            else if ((timerHours in 0..9) && (timerMinutes > 9) && (timerSeconds in 0..9)) {
                textViewString = "0$timerHours:$timerMinutes:0$timerSeconds"
                timeTextView.text = textViewString
            }
            else if ((timerHours > 9) && (timerMinutes in 0..9) && (timerSeconds > 9)) {
                textViewString = "$timerHours:0$timerMinutes:$timerSeconds"
                timeTextView.text = textViewString
            }
            else if ((timerHours > 9) && (timerMinutes in 0..9) && (timerSeconds in 0..9)) {
                textViewString = "$timerHours:0$timerMinutes:$timerSeconds"
                timeTextView.text = textViewString
            }
            else if ((timerHours > 9) && (timerMinutes > 9) && (timerSeconds in 0..9)) {
                textViewString = "$timerHours:$timerMinutes:0$timerSeconds"
                timeTextView.text = textViewString
            }
            else if ((timerHours in 0..9) && (timerMinutes in 0..9) && (timerSeconds in 0..9)) {
                textViewString = "0$timerHours:0$timerMinutes:0$timerSeconds"
                timeTextView.text = textViewString
            }
            else {
                textViewString = "$timerHours:$timerMinutes:$timerSeconds"
                timeTextView.text = textViewString
            }
        }
    }
}