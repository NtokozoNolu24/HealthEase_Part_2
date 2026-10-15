package com.example.healthease

import android.app.Application
import com.example.healthease.data.local.HealthEaseDatabase
import com.example.healthease.data.repository.*
import com.example.healthease.data.seed.ArticleSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HealthEaseApplication : Application() {

    val database by lazy { HealthEaseDatabase.getInstance(this) }

    val userRepository by lazy { UserRepository(database.userDao()) }
    val profileRepository by lazy { ProfileRepository(database.profileDao()) }
    val medicationRepository by lazy { MedicationRepository(database.medicationDao()) }
    val appointmentRepository by lazy { AppointmentRepository(database.appointmentDao()) }
    val notificationRepository by lazy { NotificationRepository(database.notificationDao()) }
    val settingsRepository by lazy { SettingsRepository(database.userSettingsDao()) }
    val articleRepository by lazy { ArticleRepository(database.articleDao(), database.bookmarkDao()) }   // ← NEW

    var currentUserId: String? = null
        private set

    fun setCurrentUser(userId: String) { currentUserId = userId }
    fun clearCurrentUser() { currentUserId = null }

    override fun onCreate() {
        super.onCreate()
        // Seed articles once at startup (no-op if already seeded)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            ArticleSeeder.seedIfEmpty(this@HealthEaseApplication, database.articleDao())
        }
    }
}