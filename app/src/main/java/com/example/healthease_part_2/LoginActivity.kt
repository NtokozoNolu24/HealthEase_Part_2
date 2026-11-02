
package com.example.healthease_part_2

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var tvCreateAccount: TextView
    private lateinit var credentialManager: CredentialManager

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseDatabase.getInstance().reference
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)

        credentialManager = CredentialManager.create(this)

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty()) {
            etEmail.error = getString(R.string.please_enter_email)
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.please_enter_valid_email)
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = getString(R.string.please_enter_password)
            etPassword.requestFocus()
            return
        }

        btnLogin.isEnabled = false
        btnLogin.text = "Please wait..."

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val uid = firebaseAuth.currentUser?.uid

                    if (uid == null) {
                        showLoginError("Could not retrieve your account ID.")
                        return@addOnCompleteListener
                    }

                    loadUserProfile(uid, email)

                } else {

                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"

                    val errorCode =
                        (task.exception as? com.google.firebase.auth.FirebaseAuthException)
                            ?.errorCode

                    val message = when (errorCode) {
                        "ERROR_INVALID_EMAIL" ->
                            "Please enter a valid email address."

                        "ERROR_USER_NOT_FOUND",
                        "ERROR_WRONG_PASSWORD",
                        "ERROR_INVALID_CREDENTIAL" ->
                            "Invalid email or password. Please try again."

                        "ERROR_USER_DISABLED" ->
                            "This account has been disabled."

                        "ERROR_NETWORK_REQUEST_FAILED" ->
                            "Network error. Please check your internet connection."

                        else ->
                            task.exception?.localizedMessage
                                ?: "Login failed. Please try again."
                    }

                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun loadUserProfile(uid: String, email: String) {

        database.child("users")
            .child(uid)
            .child("profile")
            .get()
            .addOnCompleteListener(this) { task ->

                btnLogin.isEnabled = true
                btnLogin.text = "Login"

                if (task.isSuccessful) {

                    val snapshot = task.result

                    if (snapshot.exists()) {

                        val name = snapshot.child("name")
                            .getValue(String::class.java) ?: "User"

                        val language = snapshot.child("language")
                            .getValue(String::class.java) ?: "English"

                        UserSession.saveProfile(
                            this,
                            name,
                            email,
                            language
                        )

                        UserSession.logIn(this, email, name)

                        Toast.makeText(
                            this,
                            getString(R.string.login_successful),
                            Toast.LENGTH_SHORT
                        ).show()

                        openDashboard()

                    } else {

                        Toast.makeText(
                            this,
                            "Your account exists, but your profile could not be found. Please contact support.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Could not load your profile. Please check your internet connection and try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun showLoginError(message: String) {
        btnLogin.isEnabled = true
        btnLogin.text = "Login"

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    // Existing Google sign-in flow preserved for now.
    // It still needs Firebase Authentication integration.

    private fun signInWithGoogle() {

        val googleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = getString(R.string.default_web_client_id)
        ).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )

                handleGoogleSignIn(result.credential)

            } catch (e: Exception) {

                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.google_sign_in_failed, e.message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleGoogleSignIn(credential: Credential) {

        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {
                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                val name = googleCredential.displayName ?: "User"

                UserSession.logIn(this, googleCredential.id, name)

                Toast.makeText(
                    this,
                    getString(R.string.welcome_user, name),
                    Toast.LENGTH_SHORT
                ).show()

                openDashboard()

            } catch (e: GoogleIdTokenParsingException) {

                Toast.makeText(
                    this,
                    getString(R.string.unable_process_google, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            Toast.makeText(
                this,
                getString(R.string.unexpected_google_credential),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}

