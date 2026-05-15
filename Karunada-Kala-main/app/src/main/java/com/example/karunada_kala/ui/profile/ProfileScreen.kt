package com.example.karunada_kala.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.karunada_kala.domain.model.Event
import com.example.karunada_kala.domain.model.Post
import com.example.karunada_kala.domain.model.Review
import com.example.karunada_kala.ui.components.ImageWithPlaceholder

private val KRed = Color(0xFFC1272D)
private val KYellow = Color(0xFFF9E106)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToArtForm: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val artisan by viewModel.artisan.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val events by viewModel.events.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showQuestionDialog by remember { mutableStateOf(false) }
    var questionText by remember { mutableStateOf("") }

    // Show "Studio" tab only if this artisan has studio content
    val hasStudio = (artisan?.studioImages?.isNotEmpty() == true) || (artisan?.studioDescription?.isNotBlank() == true)
    val tabs = buildList {
        add(stringResource(id = com.example.karunada_kala.R.string.portfolio))
        add(stringResource(id = com.example.karunada_kala.R.string.schedule))
        add(stringResource(id = com.example.karunada_kala.R.string.reviews))
        add("Q&A")
        if (hasStudio) add(stringResource(id = com.example.karunada_kala.R.string.studio))
    }

    artisan?.let { data ->
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            this.data = Uri.parse("tel:${data.phone}")
                        }
                        context.startActivity(intent)
                    },
                    containerColor = KRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call Artisan")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ── Hero header ────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    ImageWithPlaceholder(
                        model = data.imageUrl,
                        contentDescription = data.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            data.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = KRed,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                data.type,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Ask Question Button
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clickable { showQuestionDialog = true },
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("❓", fontSize = 14.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                stringResource(id = com.example.karunada_kala.R.string.ask_question),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bio
                if (data.bio.isNotBlank()) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text(
                            data.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                // ── Tabs ───────────────────────────────────────────────────────
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = KRed
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 12.sp) },
                            selectedContentColor = KRed,
                            unselectedContentColor = Color.Gray
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (tabs.getOrNull(selectedTabIndex)) {
                        stringResource(id = com.example.karunada_kala.R.string.portfolio) -> PortfolioTab(posts)
                        stringResource(id = com.example.karunada_kala.R.string.schedule)  -> ScheduleTab(events) { viewModel.bookEvent(it) }
                        stringResource(id = com.example.karunada_kala.R.string.reviews)   -> ReviewsTab(reviews)
                        "Q&A" -> UserQuestionsTab(questions)
                        stringResource(id = com.example.karunada_kala.R.string.studio)    -> StudioShowcaseTab(
                            description = data.studioDescription,
                            images = data.studioImages
                        )
                    }
                }
            }
        }
        
        // Ask Question Dialog
        if (showQuestionDialog) {
            AlertDialog(
                onDismissRequest = { showQuestionDialog = false },
                title = { Text(stringResource(id = com.example.karunada_kala.R.string.ask_question)) },
                text = {
                    TextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        placeholder = { Text("How can I help you?") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (questionText.isNotBlank()) {
                            viewModel.askQuestion(questionText)
                            questionText = ""
                            showQuestionDialog = false
                        }
                    }) {
                        Text("Send", color = KRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showQuestionDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = KRed)
        }
    }
}

// ── Portfolio Tab ─────────────────────────────────────────────────────────────

@Composable
fun PortfolioTab(posts: List<Post>) {
    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No portfolio posts yet", color = Color.Gray)
        }
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(posts) { post ->
            ImageWithPlaceholder(
                model = post.imageUrl,
                contentDescription = "Post image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

// ── Schedule Tab ──────────────────────────────────────────────────────────────

@Composable
fun ScheduleTab(events: List<Event>, onBook: (String) -> Unit) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No upcoming events", color = Color.Gray)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(events) { event ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(event.date, fontSize = 13.sp, color = KRed)
                        Text(event.locationName, fontSize = 13.sp, color = Color.Gray)
                        if (event.description.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(event.description, fontSize = 13.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                        }
                    }
                    
                    Button(
                        onClick = { onBook(event.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = KRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Book", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ── Reviews Tab ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsTab(reviews: List<Review>) {
    if (reviews.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐", fontSize = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text("No reviews yet", color = Color.Gray)
            }
        }
        return
    }
    val avg = reviews.map { it.rating }.average().toFloat()
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Summary
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("%.1f".format(avg), fontSize = 38.sp, fontWeight = FontWeight.Bold, color = KRed)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Row {
                            repeat(5) { i ->
                                Icon(
                                    if (i < avg) Icons.Default.Star else Icons.Default.StarOutline,
                                    contentDescription = null,
                                    tint = KYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text("${reviews.size} review${if (reviews.size != 1) "s" else ""}", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
        items(reviews) { review ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(KRed, shape = RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                review.userName.firstOrNull()?.toString() ?: "?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(review.userName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row {
                                repeat(5) { i ->
                                    Icon(
                                        if (i < review.rating) Icons.Default.Star else Icons.Default.StarOutline,
                                        contentDescription = null,
                                        tint = KYellow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        if (review.isVerifiedBuyer) {
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(50)) {
                                Text(
                                    "✅ Verified",
                                    color = Color(0xFF388E3C),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(review.comment, fontSize = 14.sp)
                    // Artisan reply
                    if (!review.artisanReply.isNullOrBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            color = KRed.copy(alpha = 0.07f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(stringResource(id = com.example.karunada_kala.R.string.artisan_response), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = KRed)
                                Spacer(Modifier.height(3.dp))
                                Text(review.artisanReply ?: "", fontSize = 13.sp, fontStyle = FontStyle.Italic)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Studio Showcase Tab (public view) ─────────────────────────────────────────

@Composable
fun StudioShowcaseTab(description: String, images: List<String>) {
    var fullscreenImage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Description section
        if (description.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎭", fontSize = 22.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(id = com.example.karunada_kala.R.string.about_studio),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = KRed
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Photo gallery heading
        if (images.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "📸 " + stringResource(id = com.example.karunada_kala.R.string.studio_gallery),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${images.size} photo${if (images.size != 1) "s" else ""}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 800.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(images) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Studio photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { fullscreenImage = url }
                        )
                    }
                }
            }
        }

        // Empty state
        if (description.isBlank() && images.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎪", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Studio showcase coming soon!", color = Color.Gray)
                    }
                }
            }
        }
    }

    // Fullscreen image viewer
    fullscreenImage?.let { url ->
        Dialog(onDismissRequest = { fullscreenImage = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .clickable { fullscreenImage = null }
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "Full image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "✕ Close",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserQuestionsTab(questions: List<com.example.karunada_kala.domain.model.Question>) {
    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions asked yet", color = Color.Gray)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(questions) { q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Q: ${q.questionText}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    
                    if (!q.answerText.isNullOrBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Row {
                            Text("A: ", fontWeight = FontWeight.Bold, color = KRed)
                            Text(
                                text = q.answerText!!,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    } else {
                        Spacer(Modifier.height(4.dp))
                        Text("Waiting for artisan's response...", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
