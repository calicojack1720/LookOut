/* TimerActivity.kt
   Initiates the timers page and handles setting, starting, stopping, and creating and using Tiemrs.
   Created by Michael Astfalk
   Created: 3/17/2023
   Updated: 4/5/2023
 */


package com.example.lologin

import android.content.ContentValues.TAG
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
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.NonCancellable.message
import java.io.File
import java.time.LocalDateTime

var numTimer = -1

var countSeconds: Int = 0
var countMinutes: Int = 0
var countHours: Int = 0


private lateinit var auth: FirebaseAuth
class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        val scheduler = AndroidTimerScheduler(this)
        var timerItem: TimerItem? = null

        //determines whether count down should be continued
        var continueCountDown: Boolean = true

        //initializing Firebase
        auth = Firebase.auth

        //create timer storage
        createTimerStorage()


        //Create val for timer_item.xml
        val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)

        val inputTimerHours = activityTimerLayout.findViewById<EditText>(R.id.TimerHours)     //hours input
        val inputTimerMinutes = activityTimerLayout.findViewById<EditText>(R.id.TimerMinutes) //minutes input
        val inputTimerSeconds = activityTimerLayout.findViewById<EditText>(R.id.TimerSeconds) //seconds input

        val addTimerButton = activityTimerLayout.findViewById<FloatingActionButton>(R.id.addTimer)

        //call TimerDisplay to format entered time
        //TimerDisplay(timerView, timerHours, timerMinutes, timerSeconds)

        //create a value for the start button, stop button, and reset button
        val startTimer = activityTimerLayout.findViewById<Button>(R.id.StartTimer)
        val stopTimer = activityTimerLayout.findViewById<Button>(R.id.stopTimer)
        val resetTimer = activityTimerLayout.findViewById<Button>(R.id.resetTimer)

        //set startTimer to BLUE
        startTimer.setBackgroundColor(Color.BLUE)

        //set stop and reset to gray background
        stopTimer.setBackgroundColor(Color.DKGRAY)
        resetTimer.setBackgroundColor(Color.DKGRAY)


        //if startTimer button is pressed, start the countdown
        startTimer.setOnClickListener {
            //set continueCountDown to true
            continueCountDown = true

            //change startTimer background color
            startTimer.setBackgroundColor(Color.DKGRAY)
            Log.w(TAG, "Background Color 'changed'")

            //change stop and reset colors
            stopTimer.setBackgroundColor(Color.BLUE)
            resetTimer.setBackgroundColor(Color.BLUE)

            //create values to hold input time
            val timerHours = inputTimerHours.text.toString().toIntOrNull()
            val timerMinutes = inputTimerMinutes.text.toString().toIntOrNull()
            val timerSeconds = inputTimerSeconds.text.toString().toIntOrNull()

            val convertedSeconds: Long = convertToSec(timerHours, timerMinutes, timerSeconds)

            val milliseconds: Long = convertedSeconds * 1000

            //set countSeconds, countMinutes, and countHours
            if(timerSeconds != null)
                countSeconds = timerSeconds
            else
                countSeconds = 0
            if(timerMinutes != null)
                countMinutes = timerMinutes
            else
                countMinutes = 0
            if(timerHours != null)
                countHours = timerHours
            else
                countHours = 0

            //start timer count down diplay
            object : CountDownTimer(milliseconds, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    //check continueCountDown, finish if false
                    if(!continueCountDown)
                        cancel()

                    //update countHours, countMinutes, and countSeconds
                    getTimeLeft()
                    inputTimerHours.setText("$countHours")
                    inputTimerMinutes.setText("$countMinutes")
                    inputTimerSeconds.setText("$countSeconds")
                }

                override fun onFinish() {
                    startTimer.setBackgroundColor(Color.BLUE)

                    //reset countHours, countMinutes, countSeconds
                    countHours = 0
                    countMinutes = 0
                    countSeconds = 0

                    //reset enter tie boxes to empty
                    inputTimerHours.setText("")
                    inputTimerMinutes.setText("")
                    inputTimerSeconds.setText("")
                }
            }.start()


            if(timerSeconds != null || timerMinutes != null || timerHours != null) {
                timerItem = TimerItem(
                    time = LocalDateTime.now()
                        .plusSeconds(convertedSeconds.toLong()),
                    message = ""
                )
            }

            timerItem?.let(scheduler::schedule)

            //set listener for timer reset, stop timer when clicked
            resetTimer.setOnClickListener {
                //cancel timer
                timerItem?.let{scheduler.cancel(it)}

                //reset button colors
                resetTimer.setBackgroundColor(Color.DKGRAY)
                stopTimer.setBackgroundColor(Color.DKGRAY)
                startTimer.setBackgroundColor(Color.BLUE)

                //reset countSeconds, countMinutes, and countHours
                countSeconds = 0
                countMinutes = 0
                countHours = 0

                //reset timer display
                inputTimerHours.setText("")
                inputTimerMinutes.setText("")
                inputTimerSeconds.setText("")
            }

            //set listener for stop button, pause timer when clicked
            stopTimer.setOnClickListener {
                //cancel timer
                timerItem?.let{scheduler.cancel(it)}

                //set continueCountDown to false
                continueCountDown = false

                //set button colors
                stopTimer.setBackgroundColor(Color.DKGRAY)
                startTimer.setBackgroundColor(Color.BLUE)
                resetTimer.setBackgroundColor(Color.BLUE)
            }
        }

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

    /*Precondition: implicit update of countSeconds, countMinutes, and countHours
      Postcondition: subtracts one second from countSeconds, countMinutes, and countHours
    */
    private fun getTimeLeft() {
        if(countSeconds == 0) {
            if(countMinutes > 0) {
                countMinutes -= 1
                countSeconds = 59
            }
            else {
                if(countHours > 0) {
                    countHours -= 1
                    countMinutes = 59
                    countSeconds = 59
                }
            }
        }
        else {
            countSeconds -= 1
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
            //set name box, and hours:minutes:seconds boxes
            val timerName = timerPopupView.findViewById<EditText>(R.id.name_text_box)
            val popHours = timerPopupView.findViewById<EditText>(R.id.timer_pop_hours)
            val popMinutes = timerPopupView.findViewById<EditText>(R.id.timer_pop_minutes)
            val popSeconds = timerPopupView.findViewById<EditText>(R.id.timer_pop_seconds)

            //get values entered into timerName, popHours, popMinutes, popSeconds
            val presetName = timerName.text.toString()
            val presetHours = popHours.text.toString().toIntOrNull()
            val presetMinutes = popMinutes.text.toString().toIntOrNull()
            val presetSeconds = popSeconds.text.toString().toIntOrNull()


            val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val timerItemLayout =
                inflater.inflate(R.layout.timer_item, activityTimerLayout, false)

            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val maxChildViewX = screenWidth * 0.9f - timerItemLayout.width

            val x = screenWidth * 0.05f //5% from left
            val y = screenHeight * .13f //13% from top

            //Set the Parameters for the new Layout
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, // set width to wrap content
                ConstraintLayout.LayoutParams.MATCH_PARENT // set height to wrap content
            )
            Log.d(AlarmActivity.TAG, "Child count is ${activityTimerLayout.childCount}")

            val context: Context = this
            val parentRight = context.dpToPx(120)
            val parentLeft = context.dpToPx(25)
            val parentTop = context.dpToPx(100)
            val parentBottom = context.dpToPx(600)
            val marginIncrement = context.dpToPx(100)

            var timerItem: TimerItem?
            val convertedSeconds: Long = convertToSec(presetHours, presetMinutes, presetSeconds)
            val scheduler = AndroidTimerScheduler(this)

            timerItem = TimerItem(
                time = LocalDateTime.now()
                    .plusSeconds(convertedSeconds.toLong()),
                message = ""
            )

            var arrayIndex = 0

            //Vars for changing how alarms are saved!
            var alarmItemPositionY = timerItemLayout.y


            if (activityTimerLayout.childCount <= 3) {
                Log.d(TAG, "Child count is ${activityTimerLayout.childCount}")

                timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                timerItemLayout.y = y

                activityTimerLayout.addView(timerItemLayout)
                timerItem?.let(scheduler::schedule)


                when (params.bottomMargin) {
                    2100 -> arrayIndex = 0
                    1750 -> arrayIndex = 1
                    1400 -> arrayIndex = 2
                    1050 -> arrayIndex = 3
                    700 -> arrayIndex = 4
                    else -> { // Note the block
                        Log.d(TAG, "Brr ${activityTimerLayout.childCount}")
                    }
                }

                Log.d(TAG, "First ${activityTimerLayout.childCount} ${params.bottomMargin}")
                //passes through hours, minutes, name, and enabled state to saveAlarms
                saveTimer(presetHours, presetMinutes, presetSeconds, presetName, arrayIndex)
                numTimer += 1

            } else if (activityTimerLayout.childCount <= 7) {
                Log.d(TAG, "Child count is ${activityTimerLayout.childCount}")
//                    params.leftMargin = parentLeft
//                    params.rightMargin = parentRight
//                    params.topMargin = parentTop + ((activityAlarmLayout.childCount - 3) * marginIncrement)
//                    params.bottomMargin = parentBottom - ((activityAlarmLayout.childCount - 3) * marginIncrement)
//
//                    alarmItemLayout.layoutParams = params

                timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                timerItemLayout.y = y + ((activityTimerLayout.childCount - 3) * marginIncrement)

                activityTimerLayout.addView(timerItemLayout)
                timerItem?.let(scheduler::schedule)

                when (params.bottomMargin) {
                    2100 -> arrayIndex = 0
                    1750 -> arrayIndex = 1
                    1400 -> arrayIndex = 2
                    1050 -> arrayIndex = 3
                    700 -> arrayIndex = 4
                    else -> { // Note the block
                        Log.d(TAG, "Brr ${activityTimerLayout.childCount}")
                    }
                }

                Log.d(TAG, "${activityTimerLayout.childCount} ${params.bottomMargin}")

                saveTimer(presetHours, presetMinutes, presetSeconds, presetName, arrayIndex)
                numTimer += 1

            } else {
                Toast.makeText(
                    applicationContext,
                    "Maximum Timer Number has been reached.",
                    Toast.LENGTH_LONG
                ).show()
            }

            //close out popup window when finished
            popupWindow.dismiss()
        }
    }

    /* Precondition: none
       Postcondition: checks if timer storage already exists and creates a storage file if there is none
         */
    private fun createTimerStorage() {
        val timerStorage = File(this.filesDir, "timerStorage.txt")
        val timerStorageExists = timerStorage.exists()

        if (timerStorageExists) {
            Log.w(AlarmActivity.TAG, "Alarm Storage file exists")
        } else {
            //creates file if doesn't exists
            timerStorage.createNewFile()
            Log.w(AlarmActivity.TAG, "Alarm Storage file created")
        }
    }

    /* Precondition: hours is of type Int?, minutes is of type Int?, name is of type String, and
                     timerIndex is of type Int
       Postcondition: creates a saved timer using the input using SharedPreferences
     */
    private fun saveTimer(hours: Int?, minutes: Int?, seconds: Int?, name: String, timerIndex: Int) {
        val timerPreferences: SharedPreferences = getSharedPreferences("timerStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = timerPreferences.edit()

        editor.apply() {
            putString("TIMER_NAME_$timerIndex", name)
            putInt("HOURS_$timerIndex", hours ?: 0)
            putInt("MINUTES_$timerIndex", minutes ?: 0)
            putInt("SECONDS_$timerIndex", seconds ?: 0)
        }.apply()
        Log.d(TAG, "Saved Timer $timerIndex")
    }

    //Precondition: none
    //Postcondition: loads saved timers on timer page
    private fun loadTimers() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)

        val scheduler = AndroidAlarmScheduler(this)
        var timerItem: TimerItem? = null

        for (i in 0 until 5) {

            //Setting default values
            val savedName: String? = sharedPreferences.getString("TIMER_NAME_$i", null)
            val savedSeconds: Int? = sharedPreferences.getInt("SECONDS_$i", 0)
            val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)

            Log.d(TAG, "Timer: $i")
            Log.d(TAG, "Saved name: $savedName")
            Log.d(TAG, "Saved seconds: $savedSeconds")
            Log.d(TAG, "Saved hours: $savedHours")
            Log.d(TAG, "Saved minutes: $savedMinutes")

            if (savedName != null) {
                numTimer += 1
            }

        }
    }

    companion object {
        const val TAG = "TimerActivity"
    }

    fun Context.dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}