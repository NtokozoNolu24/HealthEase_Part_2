package com.example.healthease.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    primaryKeys = ["userId", "articleId"]
)
data class BookmarkEntity(
    val userId: String,
    val articleId: String,
    val savedAt: Long = System.currentTimeMillis()
)