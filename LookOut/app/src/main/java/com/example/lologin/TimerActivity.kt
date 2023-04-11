/* TimerActivity.kt
   Initiates the timers page and handles setting, starting, stopping, and creating and using Tiemrs.
   Created by Michael Astfalk
   Edited by Matthew Alexander
   Created: 3/17/2023
   Updated: 4/10/2023
 */

package com.example.lologin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.HandlerCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//import kotlinx.coroutines.NonCancellable.message
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//global variables:
var numTimer = -1

var countSeconds: Int = 0       //holds count down seconds
var countMinutes: Int = 0       //holds count down minutes
var countHours: Int = 0         //holds count down hours
//determines whether count down should be continued
var continueCountDown: Boolean = true

private lateinit var auth: FirebaseAuth
class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timers)

        val scheduler = AndroidTimerScheduler(this)
        var timerItem: TimerItem? = null

        //initializing Firebase
        auth = Firebase.auth

        val logOutButton = findViewById<Button>(R.id.logout)
        //On Click of the logOutButton
        logOutButton.setOnClickListener {
            Firebase.auth.signOut()
            Log.d(TAG, "User Signed out")
            //when user signs out, change LoginSkipCheck to false
            writeLoginSkipCheck()

            //switch to Login Activity
            startActivity(Intent(this, LoginActivity::class.java))
        }

//        //create timer storage
//        createTimerStorage()

        //Creates a value to check if connected to the internet
        val connected = isInternetConnected(this)

        Log.d(TAG, "Sync: Connectivity Status - $connected")

        if (auth.currentUser != null && connected) {
            syncCloud()
            Log.d(TAG, "Sync: Difference Sync - Begin")
        }
        else {
            loadTimers()
            Log.d(TAG, "Sync: Difference Sync - Not Logged in or no Internet Connection")
        }

        //Create val for timer_item.xml
        val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)

        val inputTimerHours = activityTimerLayout.findViewById<EditText>(R.id.TimerHours)     //hours input
        val inputTimerMinutes = activityTimerLayout.findViewById<EditText>(R.id.TimerMinutes) //minutes input
        val inputTimerSeconds = activityTimerLayout.findViewById<EditText>(R.id.TimerSeconds) //seconds input

        val addTimerButton = activityTimerLayout.findViewById<FloatingActionButton>(R.id.addTimer)

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
            //create values to hold input time
            var timerHours = inputTimerHours.text.toString().toIntOrNull()
            var timerMinutes = inputTimerMinutes.text.toString().toIntOrNull()
            var timerSeconds = inputTimerSeconds.text.toString().toIntOrNull()

            //if time is null, set to 0
            if (timerHours == null)
                timerHours = 0
            if (timerMinutes == null)
                timerMinutes = 0
            if (timerSeconds == null)
                timerSeconds = 0

            var allZero = false
            if (timerHours == 0 && timerMinutes == 0 && timerSeconds == 0) {
                allZero = true
            }

            if (allZero == false) {
                if (timerHours in 0..99) {
                    if (timerMinutes in 0..59) {
                        if (timerSeconds in 0..59) {
                            //set continueCountDown to true
                            continueCountDown = true

                            //change startTimer background color
                            startTimer.setBackgroundColor(Color.DKGRAY)
                            Log.w(TAG, "Background Color 'changed'")

                            //change stop and reset colors
                            stopTimer.setBackgroundColor(Color.BLUE)
                            resetTimer.setBackgroundColor(Color.BLUE)

                            val convertedSeconds: Long =
                                convertToSec(timerHours, timerMinutes, timerSeconds)

                            val milliseconds: Long = convertedSeconds * 1000

                            //set countSeconds, countMinutes, and countHours
                            if (timerSeconds != null)
                                countSeconds = timerSeconds
                            else
                                countSeconds = 0
                            if (timerMinutes != null)
                                countMinutes = timerMinutes
                            else
                                countMinutes = 0
                            if (timerHours != null)
                                countHours = timerHours
                            else
                                countHours = 0

                            //start timer count down display
                            object : CountDownTimer(milliseconds, 1000) {

                                override fun onTick(millisUntilFinished: Long) {
                                    //check continueCountDown, finish if false
                                    if (!continueCountDown) {
                                        cancel()
                                    } else {
                                        //update countHours, countMinutes, and countSeconds
                                        getTimeLeft()
                                        inputTimerHours.setText("$countHours")
                                        inputTimerMinutes.setText("$countMinutes")
                                        inputTimerSeconds.setText("$countSeconds")
                                    }
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


                            if (timerSeconds != null || timerMinutes != null || timerHours != null) {
                                timerItem = TimerItem(
                                    time = LocalDateTime.now()
                                        .plusSeconds(convertedSeconds.toLong()),
                                    message = ""
                                )
                            }

                            timerItem?.let(scheduler::schedule)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Enter a Seconds value between 0-59",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Enter a Minutes value between 0-59",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Enter an Hours value between 0-99",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Invalid Time Value",
                    Toast.LENGTH_LONG
                ).show()
            }

            //set listener for timer reset, stop timer when clicked
            resetTimer.setOnClickListener {
                //cancel timer
                timerItem?.let { scheduler.cancel(it) }

                //cancel the countdown
                continueCountDown = false

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
                timerItem?.let { scheduler.cancel(it) }

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

//        //Navigation bar
//        val navigationBar = findViewById<TabLayout>(R.id.navigation_bar)
//
//        //set selected tab to the Timer tab
//        navigationBar.selectTab(navigationBar.getTabAt(1))
//
//        //set listener for tab selection
//        navigationBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                when (tab.position) {
//                    //Sends the user back to the Alarms page when clicking on the alarms button. It has an issue I need to look into.
//                    0 -> startActivity(Intent(this@TimerActivity, AlarmActivity::class.java))
//                }
//            }
//
//            //things we want to run when tab is reselected/unselected
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                //Handle tab unselection
//                numTimer = -1
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                // Handle tab reselection
//
//            }
//        })
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
        //check continueCountDown, return if countinueCountDown is false
        if(!continueCountDown)
            return

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
        val cancelButton =
            timerPopupView.findViewById<Button>(R.id.cancel_button)      //cancel button
        val submitButton =
            timerPopupView.findViewById<Button>(R.id.submitbutton)          //add button

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
        submitButton.setOnClickListener {
            //set name box, and hours:minutes:seconds boxes
            val timerName = timerPopupView.findViewById<EditText>(R.id.name_text_box)
            val popHours = timerPopupView.findViewById<EditText>(R.id.timer_pop_hours)
            val popMinutes = timerPopupView.findViewById<EditText>(R.id.timer_pop_minutes)
            val popSeconds = timerPopupView.findViewById<EditText>(R.id.timer_pop_seconds)

            //get values entered into timerName, popHours, popMinutes, popSeconds
            val presetName = timerName.text.toString()
            var presetHours = popHours.text.toString().toIntOrNull()
            var presetMinutes = popMinutes.text.toString().toIntOrNull()
            var presetSeconds = popSeconds.text.toString().toIntOrNull()

            //if time is null, set to 0
            if (presetHours == null)
                presetHours = 0
            if (presetMinutes == null)
                presetMinutes = 0
            if (presetSeconds == null)
                presetSeconds = 0

            var allZero = false

            if (presetHours == 0 && presetMinutes == 0 && presetSeconds == 0) {
                allZero = true
                Log.d(TAG, "preset: allZero $allZero")
            }
            Log.d(TAG, "preset: $presetHours $presetMinutes $presetSeconds")

            if (!allZero) {
                if (presetHours in 0..99) {
                    if (presetMinutes in 0..59) {
                        if (presetSeconds in 0..59) {

                            //inflate the layout file
                            val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)
                            val inflater =
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val timerItemLayout =
                                inflater.inflate(R.layout.timer_item, activityTimerLayout, false)

                            //User input of timer into layout
                            val timeTextView =
                                timerItemLayout.findViewById<TextView>(R.id.existing_timer_time)

                            //set timeTextView text
                            timeTextView.text = "$presetHours:$presetMinutes:$presetSeconds"

                            //User input of name into layout
                            val nameTextView =
                                timerItemLayout.findViewById<TextView>(R.id.existing_timer_name)

                            //set nameTextView text
                            nameTextView.text = "$presetName"

                            //set values for screen width, height, and the max child view
                            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                            val maxChildViewX = screenWidth * 0.9f - timerItemLayout.width

                            val x = screenWidth * 0.05f //5% from left
                            val y = screenHeight * .40f //40% from top
                            val yIncrement = screenHeight * .13f //13% down the screen

                            var timerItem: TimerItem?
                            val convertedSeconds: Long =
                                convertToSec(presetHours, presetMinutes, presetSeconds)

                            var arrayIndex = 0
                            var heightIndexes = arrayOf(0.0, 0.0, 0.0)

                            //Vars for changing how alarms are saved!
                            var timerItemPositionY = timerItemLayout.y


                            if (activityTimerLayout.childCount <= 11) {
                                Log.d(TAG, "Child count is ${activityTimerLayout.childCount} <= 11")

                                timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                                timerItemLayout.y = y

                                activityTimerLayout.addView(timerItemLayout)

                                heightIndexes = populateHeightArray(timerItemLayout)
                                Log.d(TAG, "Index: $arrayIndex")

                                //GetIndex for save timers
                                arrayIndex =
                                    getIndex(
                                        timerItemLayout,
                                        heightIndexes,
                                        timerItemLayout.y.toDouble()
                                    )

                                //passes through hours, minutes, name, and enabled state to saveAlarms
                                saveTimer(
                                    presetHours,
                                    presetMinutes,
                                    presetSeconds,
                                    presetName,
                                    arrayIndex
                                )
                                numTimer += 1

                            } else if (activityTimerLayout.childCount <= 13) {
                                Log.d(TAG, "Child count is ${activityTimerLayout.childCount} <= 13")

                                timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                                timerItemLayout.y =
                                    y + ((activityTimerLayout.childCount - 11) * yIncrement)

                                activityTimerLayout.addView(timerItemLayout)

                                heightIndexes = populateHeightArray(timerItemLayout)

                                //GetIndex for save timers
                                arrayIndex =
                                    getIndex(
                                        timerItemLayout,
                                        heightIndexes,
                                        timerItemLayout.y.toDouble()
                                    )
                                Log.d(TAG, "Index: $arrayIndex")

                                saveTimer(
                                    presetHours,
                                    presetMinutes,
                                    presetSeconds,
                                    presetName,
                                    arrayIndex
                                )
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

                            //set deletion button
                            val deletionButton =
                                timerItemLayout.findViewById<TextView>(R.id.deletion_button)
                            //On Click of Delete Button

                            deletionButton.setOnClickListener {
                                heightIndexes = populateHeightArray(timerItemLayout)

                                //GetIndex for save timers
                                arrayIndex =
                                    getIndex(
                                        timerItemLayout,
                                        heightIndexes,
                                        timerItemLayout.y.toDouble()
                                    )

                                val parentView = timerItemLayout.parent as ViewGroup
                                parentView.removeView(timerItemLayout)

                                deleteTimer(arrayIndex)

                                //TODO: Need to update layout as items are deleted
                                //Update layout of remaining views

                                for (i in 11 until parentView.childCount) {
                                    val child = parentView.getChildAt(i)
                                    if (i == 11) {
                                        child.y = y
                                    } else {
                                        child.y = y + ((yIncrement * -1) * (i - 13))
                                    }

                                }


                            }

                            //set time button
                            val timeButton =
                                timerItemLayout.findViewById<TextView>(R.id.existing_timer_time)

                            //set listener for time button, update time on click
                            timeButton.setOnClickListener {
                                val inputPresetHours =
                                    activityTimerLayout.findViewById<EditText>(R.id.TimerHours)     //hours input
                                val inputPresetMinutes =
                                    activityTimerLayout.findViewById<EditText>(R.id.TimerMinutes) //minutes input
                                val inputPresetSeconds =
                                    activityTimerLayout.findViewById<EditText>(R.id.TimerSeconds) //seconds input

                                inputPresetHours.setText("$presetHours")
                                inputPresetMinutes.setText("$presetMinutes")
                                inputPresetSeconds.setText("$presetSeconds")
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Invalid values", Toast.LENGTH_LONG).show()
            }
        }
    }

    /* Precondition: none
       Postcondition: checks if timer storage already exists and creates a storage file if there is none
         */
//    private fun createTimerStorage() {
//        val timerStorage = File(this.filesDir, "timerStorage.txt")
//        val timerStorageExists = timerStorage.exists()
//
//        if (timerStorageExists) {
//            Log.w(TAG, "Timer Storage file exists")
//        } else {
//            //creates file if doesn't exists
//            timerStorage.createNewFile()
//            Log.w(TAG, "Timer Storage file created")
//        }
//    }

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

        if (auth.currentUser != null) {
            saveCloud(hours, minutes, name, seconds, timerIndex)
        }
    }

    //Precondition: none
    //Postcondition: loads saved timers on timer page
    private fun loadTimers() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("timerStorage", Context.MODE_PRIVATE)


        var timerItem: TimerItem? = null

        for (i in 0 until 4) {

            //Grabbing Local Values
            val savedName: String? = sharedPreferences.getString("TIMER_NAME_$i", null)
            val savedSeconds: Int? = sharedPreferences.getInt("SECONDS_$i", 0)
            val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)

            Log.d(TAG, "Load: Timer: $i")
            Log.d(TAG, "Load: Saved name: $savedName")
            Log.d(TAG, "Load: Saved seconds: $savedSeconds")
            Log.d(TAG, "Load: Saved hours: $savedHours")
            Log.d(TAG, "Load: Saved minutes: $savedMinutes")

            if (savedName != null) {
                numTimer += 1
                Log.d(TAG, "numTimer $numTimer")
            }

            //Everything above here works

            if (savedHours != null && savedHours in 0..99 && savedMinutes != null && savedMinutes in 0..59 && savedName != null && savedSeconds != null && savedSeconds in 0..59){
                val timeForAlarm = LocalTime.of(savedHours, savedMinutes)
                var dateTimeForAlarm = LocalDateTime.of(LocalDate.now(), timeForAlarm) //

                // Calculate the time difference between the current time and the time for the alarm //
                val currentTime = LocalDateTime.now()

                if (dateTimeForAlarm.isBefore(currentTime)) {
                    Log.d(TAG, "Duration is negative, adding 1 day for alarm")
                    dateTimeForAlarm = dateTimeForAlarm.plusDays(1)
                }

                //Default message to fix issues with AlarmItem
                val name = savedName ?: "Default message"

                //Inflate the Layout file
                val activityTimerLayout: ViewGroup =
                    findViewById(R.id.activity_timers) //Was ViewGroup
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val timerItemLayout =
                    inflater.inflate(R.layout.timer_item, activityTimerLayout, false)

                //UserInput of Timer into Layout
                var textViewString = ""
                val timeTextView = timerItemLayout.findViewById<TextView>(R.id.existing_timer_time)
                timeTextView.text = "$savedHours:$savedMinutes:$savedSeconds"

                //UserInput of AlarmName into Layout
                val nameTextView = timerItemLayout.findViewById<TextView>(R.id.existing_timer_name)
                textViewString = name
                nameTextView.text = textViewString

                //set values for screen width, height, and the max child view
                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val maxChildViewX = screenWidth * 0.9f - timerItemLayout.width

                val x = screenWidth * 0.05f //5% from left
                val y = screenHeight * .40f //45% from top
                val yIncrement = screenHeight * .13f //13% down the screen

                var arrayIndex = 0
                var heightIndexes = arrayOf(0.0, 0.0, 0.0)

                if (activityTimerLayout.childCount <= 11) {
                    Log.d(TAG, "Load: Child count is ${activityTimerLayout.childCount}")
                    timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    timerItemLayout.y = y

                    activityTimerLayout.addView(timerItemLayout)

                    heightIndexes = populateHeightArray(timerItemLayout)

                    //GetIndex for save timers
                    arrayIndex = getIndex(timerItemLayout, heightIndexes, timerItemLayout.y.toDouble())
                    Log.d(TAG, "Load: Index: $arrayIndex")

                    saveTimer(savedHours, savedMinutes, savedSeconds, savedName, arrayIndex)
                } else if (activityTimerLayout.childCount <= 13) {
                    Log.d(TAG, "Load: Child count is ${activityTimerLayout.childCount}")
                    timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    timerItemLayout.y = y + ((activityTimerLayout.childCount - 11) * yIncrement)

                    activityTimerLayout.addView(timerItemLayout)

                    heightIndexes = populateHeightArray(timerItemLayout)

                    //GetIndex for save timers
                    arrayIndex = getIndex(timerItemLayout, heightIndexes, timerItemLayout.y.toDouble())
                    Log.d(TAG, "Load: Index: $arrayIndex")

                    saveTimer(savedHours, savedMinutes, savedSeconds, savedName, arrayIndex)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Maximum Timer Number has been reached.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                //Deletion Button
                val deletionButton = timerItemLayout.findViewById<TextView>(R.id.deletion_button)

                //On Click of Delete Button
                deletionButton.setOnClickListener {
                    heightIndexes = populateHeightArray(timerItemLayout)

                    //GetIndex for save timers
                    arrayIndex = getIndex(timerItemLayout, heightIndexes, timerItemLayout.y.toDouble())

                    val parentView = timerItemLayout.parent as ViewGroup
                    parentView.removeView(timerItemLayout)

                    deleteTimer(arrayIndex)

                    //TODO: Need to update layout as items are deleted
                    //Update layout of remaining views

                    for (i in 11 until parentView.childCount) {
                        val child = parentView.getChildAt(i)
                        if (i == 11) {
                            child.y = y
                        }
                        else {
                            child.y = y + ((yIncrement * -1) * (i-13))
                        }

                    }


                }
                //set time button
                val timeButton = timerItemLayout.findViewById<TextView>(R.id.existing_timer_time)

                //set listener for time button, update time on click
                timeButton.setOnClickListener {
                    val inputPresetHours =
                        activityTimerLayout.findViewById<EditText>(R.id.TimerHours)     //hours input
                    val inputPresetMinutes =
                        activityTimerLayout.findViewById<EditText>(R.id.TimerMinutes) //minutes input
                    val inputPresetSeconds =
                        activityTimerLayout.findViewById<EditText>(R.id.TimerSeconds) //seconds input

                    inputPresetHours.setText("$savedHours")
                    inputPresetMinutes.setText("$savedMinutes")
                    inputPresetSeconds.setText("$savedSeconds")
                }
            }
        }
    }

    private fun saveCloud(hours: Int?, minutes: Int?, name: String, seconds: Int?, timerIndex: Int) {
        if (auth.currentUser != null) {
            val db = Firebase.firestore
            val token = getToken()

            Log.d(TAG, "Save: Current User Not Null")

            val timerData = hashMapOf(
                "name" to "$name",
                "hours" to hours,
                "minutes" to minutes,
                "seconds" to seconds,
            )
            db.collection("users/$token/timers").document("timer$timerIndex")
                .set(timerData, SetOptions.merge())
                .addOnSuccessListener { Log.d(TAG, "Successfully written to cloud!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing to cloud", e) }
        }
        else {
            Log.w(TAG, "SaveCloud: User is not logged in")
        }
    }

    private fun getToken(): String {
        val user = Firebase.auth.currentUser
        var uid = ""
        user?.let {
            // The user's ID, unique to the Firebase project.
            uid = it.uid
        }
        return uid
    }

    private fun deleteTimer(timerIndex: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("timerStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        //testing
        val savedName: String? = sharedPreferences.getString("TIMER_NAME_$timerIndex", null)
        val savedHours: Int? = sharedPreferences.getInt("HOURS_$timerIndex", 0)
        val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$timerIndex", 0)
        val savedSeconds: Int? = sharedPreferences.getInt("SECONDS_$timerIndex", 0)

        Log.d(TAG, "Removing Timer: $timerIndex")
        Log.d(TAG, "Deleting Saved name: $savedName")
        Log.d(TAG, "Deleting Saved hours: $savedHours")
        Log.d(TAG, "Deleting Saved minutes: $savedMinutes")
        Log.d(TAG, "Deleting Saved seconds: $savedSeconds")

        // Shift the remaining alarms down by one index
        for (i in (timerIndex + 1)..3) {
            Log.d(TAG, "$i")

            //val deletedIndexes = parentView.childAt(i)

            val newIndex = i - 1

            val tempName: String? = sharedPreferences.getString("TIMER_NAME_$i", null)
            val tempHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val tempMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
            val tempSeconds: Int? = sharedPreferences.getInt("SECONDS_$i", 0)

            Log.d(TAG, "Moving saved name: $tempName from $i to $newIndex")
            Log.d(TAG, "Moving saved hours: $tempHours from $i to $newIndex")
            Log.d(TAG, "Moving saved minutes: $tempMinutes from $i to $newIndex")
            Log.d(TAG, "Moving saved seconds: $tempSeconds from $i to $newIndex")

            if (tempName != null) {
                saveTimer(tempHours, tempMinutes, tempSeconds, tempName, newIndex)
            }

            Log.d(TAG, "Check for Last Index $i ")
        }

        editor.remove("TIMER_NAME_$numTimer")
        editor.remove("IS_ENABLED_$numTimer")
        editor.remove("HOURS_$numTimer")
        editor.remove("MINUTES_$numTimer")

        numTimer -= 1

        Log.d(TAG, "numTimer $numTimer")

        editor.apply()

        Log.d(TAG, "Deleted Timer $timerIndex")

        if (auth.currentUser != null) {
            Log.d(TAG, "Shift Cloud Called")
            shiftCloud()
        }

    }

    private fun shiftCloud() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("timerStorage", Context.MODE_PRIVATE)
        val db = Firebase.firestore
        val token = getToken()

        for (i in 0 until 3) {
            //Deletes each alarm
            db.collection("users/$token/timers").document("timer$i")
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Successfully shifted timers") }
                .addOnFailureListener { e -> Log.w(TAG, "Error shifting timers", e) }

            //Grabs values from Local Storage
            val savedName: String? = sharedPreferences.getString("TIMER_NAME_$i", null)
            val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
            val savedSeconds: Int? = sharedPreferences.getInt("SECONDS_$i", 0)

            //Saves new values from Local Storage to Cloud
            if (savedName != null) {
                saveCloud(savedHours, savedMinutes, savedName, savedSeconds, i)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun syncCloud() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("timerStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        var differenceFound = false

        Log.d(TAG, "Sync: Before GlobalScope")
        GlobalScope.launch(Dispatchers.Main) {
            Log.d(TAG, "Sync: In GlobalScope")
            try {
                Log.d(TAG, "Sync: Trying $numTimer")
                var hours = 0
                var minutes = 0
                var name = ""
                var seconds = 0

                //Creates a message that indicates Cloud Sync that lasts for 2.8s
                val handler = HandlerCompat.createAsync(mainLooper)
                val durationInMillis = 2800L // Custom duration in milliseconds
                val toast = Toast.makeText(applicationContext, "Syncing...", Toast.LENGTH_LONG)
                toast.show()
                handler.postDelayed({ toast.cancel() }, durationInMillis)

                loop@ for (i in 0 until 3) {
                    //Grabs data per index from the cloud
                    hours = suspendCoroutine<Int> { continuation ->
                        accessData(i, "hours") { cloudHours ->
                            if (cloudHours == "end") {
                                val breakValue = -1
                                continuation.resume(breakValue)
                            }
                            else {
                                continuation.resume(cloudHours.toInt())
                            }
                        }
                    }
                    //If no alarm is found, break the loop
                    if (hours == -1) {
                        Log.d(TAG, "Sync: No Cloud Timer Found, checking for Local Timer $i")
                        val savedName: String? = sharedPreferences.getString("TIMER_NAME_$i", null)
                        if (savedName != null) {
                            Log.d(TAG, "Sync: Local Timer Found when no Cloud Timer")
                            differenceFound = true
                        }
                        else {
                            Log.d(TAG, "Sync: No Local or Cloud Timer")
                        }
                        break@loop
                    }
                    minutes = suspendCoroutine<Int> { continuation ->
                        accessData(i, "minutes") { cloudMinutes ->
                            continuation.resume(cloudMinutes.toInt())
                        }
                    }
                    name = suspendCoroutine<String> { continuation ->
                        accessData(i, "name") { cloudName ->
                            continuation.resume(cloudName)
                        }
                    }
                    seconds = suspendCoroutine<Int> { continuation ->
                        accessData(i, "seconds") { cloudSeconds ->
                            continuation.resume(cloudSeconds.toInt())
                        }
                    }

                    //Grabs Local Storage per index
                    val savedName: String? = sharedPreferences.getString("TIMER_NAME_$i", null)
                    val savedSeconds: Int? = sharedPreferences.getInt("SECONDS_$i", 0)
                    val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
                    val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)

                    //check for differences between local and cloud storage
                    if (savedName == name && savedSeconds == seconds && savedHours == hours && savedMinutes == minutes) {
                        Log.d(TAG, "Sync: no differences")
                    }
                    else {
                        //If a difference is found, break the loop and rewrite Local Storage
                        Log.d(TAG, "Sync: Difference Found timer$i")
                        differenceFound = true
                        break@loop
                    }
                }

                if (differenceFound == true) {
                    //Asks the user whether they want to use Local or Cloud Storage
                    val builder = AlertDialog.Builder(this@TimerActivity)
                    builder.setTitle("Timers Out of Sync")
                    builder.setMessage("Choose to keep either Cloud or Locally saved Timers")

                    val completableDeferred = CompletableDeferred<Boolean>()

                    builder.setPositiveButton("Cloud") { dialog, which ->
                        completableDeferred.complete(true)
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Local") { dialog, which ->
                        completableDeferred.complete(false)
                        dialog.dismiss()
                        //return@setNegativeButton
                    }
                    builder.show()

                    //Suspend the code here and wait for the user's response
                    val useCloudStorage = completableDeferred.await()

                    //If keeping cloud data
                    if (useCloudStorage) {
                        val handler = HandlerCompat.createAsync(mainLooper)
                        val durationInMillis = 1500L // Custom duration in milliseconds
                        val toast = Toast.makeText(applicationContext, "Syncing from Cloud", Toast.LENGTH_LONG)
                        toast.show()
                        handler.postDelayed({ toast.cancel() }, durationInMillis)

                        //clears the local storage and rewrites it with cloud data
                        editor.clear()
                        editor.apply()
                        numAlarm = -1

                        diff@ for (i in 0 until 5) {
                            Log.d(TAG, "Sync: differenceFound - numTimer$numTimer - $i")
                            //Grabs data per index from the cloud
                            hours = suspendCoroutine<Int> { continuation ->
                                accessData(i, "hours") { cloudHours ->
                                    if (cloudHours == "end") {
                                        val breakValue = -1
                                        continuation.resume(breakValue)
                                    } else {
                                        continuation.resume(cloudHours.toInt())
                                    }
                                }
                            }
                            //If no alarm is found, break the loop
                            if (hours == -1) {
                                Log.d(TAG, "Sync: No Timer Found, Loop Broken $numTimer")
                                break@diff
                            }
                            minutes = suspendCoroutine<Int> { continuation ->
                                accessData(i, "minutes") { cloudMinutes ->
                                    continuation.resume(cloudMinutes.toInt())
                                }
                            }
                            name = suspendCoroutine<String> { continuation ->
                                accessData(i, "name") { cloudName ->
                                    continuation.resume(cloudName)
                                }
                            }
                            seconds = suspendCoroutine<Int> { continuation ->
                                accessData(i, "seconds") { cloudisPM ->
                                    continuation.resume(cloudisPM.toInt())
                                }
                            }
                            Log.d(TAG, "Sync: About to save: $hours $minutes $seconds $name $i")
                            saveTimer(hours, minutes, seconds, name, i)
                        }
                    }
                    else { //If keeping local data update cloud data
                        for (i in 0 until 3) {
                            //Grabs Local Storage per index
                            val savedName: String? = sharedPreferences.getString("TIMER_NAME_$i", null)
                            val savedSeconds: Int? = sharedPreferences.getInt("SECONDS_$i", 0)
                            val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
                            val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)

                            if (savedName != null) {
                                saveCloud(savedHours, savedMinutes, savedName, savedSeconds, i)
                            }
                        }
                    }

                }

                loadTimers()

            }
            catch (e: Exception) {
                // Handle any errors that occurred during the async operation
                Log.d(TAG, "Sync: Error, $e")
            }

        }
    }

    //Precondition: None
    //Postcondition: Writes to LoginSkipCheck
    private fun writeLoginSkipCheck() {
        val loginSkipCheck = File(this.filesDir, "loginSkipCheck.txt")
        val loginSkipCheckExists = loginSkipCheck.exists()

        if (loginSkipCheckExists) {
            Log.w(LoginActivity.TAG, "writeLoginSkipCheck exists")
            val inputStream: InputStream = loginSkipCheck.inputStream()
            val outputText = inputStream.bufferedReader().use {
                it.readText()
            }

            //writes to the file
            val outputStream: OutputStream = loginSkipCheck.outputStream()
            if (outputText == "true") {
                val inputText = "false"
                outputStream.write(inputText.toByteArray())
                outputStream.close()
                Log.d(LoginActivity.TAG, "Outputtext was True, now False.")
            } else if (!loginSkipCheckExists) { //For Redundancy and Debugging
                Log.d(LoginActivity.TAG, "writeLoginSkipCheck file doesn't exist")
            }

        }
    }

    private fun populateHeightArray(timerItemLayout: View): Array<Double> {
        //Creation of Arrays to be passed into loadAlarms
        var timerItemYIndexs = arrayOf(1.0, 2.0, 3.0)
        val parentView = timerItemLayout.parent as ViewGroup
        for (i in 11 until parentView.childCount) {
            val child = parentView.getChildAt(i)
            timerItemYIndexs[i-11] = child.y.toDouble()
            Log.d(TAG, "Height: Timer at index ${i - 11} has a height value of ${timerItemYIndexs[i-11]}")
        }
        return timerItemYIndexs
    }

    private fun getIndex (timerItemLayout: View, heightIndexes : Array<Double>, heightWanted: Double ): Int {
        val parentView = timerItemLayout.parent as ViewGroup
        Log.d(TAG, "Height: Searching for Index ${parentView.childCount} $heightWanted")

        for (arrayIndex in 0 until 3) {
            val child = parentView.getChildAt(arrayIndex+11)

            Log.d(TAG, "Height: ${heightIndexes[arrayIndex]} = $heightWanted")

            if (heightIndexes[arrayIndex] == heightWanted) {
                Log.d(TAG, "ArrayIndex saved properly!")
                return arrayIndex
            }
        }
        Log.d(TAG, "ERR: Height NOT FOUND ${parentView.childCount}")
        return -1 //-1 is returned, height now found

    }

    private fun accessData(timerIndex: Int, field: String, callback: (String) -> Unit) {
        val db = Firebase.firestore
        val token = getToken()

        val timerAccess = db.collection("users/$token/timers").document("timer$timerIndex")
        timerAccess.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fieldValue = document.get(field) // Get the field value based on the field name
                    Log.d(TAG, "Cloud: Timer $timerIndex - $field: $fieldValue")
                    callback(fieldValue.toString())
                } else {
                    Log.d(TAG, "Cloud: Failed to grab Timer $timerIndex")
                    callback("end")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                callback("end")
            }
    }

    private fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }

    companion object {
        const val TAG = "TimerActivity"
    }
}