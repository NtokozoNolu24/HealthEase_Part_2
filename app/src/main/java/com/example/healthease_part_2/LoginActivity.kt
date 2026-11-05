package com.example.healthease_part_2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

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

    // EMAIL AND PASSWORD LOGIN

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
        btnLogin.text = getString(R.string.please_wait)

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

                    showLoginError(
                        task.exception?.localizedMessage
                            ?: "Login failed. Please try again."
                    )
                }
            }
    }

    private fun loadUserProfile(uid: String, email: String) {

        database.child("users")
            .child(uid)
            .child("profile")
            .get()
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val snapshot = task.result

                    if (snapshot.exists()) {

                        val name = snapshot.child("name")
                            .getValue(String::class.java) ?: "User"

                        val language = snapshot.child("language")
                            .getValue(String::class.java) ?: "English"

                        completeLogin(name, email, language)

                    } else {
                        showLoginError(
                            "Your account exists, but your profile could not be found."
                        )
                    }

                } else {
                    showLoginError(
                        "Could not load your profile. Please check your internet connection."
                    )
                }
            }
    }

    // GOOGLE SIGN-IN

    private fun signInWithGoogle() {

        btnGoogleSignIn.isEnabled = false
        btnGoogleSignIn.text = getString(R.string.connecting)

        val googleOption = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)
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

            } catch (e: GetCredentialCancellationException) {

                // The person closed the Google sheet, so just reset the button
                resetGoogleButton()

            } catch (e: NoCredentialException) {

                Log.e("HealthEaseGoogle", "No Google account on this device", e)
                showGoogleError(getString(R.string.no_google_account))

            } catch (e: Exception) {

                Log.e(
                    "HealthEaseGoogle",
                    "Credential Manager sign-in failed",
                    e
                )

                showGoogleError(
                    "${e.javaClass.simpleName}: " +
                            (e.localizedMessage ?: "Unknown error")
                )
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

                val idToken = googleCredential.idToken
                val email = googleCredential.id
                val name = googleCredential.displayName ?: "User"

                authenticateWithFirebase(idToken, email, name)

            } catch (e: GoogleIdTokenParsingException) {

                showGoogleError(
                    e.localizedMessage ?: "Could not process Google credentials."
                )
            }

        } else {
            showGoogleError("Unexpected Google credential. Please try again.")
        }
    }

    // EXCHANGE GOOGLE TOKEN FOR FIREBASE AUTHENTICATION

    private fun authenticateWithFirebase(
        idToken: String,
        email: String,
        name: String
    ) {

        val firebaseCredential =
            GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val uid = firebaseAuth.currentUser?.uid

                    if (uid == null) {
                        showGoogleError("Could not retrieve your Firebase account ID.")
                        return@addOnCompleteListener
                    }

                    loadOrCreateGoogleProfile(uid, email, name)

                } else {

                    showGoogleError(
                        task.exception?.localizedMessage
                            ?: "Firebase Google authentication failed."
                    )
                }
            }
    }

    // LOAD EXISTING PROFILE OR CREATE A NEW ONE

    private fun loadOrCreateGoogleProfile(
        uid: String,
        email: String,
        googleName: String
    ) {

        val profileReference = database.child("users")
            .child(uid)
            .child("profile")

        profileReference.get()
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val snapshot = task.result

                    if (snapshot.exists()) {

                        val name = snapshot.child("name")
                            .getValue(String::class.java) ?: googleName

                        val language = snapshot.child("language")
                            .getValue(String::class.java) ?: "English"

                        completeLogin(name, email, language)

                    } else {

                        val newProfile = mapOf(
                            "name" to googleName,
                            "email" to email,
                            "language" to "English"
                        )

                        profileReference.setValue(newProfile)
                            .addOnCompleteListener(this) { saveTask ->

                                if (saveTask.isSuccessful) {

                                    completeLogin(
                                        googleName,
                                        email,
                                        "English"
                                    )

                                } else {

                                    showGoogleError(
                                        "Your Google account was authenticated, but your profile could not be saved."
                                    )
                                }
                            }
                    }

                } else {

                    showGoogleError(
                        "Could not load your Firebase profile. Check your internet connection."
                    )
                }
            }
    }

    // COMPLETE LOGIN FOR BOTH METHODS

    private fun completeLogin(
        name: String,
        email: String,
        language: String
    ) {

        UserSession.saveProfile(
            this,
            name,
            email,
            language
        )

        UserSession.logIn(this, email, name)

        FcmTokenManager.saveCurrentToken()

        Toast.makeText(
            this,
            getString(R.string.welcome_user, name),
            Toast.LENGTH_SHORT
        ).show()

        openDashboard()
    }

    private fun showLoginError(message: String) {

        btnLogin.isEnabled = true
        btnLogin.text = getString(R.string.login)

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun resetGoogleButton() {
        btnGoogleSignIn.isEnabled = true
        btnGoogleSignIn.text = getString(R.string.sign_in_with_google)
    }

    private fun showGoogleError(message: String) {

        resetGoogleButton()

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun openDashboard() {

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}