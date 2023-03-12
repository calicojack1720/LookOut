package com.example.lologin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.InputStream
import java.io.OutputStream


class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

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
        //New Code -- ABD
        //Goal - Create a listener that opens a window to add new alarms

    }
    fun onAddAlarmButtonClick(view: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window, null)
        val popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        popupWindow.showAsDropDown(view)
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
    companion object {
        private const val TAG = "EmailPassword"
    }
}