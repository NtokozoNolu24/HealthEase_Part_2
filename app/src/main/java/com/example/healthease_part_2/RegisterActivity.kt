
package com.example.healthease_part_2

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : BaseActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var spLanguage: Spinner
    private lateinit var btnRegister: Button

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseDatabase.getInstance().reference
    }

    // Retains the Firebase account ID if the profile save needs retrying.
    private var createdUserId: String? = null
    private var pendingName = ""
    private var pendingEmail = ""
    private var pendingLanguage = ""

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        spLanguage = findViewById(R.id.spLanguage)
        btnRegister = findViewById(R.id.btnRegister)

        setupLanguages()

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setupLanguages() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            UserSession.LANGUAGES
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spLanguage.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerUser() {

        // If Firebase already created the account, retry saving its profile.
        if (createdUserId != null) {
            saveUserProfile(createdUserId!!)
            return
        }

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        val language = spLanguage.selectedItem.toString()

        if (name.isEmpty()) {
            etName.error = getString(R.string.enter_your_name)
            etName.requestFocus()
            return
        }

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

        if (password.length < 8) {
            etPassword.error = getString(R.string.password_too_short)
            etPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = getString(R.string.confirm_password)
            etConfirmPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error =
                getString(R.string.passwords_dont_match)
            etConfirmPassword.requestFocus()
            return
        }

        pendingName = name
        pendingEmail = email
        pendingLanguage = language

        setRegisterButtonEnabled(false)

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val uid = firebaseAuth.currentUser?.uid

                    if (uid == null) {
                        setRegisterButtonEnabled(true)
                        Toast.makeText(
                            this,
                            "Account was created, but its user ID could not be retrieved.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnCompleteListener
                    }

                    createdUserId = uid
                    saveUserProfile(uid)

                } else {

                    setRegisterButtonEnabled(true)

                    val errorMessage = when (
                        (task.exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                    ) {
                        "ERROR_EMAIL_ALREADY_IN_USE" ->
                            "This email is already registered."

                        "ERROR_INVALID_EMAIL" ->
                            "Please enter a valid email address."

                        "ERROR_WEAK_PASSWORD" ->
                            "Please choose a stronger password."

                        "ERROR_NETWORK_REQUEST_FAILED" ->
                            "Network error. Please check your internet connection."

                        else ->
                            task.exception?.localizedMessage
                                ?: "Registration failed. Please try again."
                    }

                    Toast.makeText(
                        this,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserProfile(uid: String) {

        val profile = mapOf(
            "name" to pendingName,
            "email" to pendingEmail,
            "language" to pendingLanguage
        )

        database.child("users")
            .child(uid)
            .child("profile")
            .setValue(profile)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    UserSession.saveProfile(
                        this,
                        pendingName,
                        pendingEmail,
                        pendingLanguage
                    )

                    // Registration is complete. The user can now log in.
                    firebaseAuth.signOut()

                    Toast.makeText(
                        this,
                        getString(R.string.registration_successful),
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()

                } else {

                    setRegisterButtonEnabled(true)

                    Toast.makeText(
                        this,
                        "Your account was created, but your profile could not be saved. Check your internet connection and tap Register again to retry.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setRegisterButtonEnabled(enabled: Boolean) {
        btnRegister.isEnabled = enabled
        btnRegister.text = if (enabled) {
            "Register"
        } else {
            "Please wait..."
        }
    }
}
