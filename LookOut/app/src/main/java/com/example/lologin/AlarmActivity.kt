package com.example.lologin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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


class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

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
        submitButton.setOnClickListener {
            val alarmName = popUpView.findViewById<EditText>(R.id.name_text_box)
            val name = alarmName.text.toString()
            //FIXME: Currently does nothing with name
            popupWindow.dismiss()
        }
    }

//    fun onAddAlarmButtonClick(view: View) {
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val popupView = inflater.inflate(R.layout.popup_window, null)
//        val popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, -800)
//        val editAlarmName = popupView.findViewById<EditText>(R.id.EnterName)
////        val editAlarmName = popupView.findViewById<EditText>(R.id.EnterName)
////        editAlarmName.requestFocus()
//    }
    //Add new Alarm

}