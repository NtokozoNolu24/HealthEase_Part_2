package com.example.healthease.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.ArticleEntity
import com.example.healthease.data.repository.ArticleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ArticlesUiState(
    val articles: List<ArticleEntity> = emptyList(),
    val recommended: List<ArticleEntity> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val query: String = ""
)

class ArticleViewModel(
    private val repo: ArticleRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(ArticlesUiState())
    val state: StateFlow<ArticlesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _state.update { it.copy(articles = list) }
            }
        }
        viewModelScope.launch {
            repo.observeRecommended().collect { list ->
                _state.update { it.copy(recommended = list) }
            }
        }
        viewModelScope.launch {
            repo.observeCategories().collect { cats ->
                _state.update { it.copy(categories = cats) }
            }
        }
    }

    fun selectCategory(cat: String?) = _state.update {
        it.copy(selectedCategory = cat, query = "")
    }

    fun setQuery(q: String) = _state.update { it.copy(query = q) }

    fun filtered(): List<ArticleEntity> {
        val s = _state.value
        return when {
            s.query.isNotBlank() ->
                s.articles.filter {
                    it.title.contains(s.query, true) || it.summary.contains(s.query, true)
                }
            s.selectedCategory != null ->
                s.articles.filter { it.category == s.selectedCategory }
            else -> s.articles
        }
    }

    fun toggleBookmark(articleId: String) {
        val uid = app.currentUserId ?: return
        viewModelScope.launch { repo.toggleBookmark(uid, articleId) }
    }

    fun isBookmarked(articleId: String): Flow<Boolean> {
        val uid = app.currentUserId
        return if (uid == null) flowOf(false) else repo.isBookmarked(uid, articleId)
    }

    fun bookmarks(): Flow<List<ArticleEntity>> {
        val uid = app.currentUserId ?: return flowOf(emptyList())
        return repo.observeBookmarks(uid).map { marks ->
            val ids = marks.map { it.articleId }.toSet()
            repo.observeAll().first().filter { it.articleId in ids }
        }
    }

    companion object {
        fun factory(app: HealthEaseApplication) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ArticleViewModel(app.articleRepository, app) as T
            }
        }
    }
}