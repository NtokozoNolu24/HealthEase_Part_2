package com.example.healthease.util

object Validation {

    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    // SA mobile: +27 followed by 9 digits (or 0 + 9 digits)
    private val SA_PHONE_REGEX = Regex("^(\\+27|0)[6-8][0-9]{8}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUpper && hasDigit
    }

    fun isValidSaPhone(phone: String): Boolean = SA_PHONE_REGEX.matches(phone.trim())

    fun passwordError(password: String): String? = when {
        password.length < 8 -> "Password must be at least 8 characters 🔐"
        !password.any { it.isUpperCase() } -> "Add at least 1 uppercase letter 🔠"
        !password.any { it.isDigit() } -> "Add at least 1 number 🔢"
        else -> null
    }
}