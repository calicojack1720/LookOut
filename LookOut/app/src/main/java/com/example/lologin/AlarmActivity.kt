package com.example.lologin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.lologin.ui.login.LoginActivity
import com.google.android.material.tabs.TabLayout


class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        //Navigation bar
        val navigationBar = findViewById<TabLayout>(R.id.navigation_bar)
        navigationBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> startActivity(Intent(this@AlarmActivity, LoginActivity::class.java))
                    1 -> startActivity(Intent(this@AlarmActivity, TimerActivity::class.java))
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
        //New Code -- ABD
        //Goal - Create a listener that opens a window to add new alarms
        val addAlarmButton = findViewById<Button>(R.id.addalarm)
        addAlarmButton.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.popup_layout, null)
            val popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow.animationStyle = R.style.PopupAnimation
            popupWindow.showAsDropDown(addAlarmButton)
        }
    }
}