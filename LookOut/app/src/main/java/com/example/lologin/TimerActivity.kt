/* TimerActivity.kt
   Initiates the timers page and handles setting, starting, stopping, and creating and using Tiemrs.
   Created by Michael Astfalk
   Created: 3/17/2023
   Updated: 4/8/2023
 */


package com.example.lologin

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
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.NonCancellable.message
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
            Log.d(AlarmActivity.TAG, "User Signed out")
            //when user signs out, change LoginSkipCheck to false
            writeLoginSkipCheck()

            //switch to Login Activity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        //create timer storage
        createTimerStorage()

        //load timers
        loadTimers()


        //Create val for timer_item.xml
        val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)
        Log.d(TAG, "Child count is ${activityTimerLayout.childCount} Start")
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

        //Navigation bar
        val navigationBar = findViewById<TabLayout>(R.id.navigation_bar)
        navigationBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    //Sends the user back to the Alarms page when clicking on the alarms button. It has an issue I need to look into.
                    //0 -> startActivity(Intent(this@AlarmActivity, AlarmActivity::class.java))
                    0 -> startActivity(Intent(this@TimerActivity, AlarmActivity::class.java))


                    //Here for TimerActivity page
                    1 -> startActivity(Intent(this@TimerActivity, TimerActivity::class.java))

                    // Add more cases for each tab as needed
                }
            }

            //things we want to run when tab is reselected/unselected
            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Handle tab unselection
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Handle tab reselection
            }
        })
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
        val cancelButton = timerPopupView.findViewById<Button>(R.id.cancel_button)      //cancel button
        val submitButton = timerPopupView.findViewById<Button>(R.id.submitbutton)          //add button

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
            if(presetHours == null)
                presetHours = 0
            if(presetMinutes == null)
                presetMinutes = 0
            if(presetSeconds == null)
                presetSeconds = 0

            //inflate the layout file
            val activityTimerLayout: ViewGroup = findViewById(R.id.activity_timers)
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val timerItemLayout =
                inflater.inflate(R.layout.timer_item, activityTimerLayout, false)

            //User input of timer into layout
            val timeTextView = timerItemLayout.findViewById<TextView>(R.id.existing_timer_time)

            //set timeTextView text
            timeTextView.text = "$presetHours:$presetMinutes:$presetSeconds"

            //User input of name into layout
            val nameTextView = timerItemLayout.findViewById<TextView>(R.id.existing_timer_name)

            //set nameTextView text
            nameTextView.text = "$presetName"

            //set values for screen width, height, and the max child view
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val maxChildViewX = screenWidth * 0.9f - timerItemLayout.width

            val x = screenWidth * 0.05f //5% from left
            val y = screenHeight * .45f //45% from top
            val yIncrement = screenHeight * .13f //13% down the screen


            //Set the Parameters for the new Layout
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, // set width to wrap content
                ConstraintLayout.LayoutParams.MATCH_PARENT // set height to wrap content
            )
            Log.d(AlarmActivity.TAG, "Child count is ${activityTimerLayout.childCount}")

            val context: Context = this
//            val parentRight = context.dpToPx(120)
//            val parentLeft = context.dpToPx(25)
//            val parentTop = context.dpToPx(100)
//            val parentBottom = context.dpToPx(600)
//            val marginIncrement = context.dpToPx(100)

            var timerItem: TimerItem?
            val convertedSeconds: Long = convertToSec(presetHours, presetMinutes, presetSeconds)

            var arrayIndex = 0
            var heightIndexes = arrayOf(0.0, 0.0, 0.0)

            //Vars for changing how alarms are saved!
            var timerItemPositionY = timerItemLayout.y


            if (activityTimerLayout.childCount <= 11) {
                Log.d(TAG, "Child count is ${activityTimerLayout.childCount} <= 10")

                timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                timerItemLayout.y = y

                activityTimerLayout.addView(timerItemLayout)

                heightIndexes = populateHeightArray(timerItemLayout)
                Log.d(TAG, "Index: $arrayIndex")

                //GetIndex for save timers
                arrayIndex = getIndex(timerItemLayout, heightIndexes, timerItemLayout.y.toDouble())

                //passes through hours, minutes, name, and enabled state to saveAlarms
                saveTimer(presetHours, presetMinutes, presetSeconds, presetName, arrayIndex)
                numTimer += 1

            } else if (activityTimerLayout.childCount <= 13) {
                Log.d(TAG, "Child count is ${activityTimerLayout.childCount} HAHAHAHAH")

                timerItemLayout.x = x.coerceIn(0f, maxChildViewX)
                timerItemLayout.y = y + ((activityTimerLayout.childCount - 11) * yIncrement)

                activityTimerLayout.addView(timerItemLayout)

                heightIndexes = populateHeightArray(timerItemLayout)

                //GetIndex for save timers
                arrayIndex = getIndex(timerItemLayout, heightIndexes, timerItemLayout.y.toDouble())
                Log.d(TAG, "Index: $arrayIndex")

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

            //set deletion button
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
                val inputPresetHours = activityTimerLayout.findViewById<EditText>(R.id.TimerHours)     //hours input
                val inputPresetMinutes = activityTimerLayout.findViewById<EditText>(R.id.TimerMinutes) //minutes input
                val inputPresetSeconds = activityTimerLayout.findViewById<EditText>(R.id.TimerSeconds) //seconds input

                inputPresetHours.setText("$presetHours")
                inputPresetMinutes.setText("$presetMinutes")
                inputPresetSeconds.setText("$presetSeconds")
            }
        }
    }

    /* Precondition: none
       Postcondition: checks if timer storage already exists and creates a storage file if there is none
         */
    private fun createTimerStorage() {
        val timerStorage = File(this.filesDir, "timerStorage.txt")
        val timerStorageExists = timerStorage.exists()

        if (timerStorageExists) {
            Log.w(AlarmActivity.TAG, "Timer Storage file exists")
        } else {
            //creates file if doesn't exists
            timerStorage.createNewFile()
            Log.w(AlarmActivity.TAG, "Timer Storage file created")
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

//        editor.clear()
//        editor.apply()
        Log.d(TAG, "Saved Timer $timerIndex")
    }

    //Precondition: none
    //Postcondition: loads saved timers on timer page
    private fun loadTimers() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("timerStorage", Context.MODE_PRIVATE)


        var timerItem: TimerItem? = null

        for (i in 0 until 4) {

            //Setting default values
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
                val y = screenHeight * .45f //45% from top
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
            }
        }
    }

    private fun saveCloud(hours: Int?, minutes: Int?, name: String, seconds: Int?, timerIndex: Int) {
        if (auth.currentUser != null) {
            val db = Firebase.firestore
            val token = getToken()

            Log.d(AlarmActivity.TAG, "Save: Current User Not Null")

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
            Log.w(AlarmActivity.TAG, "SaveCloud: User is not logged in")
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

        //editor.clear()

        editor.apply()

        Log.d(TAG, "Deleted Timer $timerIndex")
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
            Log.d(TAG, "Timer at index ${i - 11} has a height value of ${timerItemYIndexs[i-11]}")
        }
        return timerItemYIndexs
    }

    private fun getIndex (timerItemLayout: View, heightIndexes : Array<Double>, heightWanted: Double ): Int {

        val parentView = timerItemLayout.parent as ViewGroup

        for (arrayIndex in 0 until 3) {
            val child = parentView.getChildAt(arrayIndex+11)

            if (heightIndexes[arrayIndex] == heightWanted) {
                Log.d(TAG, "ArrayIndex saved properly!")
                return arrayIndex
            }
        }
        Log.d(TAG, "ERR: Height NOT FOUND")
        return -1 //-1 is returned, height now found

    }

    companion object {
        const val TAG = "TimerActivity"
    }

    fun Context.dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}