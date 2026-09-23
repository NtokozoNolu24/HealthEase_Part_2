package com.example.healthease.ui.screens.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.ArticleEntity
import com.example.healthease.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onBack: () -> Unit,
    onOpenArticle: (String) -> Unit,
    viewModel: ArticleViewModel = viewModel(
        factory = ArticleViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val ctx = LocalContext.current
    val bookmarked by viewModel.bookmarks().collectAsState(initial = emptyList<ArticleEntity>())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔖 Bookmarks", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
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
            if (bookmarked.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔖", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No bookmarks yet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tap the bookmark icon on any article to save it here for offline reading.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookmarked, key = { it.articleId }) { a ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenArticle(a.articleId) }
                        ) {
                            Row(modifier = Modifier.padding(14.dp)) {
                                Text(a.imageEmoji, fontSize = 32.sp)
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}