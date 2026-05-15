package com.example.karunada_kala.ui.reviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.karunada_kala.domain.model.Review
import com.example.karunada_kala.ui.auth.AuthViewModel

private val KRed = Color(0xFFC1272D)
private val KYellow = Color(0xFFF9E106)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    artisanId: String,
    artisanName: String = "this artisan",
    viewModel: ReviewsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val reviews by viewModel.reviews.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isStudio = userRole == "STUDIO"

    var showWriteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(artisanId) {
        viewModel.loadReviewsForArtisan(artisanId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KRed,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (!isStudio) {
                FloatingActionButton(
                    onClick = { showWriteDialog = true },
                    containerColor = KRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Write Review")
                }
            }
        }
    ) { paddingValues ->

        if (reviews.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⭐", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No reviews yet", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    if (!isStudio) {
                        Spacer(Modifier.height(8.dp))
                        Text("Be the first to review!", fontSize = 13.sp, color = Color.LightGray)
                    }
                }
            }
        } else {
            // Summary bar
            val avg = if (reviews.isEmpty()) 0f else reviews.map { it.rating }.average().toFloat()
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Rating summary
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "%.1f".format(avg),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = KRed
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            StarRow(avg)
                            Text("${reviews.size} review${if (reviews.size != 1) "s" else ""}", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reviews, key = { it.id }) { review ->
                        ReviewCard(
                            review = review,
                            isStudio = isStudio,
                            isOwnReview = review.userId == currentUser?.id,
                            onReply = { replyText ->
                                viewModel.replyToReview(review.id, replyText)
                            }
                        )
                    }
                }
            }
        }

        if (isSaving) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = KRed) }
        }
    }

    if (showWriteDialog) {
        WriteReviewDialog(
            artisanName = artisanName,
            onDismiss = { showWriteDialog = false },
            onSubmit = { rating, comment ->
                viewModel.submitReview(artisanId, rating, comment)
                showWriteDialog = false
            }
        )
    }
}

// ── Review Card ───────────────────────────────────────────────────────────────

@Composable
fun ReviewCard(
    review: Review,
    isStudio: Boolean,
    isOwnReview: Boolean,
    onReply: (String) -> Unit
) {
    var showReplyField by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    val hasReply = !review.artisanReply.isNullOrBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(KRed, shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        review.userName.firstOrNull()?.toString() ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(review.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    StarRow(review.rating, size = 14.dp)
                }
                if (review.isVerifiedBuyer) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "✅ Verified",
                            color = Color(0xFF388E3C),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(review.comment, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

            // Artisan reply
            if (hasReply) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = KRed.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "🎭 Artisan's Response",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = KRed
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            review.artisanReply ?: "",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Artisan reply input
            if (isStudio && !hasReply) {
                Spacer(Modifier.height(10.dp))
                AnimatedVisibility(
                    visible = showReplyField,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            label = { Text("Reply as Artisan...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onReply(replyText)
                                    replyText = ""
                                    showReplyField = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KRed),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Post Reply", color = Color.White)
                        }
                    }
                }

                if (!showReplyField) {
                    TextButton(onClick = { showReplyField = true }) {
                        Text("Reply to this review", color = KRed, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ── Write Review Dialog ───────────────────────────────────────────────────────

@Composable
fun WriteReviewDialog(
    artisanName: String,
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    var rating by remember { mutableFloatStateOf(0f) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Write a Review", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("For: $artisanName", fontSize = 13.sp, color = Color.Gray)

                // Star selector
                Text("Your Rating", fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star.toFloat() }) {
                            Icon(
                                if (star <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = "$star stars",
                                tint = if (star <= rating) KYellow else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your review...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (rating > 0 && comment.isNotBlank()) onSubmit(rating, comment)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KRed),
                        enabled = rating > 0 && comment.isNotBlank()
                    ) { Text("Submit", color = Color.White) }
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
fun StarRow(rating: Float, size: androidx.compose.ui.unit.Dp = 16.dp) {
    Row {
        (1..5).forEach { star ->
            Icon(
                if (star <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = null,
                tint = KYellow,
                modifier = Modifier.size(size)
            )
        }
    }
}
