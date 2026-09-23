package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val userId: String,
    // SET-001
    val language: String = "en",              // en, zu, af
    val theme: String = "system",             // light, dark, system
    val fontSize: String = "medium",          // small, medium, large
    val units: String = "metric",             // metric, imperial
    // SET-002
    val notifyReminders: Boolean = true,
    val notifyTips: Boolean = true,
    val notifySystem: Boolean = true,
    val notifySync: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "06:00",
    // SET-003
    val dataSharing: Boolean = false,
    val biometricUnlock: Boolean = false,
    val lockScreenMedicalId: Boolean = false,
    // Metadata
    val updatedAt: Long = System.currentTimeMillis()
)