package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val userId: String = UUID.randomUUID().toString(),
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val phoneNumber: String? = null,
    val languagePreference: String = "en",
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long? = null
)