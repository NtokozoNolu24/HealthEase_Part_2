package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_articles")
data class ArticleEntity(
    @PrimaryKey val articleId: String,
    val title: String,
    val summary: String,
    val content: String,            // full text
    val category: String,           // Nutrition, Fitness, Mental Health, etc.
    val subCategory: String? = null,
    val language: String = "en",    // en / zu / af
    val author: String = "HealthEase Team",
    val source: String = "WHO / SA DoH",  // ART-004
    val imageEmoji: String = "📰",  // emoji stand-in for image
    val tags: String = "",          // comma-separated
    val readMinutes: Int = 3,
    val publishedDate: Long = System.currentTimeMillis()
)