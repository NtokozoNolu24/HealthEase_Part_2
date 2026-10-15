package com.example.healthease.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun formatDate(millis: Long): String = dateFmt.format(Date(millis))
    fun formatDateTime(millis: Long): String = dateTimeFmt.format(Date(millis))
    fun todayIso(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}