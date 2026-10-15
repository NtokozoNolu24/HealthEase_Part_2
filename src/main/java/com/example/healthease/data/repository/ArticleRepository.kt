package com.example.healthease.data.repository

import com.example.healthease.data.local.ArticleDao
import com.example.healthease.data.local.ArticleEntity
import com.example.healthease.data.local.BookmarkDao
import com.example.healthease.data.local.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleRepository(
    private val articleDao: ArticleDao,
    private val bookmarkDao: BookmarkDao
) {

    fun observeAll(): Flow<List<ArticleEntity>> = articleDao.observeAll()
    fun observeByCategory(category: String) = articleDao.observeByCategory(category)
    fun observeCategories(): Flow<List<String>> = articleDao.observeCategories()
    fun search(q: String) = articleDao.search(q)
    suspend fun getById(id: String) = articleDao.getById(id)

    fun observeBookmarks(userId: String): Flow<List<BookmarkEntity>> =
        bookmarkDao.observeForUser(userId)

    fun isBookmarked(userId: String, articleId: String): Flow<Boolean> =
        bookmarkDao.isBookmarked(userId, articleId)

    suspend fun toggleBookmark(userId: String, articleId: String) {
        if (bookmarkDao.isBookmarkedOnce(userId, articleId)) {
            bookmarkDao.remove(userId, articleId)
        } else {
            bookmarkDao.add(BookmarkEntity(userId = userId, articleId = articleId))
        }
    }

    /** ART-002: simple personalized "recommended" — most recent from top-priority categories */
    fun observeRecommended(): Flow<List<ArticleEntity>> =
        articleDao.observeAll().map { list ->
            list.sortedWith(
                compareByDescending<ArticleEntity> { it.category in listOf("Chronic Conditions", "Nutrition", "Mental Health") }
                    .thenByDescending { it.publishedDate }
            ).take(5)
        }
}