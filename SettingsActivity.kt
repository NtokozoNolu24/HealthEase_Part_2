package com.example.healthease_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var spLanguage: Spinner
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var switchMedication: SwitchCompat
    private lateinit var switchAppointments: SwitchCompat
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        spLanguage = findViewById(R.id.spLanguage)
        switchNotifications = findViewById(R.id.switchNotifications)
        switchMedication = findViewById(R.id.switchMedication)
        switchAppointments = findViewById(R.id.switchAppointments)
        btnSave = findViewById(R.id.btnSaveSettings)
        btnBack = findViewById(R.id.btnBack)

        setupLanguageSpinner()
        loadSettings()

        btnSave.setOnClickListener {
            saveSettings()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            UserSession.LANGUAGES
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLanguage.adapter = adapter
    }

    private fun loadSettings() {
        val savedLanguage = UserSession.getLanguage(this)
        val languagePosition = UserSession.LANGUAGES.indexOf(savedLanguage)
        if (languagePosition >= 0) {
            spLanguage.setSelection(languagePosition)
        }

        switchNotifications.isChecked = UserSession.isNotificationsEnabled(this)
        switchMedication.isChecked = UserSession.isMedicationRemindersEnabled(this)
        switchAppointments.isChecked = UserSession.isAppointmentRemindersEnabled(this)
    }

    private fun saveSettings() {
        val selectedLanguage = spLanguage.selectedItem.toString()
        val oldLanguage = UserSession.getLanguage(this)

        UserSession.saveSettings(
            this,
            selectedLanguage,
            switchNotifications.isChecked,
            switchMedication.isChecked,
            switchAppointments.isChecked
        )

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()

        if (oldLanguage != selectedLanguage) {
            // Restart the app to apply the new language
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
