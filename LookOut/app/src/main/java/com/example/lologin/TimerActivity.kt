/* TimerActivity.kt
   Initiates the timers page and handles setting, starting, stopping, and creating and using Tiemrs.
   Created by Michael Astfalk
   Created: 3/17/2023
   Updated: 3/25/2023
 */


package com.example.lologin

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.NonCancellable.message
import java.io.File
import java.time.LocalDateTime


private lateinit var auth: FirebaseAuth
class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        val scheduler = AndroidTimerScheduler(this)
        var timerItem: TimerItem? = null

        //initializing Firebase
        auth = Firebase.auth

        //create timer storage
        createTimerStorage()


        //Create val for timer_item.xml
        val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)

        val inputTimerHours = activityTimerLayout.findViewById<EditText>(R.id.TimerHours)     //hours input
        val inputTimerMinutes = activityTimerLayout.findViewById<EditText>(R.id.TimerMinutes) //minutes input
        val inputTimerSeconds = activityTimerLayout.findViewById<EditText>(R.id.TimerSeconds) //seconds input

        //val startTimerButton = activityTimerLayout.findViewById<Button>(R.id.StartTimer)  //start timer button

        val addTimerButton = activityTimerLayout.findViewById<FloatingActionButton>(R.id.addTimer)



        //call TimerDisplay to format entered time
        //TimerDisplay(timerView, timerHours, timerMinutes, timerSeconds)

        //create a value for the start button
        val startTimer = activityTimerLayout.findViewById<Button>(R.id.StartTimer)


        //if startTimer button is pressed, start the countdown
        startTimer.setOnClickListener {
            //change timer text
            startTimer.text = "Stop"
            Log.w(TAG, "Text 'changed'")
            //create values to hold input time
            val timerHours = inputTimerHours.text.toString().toIntOrNull()
            val timerMinutes = inputTimerMinutes.text.toString().toIntOrNull()
            val timerSeconds = inputTimerSeconds.text.toString().toIntOrNull()

            val convertedSeconds: Long = convertToSec(timerHours, timerMinutes, timerSeconds)

            if(timerSeconds != null) {
                timerItem = TimerItem(
                    time = LocalDateTime.now()
                        .plusSeconds(convertedSeconds.toLong()),
                    message = ""
                )
            }

            timerItem?.let(scheduler::schedule)

            //start countdown
            /*val timer = object: CountDownTimer(timerMilliSeconds, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(applicationContext, notification)
                    r.play()
                    Log.w(TAG, "onFinish ran")

                }
            }
            Log.w(TAG, "Timer.start")
            timer.start()
            */
        }

        //TODO: This isn't doing anything
        //change timer text back to "Start"
        startTimer.text = "Start"

        //When addTimerButton is pressed, open up the timer popup
        addTimerButton.setOnClickListener {
            showTimerPopup()
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

    /* Precondition: tHours, tMinutes, and tSeconds are all of type Int?
       Postcondition: returns hours:minutes:seconds as seconds
     */
    private fun convertToSec(tHours: Int?, tMinutes: Int?, tSeconds: Int?): Long {
        var toSec: Long = 0      //value for total milliseconds of time

        //convert seconds to milliseconds
        if(tSeconds != null && tSeconds !=0)
            toSec += (tSeconds)
        //convert minutes to milliseconds
        if(tMinutes != null && tMinutes != 0)
            toSec += (tMinutes * 60)
        //convert hours to milliseconds
        if(tHours != null && tHours != 0)
            toSec += (tHours * 60 * 60)

        //return time in milliseconds
        return toSec
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

    /* Preconditon: implicit timer_popup_window.xml file
       Postoconditon: runs the create timer popup window to add a preset timer
     */
    private fun showTimerPopup() {
        //create values for buttons
        val timerPopupView = layoutInflater.inflate(R.layout.timer_popup_window, null)  //the popup
        val cancelButton = timerPopupView.findViewById<Button>(R.id.cancel_button)      //cancel button
        val addButton = timerPopupView.findViewById<Button>(R.id.submitbutton)          //add button

        val popupWindow = PopupWindow(
            timerPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        //show popup view
        popupWindow.showAtLocation(timerPopupView, Gravity.CENTER, 0, 0)

        //if cancel button is pressed, close popup
        cancelButton.setOnClickListener {
            popupWindow.dismiss()
        }

        //if add button is pressed, close popup and create saved timer
        addButton.setOnClickListener {
            val timerName = timerPopupView.findViewById<EditText>(R.id.name_text_box)

            val presetName = timerName.text.toString()

        }
    }

    private fun saveTimer(hours: Int?, minutes: Int?, seconds: Int?, name: String, timerIndex: Int) {
        val timerPreferences: SharedPreferences = getSharedPreferences("timerStorage", Context.MODE_PRIVATE)
    }

    companion object {
        const val TAG = "TimerActivity"
    }
}