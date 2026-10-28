
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
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var spLanguage: Spinner
    private lateinit var btnRegister: Button

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

        when (
            AccountStore.register(
                this,
                name,
                email,
                password,
                language
            )
        ) {
            AccountStore.RegistrationResult.Success -> {
                UserSession.saveProfile(this, name, email, language)

                Toast.makeText(
                    this,
                    getString(R.string.registration_successful),
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
                finish()
            }

            AccountStore.RegistrationResult.DuplicateEmail -> {
                etEmail.error = "This email is already registered."
                etEmail.requestFocus()
            }
        }
    }
}
