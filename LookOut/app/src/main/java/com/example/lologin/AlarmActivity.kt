package com.example.lologin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import android.widget.*
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import com.example.lologin.LoginActivity.Companion.TAG
import com.google.firebase.auth.FirebaseAuth

var numAlarm = -1

private lateinit var auth: FirebaseAuth
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
            startActivity(Intent(this, LoginActivity::class.java))
        }

        //Creates Alarm Storage File
        createAlarmStorage()

        //loadAlarms
        loadAlarms()

//        Notifications
        val notificationManager = NotificationManagerCompat.from(this)
//
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
            builder.show()
        }

        //Navigation bar
        val navigationBar = findViewById<TabLayout>(R.id.navigation_bar)
        navigationBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    //Sends the user back to the Alarms page when clicking on the alarms button. It has an issue I need to look into.
                    //0 -> startActivity(Intent(this@AlarmActivity, AlarmActivity::class.java))
                    0 -> Toast.makeText(
                        applicationContext,
                        "Timer Page is under Construction.",
                        Toast.LENGTH_LONG
                    ).show()


                    //Here for TimerActivity page
                    //1 -> startActivity(Intent(this@AlarmActivity, TimerActivity::class.java))

                    // Creates a text box telling the user the timer page isn't available.
                    1 -> Toast.makeText(
                        applicationContext,
                        "Timer Page is under Construction.",
                        Toast.LENGTH_LONG
                    ).show()
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


        submitButton.setOnClickListener {

            val alarmName = popUpView.findViewById<EditText>(R.id.name_text_box)
            val name = alarmName.text.toString()
            var hours = inputHours.text.toString().toInt()
            val minutes = inputMinutes.text.toString().toIntOrNull()

            if (hours != null && hours in 1..12 && minutes != null && minutes in 0..59) {
                //AMPMCHECK
                hours = amPmCheck(hours, isPM)
                Log.w(TAG, "Hours: $hours, isPM: $isPM")

                val timeForAlarm = LocalTime.of(hours, minutes)
                var dateTimeForAlarm = LocalDateTime.of(LocalDate.now(), timeForAlarm) //

                // Calculate the time difference between the current time and the time for the alarm //
                val currentTime = LocalDateTime.now()

                if (dateTimeForAlarm.isBefore(currentTime)) {
                    Log.d(TAG, "Duration is negative, adding 1 day for alarm")
                    dateTimeForAlarm = dateTimeForAlarm.plusDays(1)
                }

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
                    displayHours = hours - 12
                }
                else {
                    displayHours = hours
                }

                val timeTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_time)
                if ((hours in 0..9) && (minutes > 9)) {
                    textViewString = "0$displayHours:$minutes"
                    timeTextView.text = textViewString
                }
                else if (hours > 9 && (minutes in 0..9)) {
                    textViewString = "$displayHours:0$minutes"
                    timeTextView.text = textViewString
                }
                else if ((hours in 0..9) && (minutes in 0..9)) {
                    textViewString = "0$displayHours:0$minutes"
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


//            Set the Parameters for the new Layout

                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT, // set width to wrap content
                    ConstraintLayout.LayoutParams.MATCH_PARENT // set height to wrap content
                )
                Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")

//                The following method displays the alarm right
                val context: Context = this
                val parentRight = context.dpToPx(120)
                val parentLeft = context.dpToPx(25)
                val parentTop = context.dpToPx(100)
                val parentBottom = context.dpToPx(600)
                val marginIncrement = context.dpToPx(100)

                var arrayIndex = 0


                if (activityAlarmLayout.childCount <= 3) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")
//                    params.leftMargin = parentLeft
//                    params.topMargin = parentTop
//                    params.rightMargin = parentRight
//                    params.bottomMargin = parentBottom
//
//                    alarmItemLayout.layoutParams = params // set the params on the view

                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y

                    activityAlarmLayout.addView(alarmItemLayout)
                    alarmItem?.let(scheduler::schedule)



                    when (params.bottomMargin) {
                        2100 -> arrayIndex = 0
                        1750 -> arrayIndex = 1
                        1400 -> arrayIndex = 2
                        1050 -> arrayIndex = 3
                        700 -> arrayIndex = 4
                        else -> { // Note the block
                            Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                        }
                    }

                    Log.d(TAG, "First ${activityAlarmLayout.childCount} ${params.bottomMargin}")
                    //passes through hours, minutes, name, and enabled state to saveAlarms
                    saveAlarms(hours, minutes, name, alarmItem!!.isEnabled, arrayIndex, isPM)
                    numAlarm += 1

                } else if (activityAlarmLayout.childCount <= 7) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")
//                    params.leftMargin = parentLeft
//                    params.rightMargin = parentRight
//                    params.topMargin = parentTop + ((activityAlarmLayout.childCount - 3) * marginIncrement)
//                    params.bottomMargin = parentBottom - ((activityAlarmLayout.childCount - 3) * marginIncrement)
//
//                    alarmItemLayout.layoutParams = params

                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y + ((activityAlarmLayout.childCount - 3) * marginIncrement)

                    activityAlarmLayout.addView(alarmItemLayout)
                    alarmItem?.let(scheduler::schedule)

                    when (params.bottomMargin) {
                        2100 -> arrayIndex = 0
                        1750 -> arrayIndex = 1
                        1400 -> arrayIndex = 2
                        1050 -> arrayIndex = 3
                        700 -> arrayIndex = 4
                        else -> { // Note the block
                            Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                        }
                    }

                    Log.d(TAG, "${activityAlarmLayout.childCount} ${params.bottomMargin}")

                    saveAlarms(hours, minutes, name, alarmItem!!.isEnabled, arrayIndex, isPM)
                    numAlarm += 1

                    // populate height values for alarmItems, Creation of height indexes
                    var heightIndexes = populateHeightArray(alarmItemLayout)

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Maximum Alarm Number has been reached.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                //checks to see if Alarm is Enabled/Disabled
                toggleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) {
                        alarmItem?.let { scheduler.cancel(it) }

                        when (params.bottomMargin) {
                            2100 -> arrayIndex = 0
                            1750 -> arrayIndex = 1
                            1400 -> arrayIndex = 2
                            1050 -> arrayIndex = 3
                            700 -> arrayIndex = 4
                            else -> { // Note the block
                                Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                            }
                        }

                        saveAlarms(hours, minutes, name, false, arrayIndex, isPM)
                        Log.d(TAG, "Alarm Cancelled")
                    } else {
                        alarmItem?.let(scheduler::schedule)

                        when (params.bottomMargin) {
                            2100 -> arrayIndex = 0
                            1750 -> arrayIndex = 1
                            1400 -> arrayIndex = 2
                            1050 -> arrayIndex = 3
                            700 -> arrayIndex = 4
                            else -> { // Note the block
                                Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                            }
                        }

                        saveAlarms(hours, minutes, name, true, arrayIndex, isPM)
                        Log.d(TAG, "Alarm Enable")
                    }

                }
                //Deletion Button
                val deletionButton = alarmItemLayout.findViewById<TextView>(R.id.deletion_button)
                //On Click of Delete Button
                deletionButton.setOnClickListener {

                    when (params.bottomMargin) {
                        2100 -> arrayIndex = 0
                        1750 -> arrayIndex = 1
                        1400 -> arrayIndex = 2
                        1050 -> arrayIndex = 3
                        700 -> arrayIndex = 4
                        else -> { // Note the block
                            Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                        }
                    }

                    val parentView = alarmItemLayout.parent as ViewGroup
                    parentView.removeView(alarmItemLayout)

                    alarmItem?.let { scheduler.cancel(it) }

                    deleteAlarms(arrayIndex)

                    //TODO: Need to update layout as items are deleted
//                    Update layout of remaining views

                    for (i in 3 until parentView.childCount) {
                        val child = parentView.getChildAt(i)
                        val adjustedParams = child.layoutParams as ConstraintLayout.LayoutParams
                        if (i == 3) {
                            adjustedParams.topMargin = parentTop
                            adjustedParams.bottomMargin = parentBottom
                        }
                        else {
                            adjustedParams.topMargin = parentTop + ((i - 3) * marginIncrement)
                            adjustedParams.bottomMargin = context.dpToPx(700) - adjustedParams.topMargin
                        }
                        child.layoutParams = adjustedParams
                    }

//                    End of For Layout Adjustment

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
    private fun saveAlarms(hours: Int?, minutes: Int?, name: String, isEnabled: Boolean, alarmIndex: Int, isPM: Boolean) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.apply() {
            putString("ALARM_NAME_$alarmIndex", name)
            putBoolean("IS_ENABLED_$alarmIndex", isEnabled)
            putInt("HOURS_$alarmIndex", hours ?: 0)
            putInt("MINUTES_$alarmIndex", minutes ?: 0)
            putBoolean("IS_PM_$alarmIndex", isPM)
        }.apply()
        Log.d(TAG, "Saved Alarm $alarmIndex")
    }

    private fun loadAlarms() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)

        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null

        for (i in 0 until 5) {

            //Setting default values
            val savedName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
            val savedBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$i", false)
            val savedHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
            val savedPM: Boolean = sharedPreferences.getBoolean("IS_PM_$i", false)

            Log.d(TAG, "Alarm: $i")
            Log.d(TAG, "Saved name: $savedName")
            Log.d(TAG, "Saved boolean: $savedBoolean")
            Log.d(TAG, "Saved hours: $savedHours")
            Log.d(TAG, "Saved minutes: $savedMinutes")
            Log.d(TAG, "Saved PM/AM State: $savedPM")

            if (savedName != null) {
                numAlarm += 1
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
                val y = screenHeight * .13f //20% from top

//            UserInput of AlarmTime into Layout
                var displayHours = 0
                if (savedPM) {
                    displayHours = savedHours - 12
                }
                else {
                    displayHours = savedHours
                }

                var textViewString = ""
                val timeTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_time)
                if ((savedHours in 0..9) && (savedMinutes > 9)) {
                    textViewString = "0$displayHours:$savedMinutes"
                    timeTextView.text = textViewString
                } else if ((savedHours > 9) && (savedMinutes in 0..9)) {
                    textViewString = "$displayHours:0$savedMinutes"
                    timeTextView.text = textViewString
                } else if ((savedHours in 0..9) && (savedMinutes in 0..9)) {
                    textViewString = "0$displayHours:0$savedMinutes"
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


//            Set the Parameters for the new Layout

                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT, // set width to wrap content
                    ConstraintLayout.LayoutParams.MATCH_PARENT // set height to wrap content
                )
                //Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")

                val context: Context = this
                val parentRight = context.dpToPx(120)
                val parentLeft = context.dpToPx(25)
                val parentTop = context.dpToPx(100)
                val parentBottom = context.dpToPx(600)
                val marginIncrement = context.dpToPx(100)

                var arrayIndex = 0

                if (activityAlarmLayout.childCount <= 3) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")
//                    params.leftMargin = parentLeft
//                    params.topMargin = parentTop
//                    params.rightMargin = parentRight
//                    params.bottomMargin = parentBottom
//
//                    alarmItemLayout.layoutParams = params // set the params on the view
                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y

                    activityAlarmLayout.addView(alarmItemLayout)
                    if (toggleSwitch.isChecked && toggleSwitch.isEnabled) {
                        alarmItem?.let(scheduler::schedule)
                        //Log.d(TAG, "First ${activityAlarmLayout.childCount} ${params.bottomMargin}")
                    }

                } else if (activityAlarmLayout.childCount <= 7) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")
//                    params.leftMargin = parentLeft
//                    params.rightMargin = parentRight
//                    params.topMargin =
//                        parentTop + ((activityAlarmLayout.childCount - 3) * marginIncrement)
//                    params.bottomMargin =
//                        parentBottom - ((activityAlarmLayout.childCount - 3) * marginIncrement)
//
//                    alarmItemLayout.layoutParams = params
                    alarmItemLayout.x = x.coerceIn(0f, maxChildViewX)
                    alarmItemLayout.y = y + ((activityAlarmLayout.childCount - 3) * marginIncrement)

                    activityAlarmLayout.addView(alarmItemLayout)
                    if (toggleSwitch.isChecked && toggleSwitch.isEnabled) {
                        alarmItem?.let(scheduler::schedule)
                        //Log.d(TAG, "${activityAlarmLayout.childCount} ${params.bottomMargin}")
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Maximum Alarm Number has been reached.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                //checks to see if Alarm is Enabled/Disabled
                toggleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) {
                        alarmItem?.let { scheduler.cancel(it) }

                        when (params.bottomMargin) {
                            2100 -> arrayIndex = 0
                            1750 -> arrayIndex = 1
                            1400 -> arrayIndex = 2
                            1050 -> arrayIndex = 3
                            700 -> arrayIndex = 4
                            else -> { // Note the block
                                Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                            }
                        }
                        Log.d(TAG, "${activityAlarmLayout.childCount} ${params.bottomMargin}")

                        saveAlarms(savedHours, savedMinutes, name, false, arrayIndex, savedPM)//
                        Log.d(TAG, "Alarm Cancelled")
                    } else {
                        alarmItem?.let(scheduler::schedule)

                        when (params.bottomMargin) {
                            2100 -> arrayIndex = 0
                            1750 -> arrayIndex = 1
                            1400 -> arrayIndex = 2
                            1050 -> arrayIndex = 3
                            700 -> arrayIndex = 4
                            else -> { // Note the block
                                Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                            }
                        }
                        Log.d(TAG, "${activityAlarmLayout.childCount} ${params.bottomMargin}")
                        saveAlarms(savedHours, savedMinutes, name, true, arrayIndex, savedPM)//
                        Log.d(TAG, "Alarm Enable")
                    }

                }

                //Deletion Button
                val deletionButton = alarmItemLayout.findViewById<TextView>(R.id.deletion_button)
                //On Click of Delete Button
                deletionButton.setOnClickListener {
                    when (params.bottomMargin) {
                        2100 -> arrayIndex = 0
                        1750 -> arrayIndex = 1
                        1400 -> arrayIndex = 2
                        1050 -> arrayIndex = 3
                        700 -> arrayIndex = 4
                        else -> { // Note the block
                            Log.d(TAG, "Brr ${activityAlarmLayout.childCount}")
                        }
                    }
                    val parentView = alarmItemLayout.parent as ViewGroup
                    parentView.removeView(alarmItemLayout)

                    deleteAlarms(arrayIndex)

                    alarmItem?.let { scheduler.cancel(it) }

                    //TODO: Need to update layout as items are deleted
//                    Update layout of remaining views

                    for (i in 3 until parentView.childCount) {
                        val child = parentView.getChildAt(i)
                        val adjustedParams = child.layoutParams as ConstraintLayout.LayoutParams
                        if (i == 3) {
                            adjustedParams.topMargin = parentTop
                            adjustedParams.bottomMargin = parentBottom
                        }
                        else {
                            adjustedParams.topMargin = parentTop + ((i - 3) * marginIncrement)
                            adjustedParams.bottomMargin = context.dpToPx(700) - adjustedParams.topMargin
                        }
                        child.layoutParams = adjustedParams
                    }

//                    End of For Layout Adjustment

                }
            }
        }
    }

    private fun deleteAlarms(alarmIndex: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("alarmStorage", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        //testing
        val savedName: String? = sharedPreferences.getString("ALARM_NAME_$alarmIndex", null)
        val savedBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$alarmIndex", false)
        val savedHours: Int? = sharedPreferences.getInt("HOURS_$alarmIndex", 0)
        val savedMinutes: Int? = sharedPreferences.getInt("MINUTES_$alarmIndex", 0)
        val savedPM: Boolean = sharedPreferences.getBoolean("IS_PM_$alarmIndex", false)

        Log.d(TAG, "Removing Alarm: $alarmIndex")
        Log.d(TAG, "Deleting Saved name: $savedName")
        Log.d(TAG, "Deleting Saved boolean: $savedBoolean")
        Log.d(TAG, "Deleting Saved hours: $savedHours")
        Log.d(TAG, "Deleting Saved minutes: $savedMinutes")
        Log.d(TAG, "Deleting Saved AM/PM State: $savedPM")

        // Shift the remaining alarms down by one index
        for (i in (alarmIndex + 1)..4) {
            Log.d(TAG, "$i")

            //val deletedIndexes = parentView.childAt(i)

            val newIndex = i - 1

            val tempName: String? = sharedPreferences.getString("ALARM_NAME_$i", null)
            val tempBoolean: Boolean = sharedPreferences.getBoolean("IS_ENABLED_$i", false)
            val tempHours: Int? = sharedPreferences.getInt("HOURS_$i", 0)
            val tempMinutes: Int? = sharedPreferences.getInt("MINUTES_$i", 0)
            val tempPM: Boolean = sharedPreferences.getBoolean("IS_PM_$i", false)

            Log.d(TAG, "Moving saved name: $tempName from $i to $newIndex")
            Log.d(TAG, "Moving saved boolean: $tempBoolean from $i to $newIndex")
            Log.d(TAG, "Moving saved hours: $tempHours from $i to $newIndex")
            Log.d(TAG, "Moving saved minutes: $tempMinutes from $i to $newIndex")
            Log.d(TAG, "Moving saved minutes: $tempPM from $i to $newIndex")

            if (tempName != null) {
                saveAlarms(tempHours, tempMinutes, tempName, tempBoolean, newIndex, tempPM)
            }

//            if (alarmIndex == 0) {
//                editor.remove("ALARM_NAME_$alarmIndex")
//                editor.remove("IS_ENABLED_$alarmIndex")
//                editor.remove("HOURS_$alarmIndex")
//                editor.remove("MINUTES_$alarmIndex")
//            }


            Log.d(TAG, "Check for Last Index $i")
        }

        editor.remove("ALARM_NAME_$numAlarm")
        editor.remove("IS_ENABLED_$numAlarm")
        editor.remove("HOURS_$numAlarm")
        editor.remove("MINUTES_$numAlarm")
        editor.remove("IS_PM_$numAlarm")

        numAlarm -= 1

        //editor.clear()

        editor.apply()

        Log.d(TAG, "Deleted Alarm $alarmIndex")
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
    private fun getArrayIndexes (alarmItemLayout: View, heightIndexes : Array<Double> ) {

    }

    private fun amPmCheck(hours: Int, isPm: Boolean): Int {
        var newHours = 0
        if (isPm) {
            newHours = hours + 12
        }
        else {
            newHours = hours
        }
        if (newHours == 24) {
            newHours = 0
        }
        return newHours
    }

    companion object {
        const val TAG = "AlarmActivity"
    }
    fun Context.dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    fun Context.pxToDp(px: Int): Int {
        val density = resources.displayMetrics.density
        return (px / density).toInt()
    }

}