package com.example.healthease.data.repository

import com.example.healthease.data.local.UserDao
import com.example.healthease.data.local.UserEntity
import com.example.healthease.data.security.PasswordHasher

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class UserRepository(private val userDao: UserDao) {

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String?
    ): AuthResult {
        if (userDao.emailExists(email.lowercase().trim()) > 0) {
            return AuthResult.Error("An account with this email already exists 📧")
        }
        val user = UserEntity(
            email = email.lowercase().trim(),
            passwordHash = PasswordHasher.hash(password),
            fullName = fullName.trim(),
            phoneNumber = phone?.trim()?.ifBlank { null },
            isVerified = true // Email verification mocked for now
        )
        return try {
            userDao.insertUser(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed 😔")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        val user = userDao.getUserByEmail(email.lowercase().trim())
            ?: return AuthResult.Error("No account found with this email 🔍")

        if (!PasswordHasher.verify(password, user.passwordHash)) {
            return AuthResult.Error("Incorrect password 🔒")
        }
        userDao.updateLastLogin(user.userId, System.currentTimeMillis())
        return AuthResult.Success(user)
    }
}