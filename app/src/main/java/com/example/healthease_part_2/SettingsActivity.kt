package com.example.healthease_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : BaseActivity() {

    private lateinit var spLanguage: Spinner
    private lateinit var spTextSize: Spinner
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var switchMedication: SwitchCompat
    private lateinit var switchAppointments: SwitchCompat
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        spLanguage = findViewById(R.id.spLanguage)
        spTextSize = findViewById(R.id.spTextSize)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        switchNotifications = findViewById(R.id.switchNotifications)
        switchMedication = findViewById(R.id.switchMedication)
        switchAppointments = findViewById(R.id.switchAppointments)
        btnSave = findViewById(R.id.btnSaveSettings)
        btnBack = findViewById(R.id.btnBack)

        setupLanguageSpinner()
        setupTextSizeSpinner()
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

    private fun setupTextSizeSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            UserSession.TEXT_SIZES
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTextSize.adapter = adapter
    }

    private fun loadSettings() {
        val languagePosition = UserSession.LANGUAGES.indexOf(UserSession.getLanguage(this))
        if (languagePosition >= 0) {
            spLanguage.setSelection(languagePosition)
        }

        val sizePosition = UserSession.TEXT_SIZES.indexOf(UserSession.getTextSize(this))
        if (sizePosition >= 0) {
            spTextSize.setSelection(sizePosition)
        }

        switchDarkMode.isChecked = UserSession.isDarkModeEnabled(this)
        switchNotifications.isChecked = UserSession.isNotificationsEnabled(this)
        switchMedication.isChecked = UserSession.isMedicationRemindersEnabled(this)
        switchAppointments.isChecked = UserSession.isAppointmentRemindersEnabled(this)
    }

    private fun saveSettings() {
        val selectedLanguage = spLanguage.selectedItem.toString()
        val oldLanguage = UserSession.getLanguage(this)
        val oldDarkMode = UserSession.isDarkModeEnabled(this)
        val newDarkMode = switchDarkMode.isChecked

        UserSession.saveSettings(
            this,
            selectedLanguage,
            switchNotifications.isChecked,
            switchMedication.isChecked,
            switchAppointments.isChecked,
            newDarkMode,
            spTextSize.selectedItem.toString()
        )

        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()

        if (oldLanguage != selectedLanguage) {
            // Restart the whole app so every screen picks up the new language
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (oldDarkMode != newDarkMode) {
            // Switching mode recreates the open screens automatically
            AppCompatDelegate.setDefaultNightMode(
                if (newDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}