package com.example.lologin

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.lologin.ui.login.LoginActivity
import com.google.android.material.tabs.TabLayout
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.text.Layout
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.NotificationManagerCompat
import org.w3c.dom.Text
import java.sql.SQLInvalidAuthorizationSpecException
import java.time.*
import android.util.Log
import android.view.ViewParent
import java.io.File


class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        //Creates Alarm Storage File
        createAlarmStorage()

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
                    0 -> startActivity(Intent(this@AlarmActivity, AlarmActivity::class.java))

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

        //checks if ToggleAMPMButton is checked
        //val isPm = popUpView.findViewById<ToggleButton>(R.id.toggleAMPM).isChecked

        submitButton.setOnClickListener {
            val alarmName = popUpView.findViewById<EditText>(R.id.name_text_box)
            //val alarmTime = popUpView.findViewById<EditText>(R.id.time_entry)
            val name = alarmName.text.toString()
            var hours = inputHours.text.toString().toIntOrNull()
            var minutes = inputMinutes.text.toString().toIntOrNull()

            if (hours != null && minutes != null) {
//          AMPMCHECK
//                if (isPm && hours!! < 12) {
//                    hours = hours!! + 12
//                } else if (!isPm && hours == 12) {
//                    hours = 0
//                }

                val timeForAlarm = LocalTime.of(hours, minutes)
                val dateTimeForAlarm = LocalDateTime.of(LocalDate.now(), timeForAlarm) //

                // Calculate the time difference between the current time and the time for the alarm //
                val currentTime = LocalDateTime.now()
                val duration = Duration.between(currentTime, dateTimeForAlarm)

                // If the duration is negative, it means the alarm time has already passed today //
                // so we need to schedule it for tomorrow instead
                val delayMillis = if (duration.isNegative) {
                    duration.plusDays(1).toMillis()
                } else {
                    duration.toMillis()
                }

//                val timeForAlarmInMillis =
//                    timeForAlarm.atDate(LocalDate.now()).atZone(ZoneId.systemDefault())

                alarmItem = AlarmItem(
                    time = dateTimeForAlarm,
                    message = name,
                    isEnabled = true
                )


                //Inflate the Layout file
                val activityAlarmLayout: ViewGroup =
                    findViewById(R.id.activity_alarms) //Was ViewGroup
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val alarmItemLayout =
                    inflater.inflate(R.layout.alarm_item, activityAlarmLayout, false)

//            UserInput of AlarmTime into Layout
                var textViewString = ""
                val timeTextView = alarmItemLayout.findViewById<TextView>(R.id.existing_alarm_time)
                if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                    textViewString = "0$hours:0$minutes"
                    timeTextView.text = textViewString
                }
                else {
                    textViewString = "$hours:$minutes"
                    timeTextView.text = textViewString
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

                val parentRight = 350
                val parentLeft = 100
                val parentTop = 200
                val parentBottom = 2200
                val marginIncrement = 400

                if (activityAlarmLayout.childCount <= 2) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")
                    params.leftMargin = parentLeft
                    params.topMargin = parentTop
                    params.rightMargin = parentRight
                    params.bottomMargin = parentBottom

                    alarmItemLayout.layoutParams = params // set the params on the view

                    activityAlarmLayout.addView(alarmItemLayout)
                    alarmItem?.let(scheduler::schedule)
                } else if (activityAlarmLayout.childCount <= 6) {
                    Log.d(TAG, "Child count is ${activityAlarmLayout.childCount}")
                    params.leftMargin = parentLeft
                    params.rightMargin = parentRight
                    params.topMargin = parentTop + ((activityAlarmLayout.childCount - 2) * marginIncrement)
                    params.bottomMargin = parentBottom - ((activityAlarmLayout.childCount - 2) * marginIncrement)

                    alarmItemLayout.layoutParams = params
                    activityAlarmLayout.addView(alarmItemLayout)
                    alarmItem?.let(scheduler::schedule)

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
                        Log.d(TAG, "Alarm Cancelled")
                    } else {
                        alarmItem?.let(scheduler::schedule)
                        Log.d(TAG, "Alarm Enable")
                    }

                }
                //                Deletion Button
                val deletionButton =
                    alarmItemLayout.findViewById<TextView>(R.id.deletion_button)
                deletionButton.setOnClickListener {
                    val parentView = alarmItemLayout.parent as ViewGroup
                    parentView.removeView(alarmItemLayout)
                    alarmItem?.let { scheduler.cancel(it) }
//                        TODO: Need to update layout as items are deleted
//                    Update layout of remaining views
                    for (i in 2 until parentView.childCount) {
                        val child = parentView.getChildAt(i)
                        val adjustedParams = child.layoutParams as ConstraintLayout.LayoutParams
                        if (i == 2) {
                            adjustedParams.topMargin = parentTop
                            adjustedParams.bottomMargin = parentBottom
                        }
                        else {

                            adjustedParams.topMargin = parentTop + ((i - 2) * marginIncrement)
                            adjustedParams.bottomMargin = parentBottom - ((parentView.childCount - i) * marginIncrement)
                        }
                        child.layoutParams = adjustedParams
                    }
                }



                popupWindow.dismiss()

            }
        }

    }

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

    private fun writeAlarmStorage() {
        val alarmStorage = File(this.filesDir, "alarmStorage.txt")
        val alarmStorageExists = alarmStorage.exists()

        if (alarmStorageExists) {
            Log.w(TAG, "Can write to file: Exists")

//            storageFile.bufferedWriter().use { writer ->
//                alarmItem.forEach { alarmItem ->
//                    writer.write("${alarmItem.time},${alarmItem.message},${alarmItem.isEnabled}\n")
//                }
//            }

//            val inputStream: InputStream = alarmStorage.inputStream()
//
//            val outputText = inputStream.bufferedReader().use {
//                it.readText()
//            }
//
//            //writes to the file
//            val outputStream: OutputStream = alarmStorage.outputStream()

//            if (outputText == "true") {
//                val inputText = "false"
//                outputStream.write(inputText.toByteArray())
//                outputStream.close()
//                Log.d(TAG, "Outputtext was True, now False.")
//            }
//            if (outputText == "false") {
//                val inputText = "true"
//                outputStream.write(inputText.toByteArray())
//                outputStream.close()
//                Log.d(TAG, "Outputtext was False, now True.")
//
//            }
        } else {
            Log.w(TAG, "Can't write to file: Doesn't exist")
        }
    }

    companion object {
        const val TAG = "AlarmActivity"
    }
}