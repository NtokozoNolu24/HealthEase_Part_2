package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_profiles")
data class ProfileEntity(
    @PrimaryKey val profileId: String = UUID.randomUUID().toString(),
    val userId: String,
    // PROF-001
    val dateOfBirth: String? = null,          // yyyy-MM-dd
    val gender: String? = null,
    val avatarEmoji: String = "👤",
    // MED-001
    val bloodType: String? = null,
    val allergies: String = "",               // comma-separated
    val chronicConditions: String = "",
    val currentMedications: String = "",
    val emergencyContactName: String? = null,
    val emergencyContactPhone: String? = null,
    val emergencyContactRelationship: String? = null,
    val primaryPhysician: String? = null,
    val physicianPhone: String? = null,
    // PROF-002
    val addressHome: String? = null,
    val addressWork: String? = null,
    // MED-004
    val insuranceProvider: String? = null,
    val insurancePolicyNumber: String? = null,
    val insuranceExpiry: String? = null,
    // Metadata
    val updatedAt: Long = System.currentTimeMillis()
)