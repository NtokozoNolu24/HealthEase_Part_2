package com.example.healthease.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND articleId = :articleId")
    suspend fun remove(userId: String, articleId: String)

    @Query("SELECT * FROM bookmarks WHERE userId = :userId ORDER BY savedAt DESC")
    fun observeForUser(userId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND articleId = :articleId)")
    fun isBookmarked(userId: String, articleId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND articleId = :articleId)")
    suspend fun isBookmarkedOnce(userId: String, articleId: String): Boolean
}