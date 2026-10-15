package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val notificationId: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: String,                    // "reminder", "tip", "system", "sync"
    val title: String,
    val body: String,
    val isRead: Boolean = false,
    val actionTarget: String? = null,    // route to navigate to on tap
    val createdAt: Long = System.currentTimeMillis()
)