package com.example.healthease.ui.screens.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.ArticleEntity
import com.example.healthease.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesFeedScreen(
    onOpenArticle: (String) -> Unit,
    onOpenBookmarks: () -> Unit,
    viewModel: ArticleViewModel = viewModel(
        factory = ArticleViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filtered = viewModel.filtered()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📰 Health Articles", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(onClick = onOpenBookmarks) {
                        Text("🔖", fontSize = 22.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccentTeal)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(AccentTeal.copy(alpha = 0.05f), SecondaryYellow.copy(alpha = 0.08f))
                    )
                )
        ) {
            // Search bar
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                placeholder = { Text("Search articles…") },
                leadingIcon = { Text("🔍", fontSize = 20.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Category chips (ART-001)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) },
                    label = { Text("All") }
                )
                state.categories.forEach { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Recommended section (ART-002)
                if (state.selectedCategory == null && state.query.isBlank() && state.recommended.isNotEmpty()) {
                    item {
                        Text(
                            "⭐ Recommended for you",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AccentTeal
                        )
                    }
                    items(state.recommended, key = { "rec_${it.articleId}" }) { a ->
                        ArticleCard(a) { onOpenArticle(a.articleId) }
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "📚 All articles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
                items(filtered, key = { it.articleId }) { a ->
                    ArticleCard(a) { onOpenArticle(a.articleId) }
                }
                item { Spacer(Modifier.height(60.dp)) }
            }
        }
    }
}

@Composable
private fun ArticleCard(a: ArticleEntity, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(AccentTeal.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(a.imageEmoji, fontSize = 32.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(a.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(
                    a.summary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(a.category, fontSize = 11.sp, color = AccentTeal, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Text("• ${a.readMinutes} min read", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}