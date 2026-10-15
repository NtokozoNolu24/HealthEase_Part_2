package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "health_checks")
data class HealthCheckEntity(
    @PrimaryKey val checkId: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: String,                    // "blood_pressure", "blood_sugar", "weight"
    val value1: Double,                  // systolic / glucose / weight
    val value2: Double? = null,          // diastolic (for BP)
    val unit: String,                    // "mmHg", "mmol/L", "kg"
    val note: String? = null,
    val recordedAt: Long = System.currentTimeMillis()
)