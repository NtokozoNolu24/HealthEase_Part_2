package com.example.healthease.ui.screens.articles

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.healthease.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBack: () -> Unit,
    viewModel: ArticleDetailViewModel = viewModel(
        factory = ArticleDetailViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    LaunchedEffect(articleId) { viewModel.load(articleId) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val article = state.article
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Text(
                            if (state.isBookmarked) "🔖" else "📑",
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = {
                        article?.let {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, it.title)
                                putExtra(Intent.EXTRA_TEXT, "${it.title}\n\n${it.summary}\n\n— Shared from HealthEase 💚")
                            }
                            ctx.startActivity(Intent.createChooser(share, "Share article"))
                        }
                    }) {
                        Text("📤", fontSize = 22.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccentTeal)
            )
        }
    ) { padding ->
        if (article == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentTeal)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(AccentTeal.copy(alpha = 0.05f), SecondaryYellow.copy(alpha = 0.08f))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(AccentTeal.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(article.imageEmoji, fontSize = 72.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(article.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(article.category, fontSize = 12.sp, color = AccentTeal, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Text("• ${article.readMinutes} min read", fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "${article.author} • ${article.source}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                article.content,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SecondaryYellow.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("⚠️", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "This article is educational. Always consult a healthcare professional for personal advice.",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}