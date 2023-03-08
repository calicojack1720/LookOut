package com.example.lologin

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



class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        //TEST
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, "com.example.lologin")
        }
        startActivity(intent)

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
                    1 -> Toast.makeText(applicationContext,"Timer Page is under Construction.", Toast.LENGTH_LONG).show()
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
        addAlarmButton.setOnClickListener {showPopup()}

    }
    private fun showPopup() {
        val popUpView = layoutInflater.inflate(R.layout.popup_window, null)

        val popupWindow = PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(popUpView, Gravity.CENTER, 0, 0)

        val cancelButton = popUpView.findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {popupWindow.dismiss()}

        val submitButton = popUpView.findViewById<Button>(R.id.submitbutton)
//        new
        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null

        submitButton.setOnClickListener {
            val alarmName = popUpView.findViewById<EditText>(R.id.name_text_box)
            val alarmTime = popUpView.findViewById<EditText>(R.id.time_entry)
            val name = alarmName.text.toString()
            //TODO: Currently does nothing with any data
            val timeForAlarm = LocalTime.parse(alarmTime.text.toString()) //Will create a time object in the format hh:mm
            val timeForAlarmInMillis = timeForAlarm.atDate(LocalDate.now()).atZone(ZoneId.systemDefault())

            alarmItem = AlarmItem(
                time = LocalDateTime.from(timeForAlarmInMillis),
                message = name
            )
            alarmItem?.let (scheduler::schedule)

            popupWindow.dismiss()
        }
    }
}