package com.example.lologin.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.util.Log
import com.example.lologin.AlarmActivity
import com.example.lologin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

//    private lateinit var loginViewModel: LoginViewModel
//    private lateinit var binding: ActivityLoginBinding
//
//
//    private lateinit var oneTapClient: SignInClient
//    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {//////////////////////////////////////////////////////////////////////////////////////////////
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //initialize Firebase
        auth = Firebase.auth

//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val username = binding.username
//        val password = binding.password
//        val login = binding.login
//        val loading = binding.loading
//
//        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
//            .get(LoginViewModel::class.java)
//
//        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
//            val loginState = it ?: return@Observer
//
//            // disable login button unless both username / password is valid
//            login.isEnabled = loginState.isDataValid
//
//            if (loginState.usernameError != null) {
//                username.error = getString(loginState.usernameError)
//            }
//            if (loginState.passwordError != null) {
//                password.error = getString(loginState.passwordError)
//            }
//        })
////
////        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
////            val loginResult = it ?: return@Observer
////
////            loading.visibility = View.GONE
////            if (loginResult.error != null) {
////                showLoginFailed(loginResult.error)
////            }
////            if (loginResult.success != null) {
////                //updateUiWithUser(loginResult.success)
////            }
////            setResult(Activity.RESULT_OK)
////
////            //Complete and destroy login activity once successful
////            finish()
////        })
////
//        username.afterTextChanged {
//            loginViewModel.loginDataChanged(
//                username.text.toString(),
//                password.text.toString()
//            )
//        }
//
//        password.apply {
//            afterTextChanged {
//                loginViewModel.loginDataChanged(
//                    username.text.toString(),
//                    password.text.toString()
//                )
//            }
//
//            setOnEditorActionListener { _, actionId, _ ->
//                when (actionId) {
//                    EditorInfo.IME_ACTION_DONE ->
//                        loginViewModel.login(
//                            username.text.toString(),
//                            password.text.toString()
//                        )
//                }
//                false
//            }
//
//            login.setOnClickListener {
//                loading.visibility = View.VISIBLE
//                loginViewModel.login(username.text.toString(), password.text.toString())
//            }
//        }

        //Input Password Box
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        //Input Email Box
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        //Register Button
        val registerButton = findViewById<Button>(R.id.RegisterButton)

        //On click of the Register Button
        registerButton.setOnClickListener {
            //takes input of email and password
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()
            createAccount(email, password)

        }

        //Navigation to AlarmsActivity.kt
        val skipLoginButton = findViewById<Button>(R.id.SkipLoginButton)

        skipLoginButton.setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
        }

    }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //reload()
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
                    Toast.makeText(baseContext, "Authentication failed, please enter a real email",
                        Toast.LENGTH_SHORT).show()
                    //updateUiWithUser(null)
                }
            }
        // [END create_user_with_email]
    }

    //Precondition: model is an object of the LoggedInUserView class
    //              user is a String of the username/email
    //Postcondition: Updates the UI after login and displays welcome message
    private fun updateUiWithUser(user: FirebaseUser?) {
        val welcome = "Welcome"

        startActivity(Intent(this, AlarmActivity::class.java))
        Toast.makeText(
            applicationContext,
            "$welcome $user",
            Toast.LENGTH_LONG
        ).show()

        startActivity(Intent(this, AlarmActivity::class.java))
    }

//    private fun showLoginFailed(@StringRes errorString: Int) {
//        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
//    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
//fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
//    this.addTextChangedListener(object : TextWatcher {
//        override fun afterTextChanged(editable: Editable?) {
//            afterTextChanged.invoke(editable.toString())
//        }
//
//        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//    })
//}




////line 48
//// TODO: Integrate sign in with entered email and password
////Integrating Google sign-in with the BeginSignInRequest object calling setGoogleIdTokenRequestOptions
//oneTapClient = Identity.getSignInClient(this)
//signInRequest = BeginSignInRequest.builder()
//.setGoogleIdTokenRequestOptions(
//BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//.setSupported(true)
//// Your server's client ID, not your Android client ID.
//// Using API key 1 from Google Cloud APIs and Services
//.setServerClientId("486612106496-5j9befb9iphu5lobt16rbkbehqncegjh.apps.googleusercontent.com")
//// Only show accounts previously used to sign in.
//.setFilterByAuthorizedAccounts(true)
//.build()
//)
//.build()
///*paoverride fun onStart() {
//    super.onStart()
//    // Check if user is signed in (non-null) and update UI accordingly.
//    var currentUser = auth.getCurrentUser()
//    updateUI(currentUser);
//}*/




//authenticate with Firebase using email and password
// TODO: App CRASHES here
/*auth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "createUserWithEmail:success")
            val user = auth.currentUser
            // TODO: Need to figure out how the UI/Result should be updated
            //updateUI(user)
        } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "createUserWithEmail:failure", task.exception)
            Toast.makeText(
                baseContext, "Authentication failed.",
                Toast.LENGTH_SHORT
            ).show()
            // TODO: Need to figure out how the UI/Result should be updated
            //updateUI(null)
        }
    }*/