package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val appointmentId: String = UUID.randomUUID().toString(),
    val userId: String,
    val doctorName: String,
    val clinicName: String? = null,
    val specialty: String? = null,
    val appointmentDate: Long,           // epoch millis
    val address: String? = null,
    val notes: String? = null,
    val reminderBufferMinutes: Int = 60,
    val status: String = "scheduled",    // scheduled, attended, missed, cancelled
    val prepChecklist: String = "",      // REM-006: comma-separated items
    val createdAt: Long = System.currentTimeMillis()
)