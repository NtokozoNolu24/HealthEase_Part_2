package com.example.healthease.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.ArticleEntity
import com.example.healthease.data.repository.ArticleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ArticleDetailUiState(
    val article: ArticleEntity? = null,
    val isBookmarked: Boolean = false
)

class ArticleDetailViewModel(
    private val repo: ArticleRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(ArticleDetailUiState())
    val state: StateFlow<ArticleDetailUiState> = _state.asStateFlow()

    fun load(articleId: String) {
        viewModelScope.launch {
            _state.update { it.copy(article = repo.getById(articleId)) }
        }
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                repo.isBookmarked(uid, articleId).collect { marked ->
                    _state.update { it.copy(isBookmarked = marked) }
                }
            }
        }
    }

    fun toggleBookmark() {
        val uid = app.currentUserId ?: return
        val id = _state.value.article?.articleId ?: return
        viewModelScope.launch { repo.toggleBookmark(uid, id) }
    }

    companion object {
        fun factory(app: HealthEaseApplication) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ArticleDetailViewModel(app.articleRepository, app) as T
            }
        }
    }
}