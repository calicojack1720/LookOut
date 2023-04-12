package com.example.lologin

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import android.widget.*
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.HandlerCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.lologin.LoginActivity.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import java.util.concurrent.CompletableFuture
import org.w3c.dom.Text
import java.time.*
import java.util.*

var numAlarm = -1

private lateinit var auth: FirebaseAuth
//TODO Create check for choosing between keep local or cloud database when out of sync

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        //initialize Firebase
        auth = Firebase.auth

        val logOutButton = findViewById<Button>(R.id.logout)
        //On Click of the logOutButton
        logOutButton.setOnClickListener {
            Firebase.auth.signOut()
            Log.d(TAG, "User Signed out")
            //when user signs out, change LoginSkipCheck to false
            writeLoginSkipCheck()

            //switch to Login Activity
            numAlarm = -1
            startActivity(Intent(this, LoginActivity::class.java))
        }

//        //Creates Alarm Storage File
//        createAlarmStorage()

        Log.d(TAG, "OnCreate: $numAlarm")

        //Creates a value to check if connected to the internet
        val connected = isInternetConnected(this)

        Log.d(TAG, "Sync: Connectivity Status - $connected")

        //loadAlarms
        if (auth.currentUser != null && connected) {
            syncCloud()
            Log.d(TAG, "Sync: Difference Sync - Begin")
        }
        else {
            loadAlarms()
            Log.d(TAG, "Sync: Difference Sync - Not Logged in")
        }

        //Notifications
        val notificationManager = NotificationManagerCompat.from(this)

        if (!notificationManager.areNotificationsEnabled()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enable Push Notifications")
            builder.setMessage("This app requires push notifications to function properly")
            builder.setPositiveButton("Ok") { dialog, which ->
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, "com.example.lologin")
                }
                startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            val alertDialog = builder.show()
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                val okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                val cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                cancelButton?.setTextColor(Color.WHITE)
                okButton?.setTextColor(Color.WHITE)

            } else {
                val okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                val cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                cancelButton?.setTextColor(Color.BLACK)
                okButton?.setTextColor(Color.BLACK)
            }
        }

        //Navigation bar
        val navigationBar = findViewById<TabLayout>(R.id.navigation_bar)

        //set selected tab to the Timer tab
        navigationBar.selectTab(navigationBar.getTabAt(0))

        //set listener for tab selection
        navigationBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    //Sends the user back to the Alarms page when clicking on the alarms button. It has an issue I need to look into.
                    1 -> startActivity(Intent(this@AlarmActivity, TimerActivity::class.java))
                }
            }

            //things we want to run when tab is reselected/unselected
            override fun onTabUnselected(tab: TabLayout.Tab) {
                //Handle tab unselection
                numAlarm = -1
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Handle tab reselection

            }
        })

        //new
        val addAlarmButton = findViewById<FloatingActionButton>(R.id.addalarm)
        addAlarmButton.setOnClickListener { showPopup() }


    }

    //Writes to LoginSkipCheck
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

    private fun showPopup() {
        val popUpView = layoutInflater.inflate(R.layout.popup_window, null)

        val popupWindow = PopupWindow(
            popUpView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(popUpView, Gravity.CENTER, 0, 0)

        val cancelButton = popUpView.findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener { popupWindow.dismiss() }

        val submitButton = popUpView.findViewById<Button>(R.id.submitbutton)
//        new
        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null

        val inputHours = popUpView.findViewById<EditText>(R.id.hours)
        val inputMinutes = popUpView.findViewById<EditText>(R.id.minutes)
        var isPM = false

        //checks if ToggleAMPMButton is checked
        val toggleAMPM = popUpView.findViewById<ToggleButton>(R.id.toggleAMPM)
        toggleAMPM.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isPM = true
                Log.w(TAG, "PM")
            } else {
                isPM = false
                Log.w(TAG, "AM")
            }
        }

//        //Test Idea for Days Selector
//        val sundayTextView = popUpView.findViewById<TextView>(R.id.sunday_button)
//        val mondayTextView = popUpView.findViewById<TextView>(R.id.monday_button)
//        val tuesdayTextView = popUpView.findViewById<TextView>(R.id.tuesday_button)
//        val wednesdayTetView = popUpView.findViewById<TextView>(R.id.wednesday_button)
//        val thursdayTextView = popUpView.findViewById<TextView>(R.id.thursday_button)
//        val fridayTextView = popUpView.findViewById<TextView>(R.id.friday_button)
//        val saturdayTextView = popUpView.findViewById<TextView>(R.id.saturday_button)
//        val daysArray = arrayOf<TextView>(sundayTextView, mondayTextView, tuesdayTextView, wednesdayTetView, thursdayTextView, fridayTextView, saturdayTextView)
        var daysList = mutableListOf<Int>()


//        for (day in daysArray) {
//            Log.w(TAG, "Currently on day ${day.text}")
//            day.setOnClickListener {
//                daysList = daysCheck(day) }
//        }


        submitButton.setOnClickListener {

            val alarmName = popUpView.findViewById<EditText>(R.id.name_text_box)
            val name = alarmName.text.toString()
            var hours = inputHours.text.toString().toIntOrNull()
            val minutes = inputMinutes.text.toString().toIntOrNull()

            if (hours != null && hours in 1..12 && minutes != null && minutes in 0..59) {
                //AMPMCHECK
                Log.w(TAG, "Hours before time $hours")
                hours = amPmCheck(hours, isPM)
                Log.w(TAG, "Hours: $hours, isPM: $isPM")


                val timeForAlarm = LocalTime.of(hours, minutes)
                var dateTimeForAlarm = LocalDateTime.of(LocalDate.now(), timeForAlarm)




                alarmItem = AlarmItem(
                    time = dateTimeForAlarm,
                    message = name,
                    isEnabled = true
                )

                //Inflate the Layout file
                val activityAlarmLayout: ViewGroup = findViewById(R.id.activity_alarms) //Was ViewGroup
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val alarmItemLayout = inflater.inflate(R.layout.alarm_item, activityAlarmLayout, false)

                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val maxChildViewX = screenWidth * 0.9f - alarmItemLayout.width

                val x = screenWidth * 0.05f //5% from left
                val y = screenHeight * .13f //13% from top

//            UserInput of AlarmTime into Layout
                var textViewString = ""
                var displayHours = hours

                if (isPM) {
                    if (hours != 0 && hours!= 12) {
                        displayHours = hours - 12
                    }
                    else if (hours == 12) {
                        displayHours = hours
                    }
                }
                else {
                    if (hours == 0) {
                        displayHours = 12
                    }
                    else {
                        displayHours = hours
                    }
                }

                val timeTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_time)
                if ((hours in 0..9) && (minutes > 9)) {
                    textViewString = "$displayHours:$minutes"
                    timeTextView.text = textViewString
                }
                else if (hours > 9 && (minutes in 0..9)) {
                    textViewString = "$displayHours:0$minutes"
                    timeTextView.text = textViewString
                }
                else if ((hours in 0..9) && (minutes in 0..9)) {
                    textViewString = "$displayHours:0$minutes"
                    timeTextView.text = textViewString
                }
                else {
                    textViewString = "$displayHours:$minutes"
                    timeTextView.text = textViewString
                }
                //AMPM input
                val ampmTextView = alarmItemLayout.findViewById<TextView>(R.id.AMPM)

                if (isPM) {
                    ampmTextView.text = "PM"
                }
                else {
                    ampmTextView.text = "AM"
                }

//            UserInput of AlarmName into Layout
                val nameTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_name)
                textViewString = alarmName.text.toString()
                nameTextView.text = textViewString

//            Enable the toggle switch
                val toggleSwitch = alarmItemLayout.findViewById<Switch>(R.id.toggle_switch)
                toggleSwitch.isChecked = true
                toggleSwitch.isEnabled = true

                Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")

//                The following method displays the alarm right
                val context: Context = this

                var arrayIndex = 0
                var heightIndexes = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0)

                if (activityAlarmLayout.childCount <= 3) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")

                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y

                    activityAlarmLayout.addView(alarmItemLayout)
//                    alarmItem?.let(scheduler::schedule)
                    alarmItem.let { scheduler.schedule(alarmItem!!, daysList) }

                    heightIndexes = populateHeightArray(alarmItemLayout)

                    //GetIndex for save alarms
                    arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())

                    //passes through hours, minutes, name, and enabled state to saveAlarms
                    saveAlarms(hours, minutes, name, alarmItem!!.isEnabled, arrayIndex, isPM, daysList)
                    numAlarm += 1
                    daysList.clear()
                    Log.w(TAG, "List deleted")

                } else if (activityAlarmLayout.childCount <= 7) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")

                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y + ((activityAlarmLayout.childCount - 3) * y)

                    activityAlarmLayout.addView(alarmItemLayout)
//                    alarmItem?.let(scheduler::schedule)
                    alarmItem.let { scheduler.schedule(alarmItem!!, daysList) }

                    // populate height values for alarmItems, Creation of height indexes
                    heightIndexes = populateHeightArray(alarmItemLayout)

                    //GetIndex for save alarms
                    arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())

                    Log.w(TAG, "List to be added to saveAlarms at index $arrayIndex: $daysList")
                    saveAlarms(hours, minutes, name, alarmItem!!.isEnabled, arrayIndex, isPM, daysList)
                    numAlarm += 1
                    daysList.clear()

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Maximum Alarm Number has been reached.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                //Setting an on click listener to be able to edit alarms
                alarmItemLayout.setOnClickListener{editAlarms(alarmItemLayout, scheduler, alarmItem!!, heightIndexes)}

                //checks to see if Alarm is Enabled/Disabled
                toggleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) {
                        alarmItem?.let { scheduler.cancel(it) }

                        //GetIndex for save alarms
                        arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())

                        saveAlarms(hours, minutes, name, false, arrayIndex, isPM, daysList)
                        Log.d(TAG, "Alarm Cancelled")
                    } else {
//                        alarmItem?.let(scheduler::schedule)
                        alarmItem.let { scheduler.schedule(alarmItem!!, daysList) }
                        //GetIndex for save alarms
                        arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())

                        saveAlarms(hours, minutes, name, true, arrayIndex, isPM, daysList)
                        Log.d(TAG, "Alarm Enable")
                    }

                }
                //Deletion Button
                val deletionButton = alarmItemLayout.findViewById<TextView>(R.id.deletion_button)
                //On Click of Delete Button
                deletionButton.setOnClickListener {
                    arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())
                    val parentView = alarmItemLayout.parent as ViewGroup
                    parentView.removeView(alarmItemLayout)

                    alarmItem?.let { scheduler.cancel(it) }

                    deleteAlarms(arrayIndex, true)
//                    Update layout of remaining views

                    for (i in 3 until parentView.childCount) {
                        val child = parentView.getChildAt(i)
                        if (i == 3) {
                            child.y = y
                        }
                        else {
                            child.y = y * (i-2)
                        }
                    }
                }

                popupWindow.dismiss()

            }
            else {
                Toast.makeText(this, "Invalid time entered", Toast.LENGTH_SHORT).show()
            }
        }


    } // end of showPopup()

    private fun createAlarmStorage() {
        val alarmStorage = File(this.filesDir, "alarmStorage.txt")
        val alarmStorageExists = alarmStorage.exists()

        if (alarmStorageExists) {
            Log.w(TAG, "Alarm Storage file exists")
        } else {
            //creates file if doesn't exists
            alarmStorage.createNewFile()
            Log.w(TAG, "Alarm Storage file created")
        }
    }

    //Saves created alarms to using
    private fun saveAlarms(hours: Int?, minutes: Int?, name: String, isEnabled: Boolean, alarmIndex: Int, isPM: Boolean, daysList: MutableList<Int>) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val connected = isInternetConnected(this)

        val daysListSort: MutableList<Int> = daysList.sorted().toMutableList() // Create a mutable sorted copy of the list
        val daysListString = daysListSort.map { it.toString() }.toMutableList()


        editor.apply() {
            putString("ALARM_NAME_$alarmIndex", name)
            putBoolean("IS_ENABLED_$alarmIndex", isEnabled)
            putInt("HOURS_$alarmIndex", hours ?: 0)
            putInt("MINUTES_$alarmIndex", minutes ?: 0)
            putBoolean("IS_PM_$alarmIndex", isPM)
            putStringSet("DAYS_LIST_$alarmIndex", daysListString.toSet())

        }.apply()
        Log.d(TAG, "Saved Alarm $alarmIndex")

        if (auth.currentUser != null && connected) {
            saveCloud(hours, minutes, name, isEnabled, alarmIndex, isPM, daysListSort)
        }
    }

    private fun saveCloud(hours: Int?, minutes: Int?, name: String, isEnabled: Boolean, alarmIndex: Int, isPM: Boolean, daysList: MutableList<Int>) {
        val connected = isInternetConnected(this)
        if (auth.currentUser != null && connected) {
            val db = Firebase.firestore
            val token = getToken()

            Log.d(TAG, "Save: Current User Not Null")

            val alarmData = hashMapOf(
                "name" to "$name",
                "isPM" to isPM,
                "isEnabled" to isEnabled,
                "hours" to hours,
                "minutes" to minutes,
                "daysList" to daysList,
            )
            db.collection("users/$token/alarms").document("alarm$alarmIndex")
                .set(alarmData, SetOptions.merge())
                .addOnSuccessListener { Log.d(TAG, "Successfully written to cloud!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing to cloud", e) }
        }
        else {
            Log.w(TAG, "SaveCloud: User is not logged in")
        }
    }

    private fun loadAlarms() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)

        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null

        for (i in 0 until 5) {

            //Setting default values
            val savedName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
            val savedBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$i", false)
            val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
            val savedPM: Boolean = sharedPreferences.getBoolean("IS_PM_$i", false)
            val savedDaysList : MutableSet<String>? = sharedPreferences.getStringSet("DAYS_LIST_$i", null)
            val daysList : List<Int> = if (savedDaysList != null) {
                savedDaysList.map { it.toInt() }
            } else {
                emptyList()
            }
            Log.d(TAG, "Alarm: $i")
            Log.d(TAG, "Saved name: $savedName")
            Log.d(TAG, "Saved boolean: $savedBoolean")
            Log.d(TAG, "Saved hours: $savedHours")
            Log.d(TAG, "Saved minutes: $savedMinutes")
            Log.d(TAG, "Saved PM/AM State: $savedPM")
            Log.d(TAG, "Saved daysList: $daysList")

            if (savedName != null) {
                numAlarm += 1
                Log.d(TAG, "loadAlarms numAlarm:$numAlarm")
            }

            //Everything above here works

            if (savedHours != null && savedHours in 0..23 && savedMinutes != null && savedMinutes in 0..59 && savedName != null) {

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

                alarmItem = AlarmItem(
                    time = dateTimeForAlarm,
                    message = name,
                    isEnabled = savedBoolean
                )

                //Inflate the Layout files
                val activityAlarmLayout: ViewGroup = findViewById(R.id.activity_alarms) //Was ViewGroup

                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                val alarmItemLayout = inflater.inflate(R.layout.alarm_item, activityAlarmLayout, false)

                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val maxChildViewX = screenWidth * 0.9f - alarmItemLayout.width

                val x = screenWidth * 0.05f //5% from left
                val y = screenHeight * .13f //13% from top

//            UserInput of AlarmTime into Layout
                var displayHours = 0
                if (savedPM) {
                    if (savedHours != 0) {
                        displayHours = savedHours - 12
                    }
                    else {
                        displayHours = 12
                    }
                }
                else {
                    displayHours = savedHours
                }
                if (savedPM) {
                    if (savedHours != 0 && savedHours!= 12) {
                        displayHours = savedHours - 12
                    }
                    else if (savedHours == 12) {
                        displayHours = savedHours
                    }
                }
                else {
                    if (savedHours == 0) {
                        displayHours = 12
                    }
                    else {
                        displayHours = savedHours
                    }
                }

                var textViewString = ""
                val timeTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_time)
                if ((savedHours in 0..9) && (savedMinutes > 9)) {
                    textViewString = "$displayHours:$savedMinutes"
                    timeTextView.text = textViewString
                } else if ((savedHours > 9) && (savedMinutes in 0..9)) {
                    textViewString = "$displayHours:0$savedMinutes"
                    timeTextView.text = textViewString
                } else if ((savedHours in 0..9) && (savedMinutes in 0..9)) {
                    textViewString = "$displayHours:0$savedMinutes"
                    timeTextView.text = textViewString
                } else {
                    textViewString = "$displayHours:$savedMinutes"
                    timeTextView.text = textViewString
                }
                //AMPM input for display
                val ampmTextView = alarmItemLayout.findViewById<TextView>(R.id.AMPM)

                if (savedPM) {
                    ampmTextView.text = "PM"
                }
                else {
                    ampmTextView.text = "AM"
                }


//            UserInput of AlarmName into Layout
                val nameTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_name)
                textViewString = name
                nameTextView.text = textViewString

//            Enable the toggle switch
                val toggleSwitch = alarmItemLayout.findViewById<Switch>(R.id.toggle_switch)
                toggleSwitch.isChecked = savedBoolean
                toggleSwitch.isEnabled = true

                var arrayIndex = 0
                var heightIndexes = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0)

                if (activityAlarmLayout.childCount <= 3) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")

                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y

                    activityAlarmLayout.addView(alarmItemLayout)
                    if (toggleSwitch.isChecked && toggleSwitch.isEnabled) {
//                        alarmItem?.let(scheduler::schedule)
                        alarmItem.let { scheduler.schedule(alarmItem, daysList) }
                    }
                    heightIndexes = populateHeightArray(alarmItemLayout)

                } else if (activityAlarmLayout.childCount <= 7) {

                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y + ((activityAlarmLayout.childCount - 3) * y)

                    activityAlarmLayout.addView(alarmItemLayout)
                    if (toggleSwitch.isChecked && toggleSwitch.isEnabled) {
//                        alarmItem?.let(scheduler::schedule) //Fixed
                        alarmItem.let { scheduler.schedule(alarmItem, daysList) }
                    }
                    heightIndexes = populateHeightArray(alarmItemLayout)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Maximum Alarm Number has been reached.",
                        Toast.LENGTH_LONG
                    ).show()
                }
//                Setting an on click listener to be able to edit alarms
                alarmItemLayout.setOnClickListener{editAlarms(alarmItemLayout, scheduler, alarmItem!!, heightIndexes)}

                //checks to see if Alarm is Enabled/Disabled
                toggleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) {
                        alarmItem?.let { scheduler.cancel(it) }

                        //GetIndex for save alarms
                        arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())

                        saveAlarms(savedHours, savedMinutes, name, false, arrayIndex, savedPM, daysList.toMutableList())
                        Log.d(TAG, "Alarm Cancelled")
                    } else {
//                        alarmItem?.let(scheduler::schedule)
                        alarmItem.let { scheduler.schedule(alarmItem, daysList) }

                        //GetIndex for save alarms
                        arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())

                        saveAlarms(savedHours, savedMinutes, name, true, arrayIndex, savedPM, daysList.toMutableList())
                        Log.d(TAG, "Alarm Enable")
                    }

                }

                //Deletion Button
                val deletionButton = alarmItemLayout.findViewById<TextView>(R.id.deletion_button)
                //On Click of Delete Button
                deletionButton.setOnClickListener {
                    arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())
                    val parentView = alarmItemLayout.parent as ViewGroup
                    parentView.removeView(alarmItemLayout)

                    deleteAlarms(arrayIndex, true)

                    alarmItem?.let { scheduler.cancel(it) }

                    //Update layout of remaining views

                    for (i in 3 until parentView.childCount) {
                        val child = parentView.getChildAt(i)
                        if (i == 3) {
                            child.y = y
                        }
                        else {
                            child.y = y * (i-2)
                        }
                    }
//                    End of For Layout Adjustment
                }
            }

        }
    }

    //Checks all values in the cloud, and in the Local Storage, and outputs a TRUE value if they are all
    @SuppressLint("SuspiciousIndentation")
    private fun syncCloud() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        var differenceFound = false

        Log.d(TAG, "Sync: Before GlobalScope")
            GlobalScope.launch(Dispatchers.Main) {
                Log.d(TAG, "Sync: In GlobalScope")
                try {
                    Log.d(TAG, "Sync: Trying $numAlarm")
                    var hours = 0
                    var minutes = 0
                    var name = ""
                    var isPM = false
                    var isEnabled = false
                    var accessDaysList: List<Int> = mutableListOf()

                    //Creates a message that indicates Cloud Sync that lasts for 2.8s
                    val handler = HandlerCompat.createAsync(mainLooper)
                    val durationInMillis = 2800L // Custom duration in milliseconds
                    val toast = Toast.makeText(applicationContext, "Syncing...", Toast.LENGTH_LONG)
                    toast.show()
                    handler.postDelayed({ toast.cancel() }, durationInMillis)

                    loop@ for (i in 0 until 5) {
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
                            Log.d(TAG, "Sync: No Cloud Alarm Found, checking for Local Alarm")
                            val savedName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
                            if (savedName != null) {
                                Log.d(TAG, "Sync: Local Alarm Found when no Cloud Alarm")
                                differenceFound = true
                            }
                            else {
                                Log.d(TAG, "Sync: No Local or Cloud Alarm")
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
                        isPM = suspendCoroutine<Boolean> { continuation ->
                            accessData(i, "isPM") { cloudisPM ->
                                continuation.resume(cloudisPM.toBoolean())
                            }
                        }
                        isEnabled = suspendCoroutine<Boolean> { continuation ->
                            accessData(i, "isEnabled") { cloudisEnabled ->
                                continuation.resume(cloudisEnabled.toBoolean())
                            }
                        }
                        accessDaysList = accessDaysList(i)
                        var daysList = accessDaysList.toMutableList()

                        Log.d(TAG, "Sync: daysList $daysList")


                        //Grabs Local Storage per index
                        val savedName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
                        val savedBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$i", false)
                        val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
                        val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
                        val savedPM: Boolean = sharedPreferences.getBoolean("IS_PM_$i", false)
                        val savedList : MutableSet<String>? = sharedPreferences.getStringSet("DAYS_LIST_$i", null)
                        val savedDaysList : List<Int> = if (savedList != null) {
                            savedList.map { it.toInt() }
                        } else {
                            emptyList()
                        }

                        //check for differences between local and cloud storage
                        if (savedName == name && savedBoolean == isEnabled && savedHours == hours && savedMinutes == minutes && savedPM == isPM && savedDaysList.toMutableList() == daysList) {
                            Log.d(TAG, "Sync: no differences")
                        }
                        else {
                            //If a difference is found, break the loop and rewrite Local Storage
                            Log.d(TAG, "Sync: Difference Found alarm$i")
                            differenceFound = true
                            break@loop
                        }
                    }

                    if (differenceFound == true) {
                        //Asks the user whether they want to use Local or Cloud Storage
                        val builder = AlertDialog.Builder(this@AlarmActivity)
                        builder.setTitle("Alarms Out of Sync")
                        builder.setMessage("Choose to keep either Cloud or Locally saved Alarms")

                        val completableDeferred = CompletableDeferred<Boolean>()

                        builder.setPositiveButton("Cloud") { dialog, which ->
                            completableDeferred.complete(true)
                            dialog.dismiss()
                        }
                        builder.setNegativeButton("Local") { dialog, which ->
                            completableDeferred.complete(false)
                            dialog.dismiss()
                        }
                        val alertDialog = builder.show()
                        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                            val cloudButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            val localButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                            localButton?.setTextColor(Color.WHITE)
                            cloudButton?.setTextColor(Color.WHITE)

                        } else {
                            val cloudButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            val localButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                            localButton?.setTextColor(Color.BLACK)
                            cloudButton?.setTextColor(Color.BLACK)
                        }


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
                                Log.d(TAG, "Sync: differenceFound - numAlarm$numAlarm - $i")
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
                                    Log.d(TAG, "Sync: No Alarm Found, Loop Broken $numAlarm")
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
                                isPM = suspendCoroutine<Boolean> { continuation ->
                                    accessData(i, "isPM") { cloudisPM ->
                                        continuation.resume(cloudisPM.toBoolean())
                                    }
                                }
                                isEnabled = suspendCoroutine<Boolean> { continuation ->
                                    accessData(i, "isEnabled") { cloudisEnabled ->
                                        continuation.resume(cloudisEnabled.toBoolean())
                                    }
                                }
                                accessDaysList = accessDaysList(i)
                                var daysList = accessDaysList.toMutableList()

                                Log.d(TAG, "Sync: About to save: $hours $minutes $name $isEnabled $i $isPM $daysList")

                                saveAlarms(hours, minutes, name, isEnabled, i, isPM, daysList)
                            }
                        }
                        else { //If keeping local data update cloud data
                            for (i in 0 until 5) {
                                //Grabs Local Storage per index
                                val savedName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
                                val savedBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$i", false)
                                val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
                                val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
                                val savedPM: Boolean = sharedPreferences.getBoolean("IS_PM_$i", false)
                                val savedList : MutableSet<String>? = sharedPreferences.getStringSet("DAYS_LIST_$i", null)
                                val savedDaysList : List<Int> = if (savedList != null) {
                                    savedList.map { it.toInt() }
                                } else {
                                    emptyList()
                                }

                                if (savedName != null) {
                                    saveCloud(savedHours, savedMinutes, savedName, savedBoolean, i, savedPM, savedDaysList.toMutableList())
                                }
                            }
                        }

                    }

                    loadAlarms()

                }
                catch (e: Exception) {
                // Handle any errors that occurred during the async operation
                 Log.d(TAG, "Sync: Error, $e")
                }

            }
    }


    private fun shiftCloud() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)
        val db = Firebase.firestore
        val token = getToken()

        for (i in 0 until 5) {
            //Deletes each alarm
            db.collection("users/$token/alarms").document("alarm$i")
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Successfully shifted alarms") }
                .addOnFailureListener { e -> Log.w(TAG, "Error shifting alarms", e) }

            //Grabs values from Local Storage
            val savedName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
            val savedBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$i", false)
            val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
            val savedPM: Boolean = sharedPreferences.getBoolean("IS_PM_$i", false)
            val savedDaysList : MutableSet<String>? = sharedPreferences.getStringSet("DAYS_LIST_$i", null)
            val daysList : List<Int> = if (savedDaysList != null) {
                savedDaysList.map { it.toInt() }
            } else {
                emptyList()
            }

            //Saves new values from Local Storage to Cloud
            if (savedName != null) {
                saveCloud(savedHours, savedMinutes, savedName, savedBoolean, i, savedPM, daysList.toMutableList())
            }
        }
    }

    private fun editAlarms(alarmItemLayout : View, scheduler: AlarmScheduler, alarmItem: AlarmItem, heightIndexes: Array<Double>) {
        val popUpView = layoutInflater.inflate(R.layout.popup_window, null)

        val popupWindow = PopupWindow(
            popUpView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(popUpView, Gravity.CENTER, 0, 0)

        //AlarmItemLayout values
        val nameTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_name)
        val timeTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_time)
        val amPmTextView = alarmItemLayout.findViewById<TextView>(R.id.AMPM)

        //PopupViewValues
        val inputName = popUpView.findViewById<EditText>(R.id.name_text_box)
        val inputHours = popUpView.findViewById<EditText>(R.id.hours)
        val inputMinutes = popUpView.findViewById<EditText>(R.id.minutes)
        val cancelButton = popUpView.findViewById<Button>(R.id.cancel_button)

        var isPM = false
        val toggleAMPM = popUpView.findViewById<ToggleButton>(R.id.toggleAMPM)

        cancelButton.setOnClickListener {
            popupWindow.dismiss()
        }

        toggleAMPM.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isPM = true
                Log.w(TAG, "PM")
            } else {
                isPM = false
                Log.w(TAG, "AM")
            }
//            toggleAMPM.isChecked = isPM
        }
        //Need to check if statement again in case that CheckedChangeListener is not ran
        if (toggleAMPM.isChecked) {
            isPM = true
        }
        else {
            isPM = false
        }
        toggleAMPM.isChecked = isPM


        val submitButtom = popUpView.findViewById<Button>(R.id.submitbutton)

//        val sundayTextView = popUpView.findViewById<TextView>(R.id.sunday_button)
//        val mondayTextView = popUpView.findViewById<TextView>(R.id.monday_button)
//        val tuesdayTextView = popUpView.findViewById<TextView>(R.id.tuesday_button)
//        val wednesdayTetView = popUpView.findViewById<TextView>(R.id.wednesday_button)
//        val thursdayTextView = popUpView.findViewById<TextView>(R.id.thursday_button)
//        val fridayTextView = popUpView.findViewById<TextView>(R.id.friday_button)
//        val saturdayTextView = popUpView.findViewById<TextView>(R.id.saturday_button)
//        val daysArray = arrayOf<TextView>(sundayTextView, mondayTextView, tuesdayTextView, wednesdayTetView, thursdayTextView, fridayTextView, saturdayTextView)
        var daysList = mutableListOf<Int>()


//        for (day in daysArray) {
//            Log.w(TAG, "Currently on day ${day.text}")
//            day.setOnClickListener {
//                daysList = daysCheck(day) }
//        }

        submitButtom.setOnClickListener {
            var hours = inputHours.text.toString().toInt()
            var minutes = inputMinutes.text.toString().toIntOrNull()
            val name = inputName.text.toString()

            alarmItem?.let { scheduler.cancel(it) }

            var textViewString = ""

            if (hours != null && hours in 1..12 && minutes != null && minutes in 0..59) {
                hours = amPmCheck(hours, isPM)

                //Data for edited alarm
                val timeForAlarm = LocalTime.of(hours, minutes)
                var dateTimeForAlarm = LocalDateTime.of(LocalDate.now(), timeForAlarm)

                val currentTime = LocalDateTime.now()
                if (dateTimeForAlarm.isBefore(currentTime)) {
                    dateTimeForAlarm = dateTimeForAlarm.plusDays(1)
                }

                var newAlarmItem = AlarmItem( //May cause an issue, using new var instead of alarmItem
                    time = dateTimeForAlarm,
                    message = name,
                    isEnabled = true
                )
                //FIXME: Alarms do not schedule using newAlarmItem? Need to find way to just use alarmItem
                
                //Input of alarmName from popup into existing alarm
                nameTextView.text = inputName.text.toString()
                var displayHours = hours

                //Correcting value to 12 hour time
                if (isPM) {
                    if (hours != 0) {
                        displayHours = hours - 12
                        Log.w(TAG, "displayHours was edited to be $displayHours")
                    }
                    else {
                        displayHours = 12
                        Log.w(TAG, "displayHours was edited to be $displayHours")
                    }
                }
                else {
                    displayHours = hours
                    Log.w(TAG, "displayHours was edited to be $displayHours")
                }
                //Inputting the time into the alarmItem
                if ((hours in 0..9) && (minutes > 9)) {
                    textViewString = "$displayHours:$minutes"
                    timeTextView.text = textViewString
                    Log.w(TAG, "Alarm time has been changed to ${timeTextView.text}")
                }
                else if (hours > 9 && (minutes in 0..9)) {
                    textViewString = "${displayHours}:0$minutes"
                    timeTextView.text = textViewString
                    Log.w(TAG, "Alarm time has been changed to ${timeTextView.text}")
                }
                else if ((hours in 0..9) && (minutes in 0..9)) {
                    textViewString = "$displayHours:0$minutes"
                    timeTextView.text = textViewString
                    Log.w(TAG, "Alarm time has been changed to ${timeTextView.text}")
                }
                else {
                    textViewString = "$displayHours:$minutes"
                    timeTextView.text = textViewString
                    Log.w(TAG, "Alarm time has been changed to ${timeTextView.text}")
                }
                if (isPM) {
                    amPmTextView.text = "PM"
                    Log.w(TAG, "amPmButtonText is now ${amPmTextView.text}")
                }
                else {
                    amPmTextView.text = "AM"
                    Log.w(TAG, "amPmButtonText is now ${amPmTextView.text}")
                }

                //Schedule the alarmItem
                newAlarmItem.let { scheduler.schedule(newAlarmItem, daysList) }
            }
            popupWindow.dismiss()

            //save the alarm
            var arrayIndex = getIndex(alarmItemLayout, heightIndexes, alarmItemLayout.y.toDouble())
            saveAlarms(hours, minutes, name, alarmItem!!.isEnabled, arrayIndex, isPM, daysList)

        }

    }

    private fun deleteAlarms(alarmIndex: Int, updateCloud: Boolean) {
        val connected = isInternetConnected(this)
        //Local Storage
        val sharedPreferences: SharedPreferences = getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val savedName: String? = sharedPreferences.getString("ALARM_NAME_$alarmIndex", null)
        val savedBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$alarmIndex", false)
        val savedHours: Int? = sharedPreferences.getInt("HOURS_$alarmIndex", 0)
        val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$alarmIndex", 0)
        val savedPM: Boolean = sharedPreferences.getBoolean("IS_PM_$alarmIndex", false)
        val savedDaysList : MutableSet<String>? = sharedPreferences.getStringSet("DAYS_LIST_$alarmIndex", null)

        Log.d(TAG, "Removing Alarm: $alarmIndex")
        Log.d(TAG, "Deleting Saved name: $savedName")
        Log.d(TAG, "Deleting Saved boolean: $savedBoolean")
        Log.d(TAG, "Deleting Saved hours: $savedHours")
        Log.d(TAG, "Deleting Saved minutes: $savedMinutes")
        Log.d(TAG, "Deleting Saved AM/PM State: $savedPM")
        Log.d(TAG, "Deleting Saved daysList: $savedDaysList")

        // Shift the remaining alarms down by one index
        for (i in (alarmIndex + 1)..4) {
            Log.d(TAG, "$i")

            val newIndex = i - 1

            val tempName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
            val tempBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$i", false)
            val tempHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val tempMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
            val tempPM: Boolean = sharedPreferences.getBoolean("IS_PM_$i", false)
            val tempDaysList : MutableSet<String>? = sharedPreferences.getStringSet("DAYS_LIST_$i", null)
            val daysList : List<Int> = if (tempDaysList != null) {
                tempDaysList.map { it.toInt() }
            } else {
                emptyList()
            }

            Log.d(TAG, "Moving saved name: $tempName from $i to $newIndex")
            Log.d(TAG, "Moving saved boolean: $tempBoolean from $i to $newIndex")
            Log.d(TAG, "Moving saved hours: $tempHours from $i to $newIndex")
            Log.d(TAG, "Moving saved minutes: $tempMinutes from $i to $newIndex")
            Log.d(TAG, "Moving saved isPM: $tempPM from $i to $newIndex")
            Log.d(TAG, "Moving saved DaysList: $tempDaysList from $i to $newIndex")

            if (tempName != null) {
                if (updateCloud) {
                    saveAlarms(tempHours, tempMinutes, tempName, tempBoolean, newIndex, tempPM, daysList.toMutableList())
                }
            }

            Log.d(TAG, "Check for Last Index $i")
        }

        editor.remove("ALARM_NAME_$numAlarm")
        editor.remove("IS_ENABLED_$numAlarm")
        editor.remove("HOURS_$numAlarm")
        editor.remove("MINUTES_$numAlarm")
        editor.remove("IS_PM_$numAlarm")
        editor.remove("DAYS_LIST_$numAlarm")

        numAlarm -= 1
        editor.apply()

        Log.d(TAG, "Deleted Alarm $alarmIndex - numAlarm:$numAlarm")

        if (updateCloud) {
            if (auth.currentUser != null && connected) {
                Log.d(TAG, "Shift Cloud Called")
                shiftCloud()
            }
        }
    }

    private fun populateHeightArray(alarmItemLayout: View): Array<Double> {
        //Creation of Arrays to be passed into loadAlarms
        var alarmItemYIndexs = arrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val parentView = alarmItemLayout.parent as ViewGroup
        for (i in 3 until parentView.childCount) {
            val child = parentView.getChildAt(i)
            alarmItemYIndexs[i-3] = child.y.toDouble()
            Log.d(TAG, "Alarm at index ${i - 3} has a height value of ${alarmItemYIndexs[i-3]}")
        }
        return alarmItemYIndexs
    }

    private fun getIndex (alarmItemLayout: View, heightIndexes : Array<Double>, heightWanted: Double ): Int {

        val parentView = alarmItemLayout.parent as ViewGroup

        for (arrayIndex in 0 until 5) {
            val child = parentView.getChildAt(arrayIndex+3)

            if (heightIndexes[arrayIndex] == heightWanted) {
                Log.d(TAG, "ArrayIndex saved properly!")
                return arrayIndex
            }
        }
        Log.d(TAG, "ERR: Height NOT FOUND")
        return -1 //-1 is returned, height now found

    }

    private val selectedDays = mutableListOf<Int>()
    private fun daysCheck(day : TextView): MutableList<Int> {
        day.isSelected = !day.isSelected
        Log.w(TAG, "${day.text} is selected? ${day.isSelected}")
        if (day.isSelected) {
            day.setTextColor(resources.getColor(R.color.black))
            val dayOfWeek = getDayOfWeek(day.text.toString())
            if (!selectedDays.contains(dayOfWeek)) {
                selectedDays.add(dayOfWeek)
            }

        }
        else {
            day.setTextColor(resources.getColor(R.color.grey))
            selectedDays.remove(getDayOfWeek(day.text.toString()))
        }
        Log.d(TAG, "Selected Days: $selectedDays")
        return selectedDays

    }


    private fun getDayOfWeek(day: String): Int {
        return when(day.toLowerCase()) {
            "sun" -> Calendar.SUNDAY
            "mon" -> Calendar.MONDAY
            "tues" -> Calendar.TUESDAY
            "wed" -> Calendar.WEDNESDAY
            "thurs" -> Calendar.THURSDAY
            "fri" -> Calendar.FRIDAY
            "sat" -> Calendar.SATURDAY
            else -> throw java.lang.IllegalArgumentException("Invalid day: $day")
        }
    }

    private fun amPmCheck(hours: Int, isPm: Boolean): Int {
        var newHours = 0
        if (isPm && hours != 12) {
            newHours = hours + 12
        }
        else {
            newHours = hours
        }
        if (newHours == 12 && !isPm) {
            newHours = 0
        }
        "New Hours: $newHours"
        return newHours
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

    private fun accessData(alarmIndex: Int, field: String, callback: (String) -> Unit) {
        val db = Firebase.firestore
        val token = getToken()

        val alarmAccess = db.collection("users/$token/alarms").document("alarm$alarmIndex")
        alarmAccess.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fieldValue = document.get(field) // Get the field value based on the field name
                    Log.d(TAG, "Cloud: Alarm $alarmIndex - $field: $fieldValue")
                    callback(fieldValue.toString())
                } else {
                    Log.d(TAG, "Cloud: Failed to grab Alarm $alarmIndex")
                    callback("end")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                callback("end")
            }
    }

    private suspend fun accessDaysList(i: Int): List<Int> = suspendCoroutine { continuation ->
        accessData(i, "daysList") { cloudDaysList ->
            if (cloudDaysList == "[]") { // Check if the string is empty
                //var accessDaysList: List<Int> = mutableListOf()
                Log.d(TAG, "Sync: CloudDaysList is Empty")
                continuation.resume(emptyList()) // Return an empty list
            } else {
                // Remove square brackets from the string using String.replace() method
                val cleanedString = cloudDaysList.replace("[", "").replace("]", "")
                val minutesList = cleanedString.split(",").map { it.trim().toInt() }
                Log.d(TAG, "Sync: CloudDaysList is Not Empty")
                continuation.resume(minutesList)
            }
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
        const val TAG = "AlarmActivity"
    }
}