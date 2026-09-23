package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val medicationId: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val dosage: String,
    val frequency: String,               // e.g. "Once daily", "Twice daily"
    val scheduleTimes: String,           // "08:00,20:00" (comma-separated)
    val startDate: String,               // yyyy-MM-dd
    val endDate: String? = null,
    val withFood: Boolean = false,       // REM-004
    val instructions: String? = null,
    val refillDate: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "medication_logs")
data class MedicationLogEntity(
    @PrimaryKey val logId: String = UUID.randomUUID().toString(),
    val medicationId: String,
    val userId: String,
    val scheduledTime: Long,             // epoch millis
    val takenAt: Long? = null,
    val status: String,                  // "taken", "skipped", "pending"
    val skipReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)