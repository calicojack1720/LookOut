package com.example.lologin.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lologin.AlarmActivity
import com.example.lologin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("WrongViewCast")
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

        //Forgot Password Button
        val forgotPasswordButton = findViewById<TextView>(R.id.ForgotPasswordButton)

        //Skip Log in Password
        val skipLoginButton = findViewById<Button>(R.id.SkipLoginButton)


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

        //On Click of the Skip log in Button
        skipLoginButton.setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
        }

        //On Click of the Forgot Password Button
        forgotPasswordButton.setOnClickListener {
            val email = inputEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(baseContext, "Please enter an Email.", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordReset(email)
            }
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
                    updateUiWithUser()
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
                    updateUiWithUser()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Sign in failed, please enter correct credentials.", Toast.LENGTH_SHORT).show()
                }
            }
        // [END sign_in_with_email]
    }

    //Precondition: model is an object of the LoggedInUserView class
    //              user is a String of the username/email
    //Postcondition: Updates the UI after login and displays welcome message
    private fun updateUiWithUser() {
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

    private fun sendPasswordReset(email: String) {
        // [START send_password_reset]

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset Email sent.")
                    Toast.makeText(baseContext, "Password Reset Email sent to $email." , Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "Password rest Email not sent.")
                    Toast.makeText(baseContext, "No account detected under $email", Toast.LENGTH_SHORT).show()
                }
            }
        // [END send_password_reset]
    }

    //function called in OnStart if user is signed in
    private fun reload() {
        startActivity(Intent(this, AlarmActivity::class.java))
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}
