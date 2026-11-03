package com.example.healthease_part_2

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvAvatar: TextView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var spLanguage: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvAvatar = findViewById(R.id.tvAvatar)
        etName = findViewById(R.id.etProfileName)
        etEmail = findViewById(R.id.etProfileEmail)
        spLanguage = findViewById(R.id.spProfileLanguage)
        btnSave = findViewById(R.id.btnSaveProfile)
        btnBack = findViewById(R.id.btnBackToDashboard)

        setupLanguages()
        loadProfile()

        btnSave.setOnClickListener {
            saveProfile()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupLanguages() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            UserSession.LANGUAGES
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLanguage.adapter = adapter
    }

    private fun loadProfile() {
        etName.setText(UserSession.getName(this))
        etEmail.setText(UserSession.getEmail(this))

        val position = UserSession.LANGUAGES.indexOf(UserSession.getLanguage(this))
        spLanguage.setSelection(if (position >= 0) position else 0)

        updateAvatar()
    }

    private fun updateAvatar() {
        val initial = UserSession.displayName(this).firstOrNull()
        tvAvatar.text = initial?.uppercaseChar()?.toString() ?: "?"
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = getString(R.string.enter_your_name)
            etName.requestFocus()
            return
        }

        UserSession.saveProfile(
            this,
            name,
            UserSession.getEmail(this),
            spLanguage.selectedItem.toString()
        )

        updateAvatar()
        Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
    }
}
