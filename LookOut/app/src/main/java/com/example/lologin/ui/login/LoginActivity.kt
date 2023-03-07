package com.example.lologin.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lologin.AlarmActivity
import com.example.lologin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //initialize Firebase
        auth = Firebase.auth

        //Input Password Box
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        //Input Email Box
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        //Register Button
        val registerButton = findViewById<Button>(R.id.RegisterButton)
        //Sign in Button
        val signInButton = findViewById<Button>(R.id.LoginButton)

        //On click of the Sign in Button
        signInButton.setOnClickListener {
            //takes input of email and password
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            //checks to see if Email and Password Boxes are empty and alerts the user.
            if (email.isEmpty() || password.isEmpty()) {
                Log.d(TAG, "SignIn:Email Or Password is Null")
                Toast.makeText(baseContext, "Email Or Password is Empty.", Toast.LENGTH_SHORT).show()
            }  else {
                signIn(email, password)
            }
        }

        //On click of the Register Button
        registerButton.setOnClickListener {
            //takes input of email and password
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            //checks to see if Email and Password Boxes are empty and alerts the user.
            if (email.isEmpty() || password.isEmpty()) {
                Log.d(TAG, "Register:Email Or Password is Null")
                Toast.makeText(baseContext, "Email Or Password is Empty.", Toast.LENGTH_SHORT).show()
            }  else {
                createAccount(email, password)
            }
        }

        //Navigation to AlarmsActivity.kt
        val skipLoginButton = findViewById<Button>(R.id.SkipLoginButton)

        skipLoginButton.setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
        }

    }//End of OnCreate Function

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d(TAG, "currentuser:notnull")
            reload()
            getUserProfile()
        } else {
            Log.d(TAG, "currentuser:null")
            getUserProfile()
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUiWithUser(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Registration failed, please enter valid credentials.", Toast.LENGTH_SHORT).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUiWithUser(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Sign in failed, please enter correct credentials.", Toast.LENGTH_SHORT).show()
                    //updateUiWithUser(null)
                }
            }
        // [END sign_in_with_email]
    }

    //Precondition: model is an object of the LoggedInUserView class
    //              user is a String of the username/email
    //Postcondition: Updates the UI after login and displays welcome message
    private fun updateUiWithUser(user: FirebaseUser?) {
        val welcome = "Welcome"

        startActivity(Intent(this, AlarmActivity::class.java))
        Toast.makeText(
            applicationContext,
            welcome,
            Toast.LENGTH_LONG
        ).show()
    }

    //Gets User Profile information, used for debugging purposes
    private fun getUserProfile() {
        // [START get_user_profile]
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            //val name = it.displayName
            val email = it.email
            //val photoUrl = it.photoUrl

            // Check if user's email is verified
            //val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            //val uid = it.uid
            if (email != null) {
                Log.d(TAG, email)
            }
        }
        // [END get_user_profile]
    }

    //function called in OnStart if user is signed in
    private fun reload() {
        startActivity(Intent(this, AlarmActivity::class.java))
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}
