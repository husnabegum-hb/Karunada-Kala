package com.example.karunada_kala.ui.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.karunada_kala.domain.model.Post
import com.example.karunada_kala.ui.components.ImageWithPlaceholder
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onNavigateToPostDetail: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.karunada_kala.R.string.feed)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(posts) { post ->
                PostItem(post = post, onClick = { onNavigateToPostDetail(post.id) })
            }
        }
    }
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = com.example.karunada_kala.R.string.artisan_profile), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f).clickable { /* Navigate to Profile */ })
            }
            ImageWithPlaceholder(
                model = post.imageUrl,
                contentDescription = "Post image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${post.likesCount} " + stringResource(id = com.example.karunada_kala.R.string.likes), style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = post.caption,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
            )
        }
    }
}
