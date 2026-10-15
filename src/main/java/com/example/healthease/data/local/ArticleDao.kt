package com.example.healthease.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM health_articles ORDER BY publishedDate DESC")
    fun observeAll(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM health_articles WHERE category = :category ORDER BY publishedDate DESC")
    fun observeByCategory(category: String): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM health_articles WHERE articleId = :id LIMIT 1")
    suspend fun getById(id: String): ArticleEntity?

    @Query("SELECT DISTINCT category FROM health_articles")
    fun observeCategories(): Flow<List<String>>

    @Query("SELECT * FROM health_articles WHERE title LIKE '%' || :q || '%' OR summary LIKE '%' || :q || '%' ORDER BY publishedDate DESC")
    fun search(q: String): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("SELECT COUNT(*) FROM health_articles")
    suspend fun count(): Int
}