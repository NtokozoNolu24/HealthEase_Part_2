package com.example.healthease_part_2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnProfile: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button
    private lateinit var btnMedicationReminders: Button
    private lateinit var btnAppointmentReminders: Button

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        tvWelcome = findViewById(R.id.tvWelcome)
        btnProfile = findViewById(R.id.btnProfile)
        btnSettings = findViewById(R.id.btnSettings)
        btnLogout = findViewById(R.id.btnLogout)
        btnMedicationReminders = findViewById(R.id.btnMedicationReminders)
        btnAppointmentReminders = findViewById(R.id.btnAppointmentReminders)

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        btnMedicationReminders.setOnClickListener {
            startActivity(Intent(this, MedicationReminderActivity::class.java))
        }
        btnAppointmentReminders.setOnClickListener {
            startActivity(Intent(this, AppointmentReminderActivity::class.java))
        }

        btnLogout.setOnClickListener {
            confirmLogout()
        }
    }

    // Runs again when returning from the profile page, so a changed name shows up here
    override fun onResume() {
        super.onResume()
        tvWelcome.text = getString(R.string.welcome_user, UserSession.displayName(this))
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.logout) { _, _ -> logout() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun logout() {
        // End the local session straight away
        UserSession.logOut(this)

        lifecycleScope.launch {
            // Also clear the Google sign-in state so the next login starts fresh
            try {
                CredentialManager.create(this@DashboardActivity)
                    .clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                // Not signed in with Google, or it failed. The local logout has already happened.
            }

            Toast.makeText(
                this@DashboardActivity,
                getString(R.string.logged_out),
                Toast.LENGTH_SHORT
            ).show()

            // Go to Login and clear the back stack so Back cannot return to the dashboard
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
