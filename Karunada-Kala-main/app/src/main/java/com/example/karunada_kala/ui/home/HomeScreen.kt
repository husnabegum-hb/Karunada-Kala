package com.example.karunada_kala.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.karunada_kala.domain.model.Artisan
import com.example.karunada_kala.domain.model.Event
import com.example.karunada_kala.domain.model.Review
import com.example.karunada_kala.domain.model.User
import com.example.karunada_kala.ui.components.ShimmerCardLoading
import com.example.karunada_kala.ui.components.ImageWithPlaceholder

private val KRed = Color(0xFFC1272D)
private val KYellow = Color(0xFFF9E106)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userRole: String?,
    currentUser: User?,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToArtForm: (String) -> Unit,
    onNavigateToUserProfile: () -> Unit,
    onNavigateToListings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: com.example.karunada_kala.ui.auth.AuthViewModel = hiltViewModel()
) {
    val isKannada by authViewModel.isKannadaMode.collectAsState()
    
    if (userRole == "STUDIO") {
        StudioDashboardScreen(
            currentUser = currentUser,
            onNavigateToUserProfile = onNavigateToUserProfile,
            onNavigateToListings = onNavigateToListings,
            isKannada = isKannada
        )
    } else {
        ArtExplorerScreen(
            viewModel, 
            onNavigateToProfile, 
            onNavigateToArtForm, 
            currentUser, 
            onNavigateToUserProfile,
            isKannada = isKannada
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioDashboardScreen(
    currentUser: User? = null,
    onNavigateToUserProfile: () -> Unit = {},
    onNavigateToListings: () -> Unit = {},
    isKannada: Boolean = false,
    dashboardViewModel: StudioDashboardViewModel = hiltViewModel()
) {
    val myEvents by dashboardViewModel.myEvents.collectAsState()
    val myReviews by dashboardViewModel.myReviews.collectAsState()
    val artisan by dashboardViewModel.artisan.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.karunada_kala.R.string.studio_dashboard), fontWeight = FontWeight.Bold) },
                actions = {
                    ProfileAvatarButton(currentUser = currentUser, onClick = onNavigateToUserProfile)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KRed,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToListings,
                containerColor = KRed,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(id = com.example.karunada_kala.R.string.my_listings)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Location Warning ────────────────────────────────────────
            if (artisan != null && artisan!!.lat == 0.0 && artisan!!.lng == 0.0) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOff, contentDescription = null, tint = Color(0xFFE65100))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (isKannada) "ಸ್ಥಳವನ್ನು ಹೊಂದಿಸಲಾಗಿಲ್ಲ" else "Location Not Set",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    if (isKannada) "ನಿಮ್ಮ ಸ್ಟುಡಿಯೋ ನಕ್ಷೆಯಲ್ಲಿ ಕಾಣಿಸಿಕೊಳ್ಳಲು ದಯವಿಟ್ಟು ಸ್ಥಳವನ್ನು ಸೇರಿಸಿ." else "Please set your studio location to appear on the map.",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                            TextButton(onClick = onNavigateToListings) {
                                Text(if (isKannada) "ಹೊಂದಿಸಿ" else "SET NOW", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ── Stats row ──────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(Modifier.weight(1f), stringResource(id = com.example.karunada_kala.R.string.my_listings), myEvents.size.toString(), KRed)
                    val avg = if (myReviews.isEmpty()) 0f
                              else myReviews.map { it.rating }.average().toFloat()
                    StatCard(Modifier.weight(1f), if (isKannada) "ಸರಾಸರಿ ರೇಟಿಂಗ್" else "Avg Rating", "%.1f ⭐".format(avg), Color(0xFFF57F17))
                    StatCard(Modifier.weight(1f), stringResource(id = com.example.karunada_kala.R.string.reviews), myReviews.size.toString(), Color(0xFF4CAF50))
                }
            }

            // ── Upcoming Events ────────────────────────────────────────
            item {
                SectionHeader(stringResource(id = com.example.karunada_kala.R.string.my_bookings), isKannada) { onNavigateToListings() }
            }
            val upcoming = myEvents.filter { it.status == "Upcoming" }
            if (upcoming.isEmpty()) {
                item {
                    EmptyCard(if (isKannada) "ಯಾವುದೇ ಮುಂಬರುವ ಕಾರ್ಯಕ್ರಮಗಳಿಲ್ಲ. ಕಾರ್ಯಕ್ರಮವನ್ನು ರಚಿಸಲು '+ ಹೊಸ ಪಟ್ಟಿ' ಟ್ಯಾಪ್ ಮಾಡಿ." else "No upcoming events. Tap '+ New Listing' to create one.")
                }
            } else {
                items(upcoming.take(3), key = { it.id }) { event ->
                    DashboardEventCard(event)
                }
            }

            // ── Recent Reviews ─────────────────────────────────────────
            item {
                SectionHeader(stringResource(id = com.example.karunada_kala.R.string.reviews), isKannada, null)
            }
            if (myReviews.isEmpty()) {
                item {
                    EmptyCard(if (isKannada) "ಇನ್ನೂ ಯಾವುದೇ ವಿಮರ್ಶೆಗಳಿಲ್ಲ. ವಿಮರ್ಶೆಗಳನ್ನು ಪಡೆಯಲು ನಿಮ್ಮ ಪಟ್ಟಿಗಳನ್ನು ಹಂಚಿಕೊಳ್ಳಿ!" else "No reviews yet. Share your listings to earn your first review!")
                }
            } else {
                items(myReviews.take(5), key = { it.id }) { review ->
                    DashboardReviewCard(review, isKannada)
                }
            }
        }
    }
}

// ── Dashboard helper composables ──────────────────────────────────────────────

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun SectionHeader(title: String, isKannada: Boolean, onSeeAll: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) { Text(if (isKannada) "ಎಲ್ಲವನ್ನೂ ನೋಡಿ" else "See All", color = KRed, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun EmptyCard(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            message,
            modifier = Modifier.padding(16.dp),
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun DashboardEventCard(event: Event) {
    val statusColor = if (event.status == "Upcoming") Color(0xFF4CAF50) else Color.Gray
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.verticalGradient(listOf(KRed, KRed.copy(alpha = 0.6f))),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🎭", fontSize = 20.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(event.date, fontSize = 12.sp, color = Color.Gray)
                Text(event.locationName, fontSize = 12.sp, color = Color.Gray)
            }
            Surface(
                color = statusColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    event.status,
                    color = statusColor,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DashboardReviewCard(review: Review, isKannada: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                if (!review.artisanReply.isNullOrBlank()) {
                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(50)) {
                        Text(
                            if (isKannada) "ಪ್ರತಿಕ್ರಿಯಿಸಲಾಗಿದೆ" else "Replied",
                            color = Color(0xFF388E3C),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(review.comment, fontSize = 13.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurface.copy(0.8f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtExplorerScreen(
    viewModel: HomeViewModel,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToArtForm: (String) -> Unit,
    currentUser: User? = null,
    onNavigateToUserProfile: () -> Unit = {},
    isKannada: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.karunada_kala.R.string.discover_arts)) },
                actions = {
                    ProfileAvatarButton(currentUser = currentUser, onClick = onNavigateToUserProfile)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text(stringResource(id = com.example.karunada_kala.R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(5) { ShimmerCardLoading() }
                    }
                }
                is HomeUiState.Success -> {
                    val featured = state.artisans.take(3)
                    val rest = state.artisans

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (searchQuery.isBlank()) {
                            item {
                                Text(
                                    text = stringResource(id = com.example.karunada_kala.R.string.featured_heritage),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(featured) { artisan ->
                                        HeroCard(artisan = artisan) {
                                            onNavigateToProfile(artisan.id)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = stringResource(id = com.example.karunada_kala.R.string.all_artisans),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)
                            )
                        }

                        items(rest) { artisan ->
                            ArtisanListItem(artisan = artisan) {
                                onNavigateToProfile(artisan.id)
                            }
                        }
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun HeroCard(artisan: Artisan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ImageWithPlaceholder(
                model = artisan.imageUrl,
                contentDescription = artisan.name,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = artisan.type,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = artisan.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}

@Composable
fun ArtisanListItem(artisan: Artisan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageWithPlaceholder(
                model = artisan.imageUrl,
                contentDescription = artisan.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = artisan.name, style = MaterialTheme.typography.titleMedium)
                Text(text = artisan.type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = artisan.bio,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ProfileAvatarButton(currentUser: User?, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        val photoUrl = currentUser?.profilePictureUrl
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "My profile",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "My profile",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
