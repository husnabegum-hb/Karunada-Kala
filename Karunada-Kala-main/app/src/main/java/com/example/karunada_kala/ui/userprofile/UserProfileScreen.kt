package com.example.karunada_kala.ui.userprofile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.karunada_kala.ui.auth.AuthLoadState
import com.example.karunada_kala.ui.auth.AuthViewModel

val KarnatakaRed = Color(0xFFC1272D)
val KarnatakaYellow = Color(0xFFF9E106)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToBookings: () -> Unit = {},
    onNavigateToListings: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authLoadState by authViewModel.authLoadState.collectAsState()
    val isKannada by authViewModel.isKannadaMode.collectAsState()

    // Show a spinner while Firebase resolves the auth state.
    if (authLoadState is AuthLoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = KarnatakaRed)
        }
        return
    }

    // If explicitly unauthenticated, go to auth
    LaunchedEffect(authLoadState) {
        if (authLoadState is AuthLoadState.Unauthenticated) {
            onNavigateToAuth()
        }
    }

    val currentUser = (authLoadState as? AuthLoadState.Authenticated)?.user
    val isGuest = currentUser?.name == "Guest User"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = com.example.karunada_kala.R.string.my_profile),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = com.example.karunada_kala.R.string.back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = KarnatakaRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            // ── Hero Header ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(KarnatakaRed, KarnatakaRed.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(KarnatakaYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = currentUser?.profilePictureUrl,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "avatar"
                        ) { url ->
                            if (!url.isNullOrBlank()) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Guest avatar",
                                    modifier = Modifier.size(48.dp),
                                    tint = KarnatakaRed
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (isGuest) stringResource(id = com.example.karunada_kala.R.string.guest_user) else (currentUser?.name ?: ""),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!currentUser?.email.isNullOrBlank()) {
                        Text(
                            text = currentUser?.email ?: "",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                    // Role Badge
                    currentUser?.role?.let { role ->
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = KarnatakaYellow,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = if (role == "STUDIO") stringResource(id = com.example.karunada_kala.R.string.studio_artist_role) else stringResource(id = com.example.karunada_kala.R.string.explorer_role),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                                color = KarnatakaRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Guest CTA ────────────────────────────────────────────
            AnimatedVisibility(visible = isGuest) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = KarnatakaYellow.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, KarnatakaYellow),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = KarnatakaRed)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(id = com.example.karunada_kala.R.string.guest_browse_msg), fontWeight = FontWeight.Bold)
                            Text(stringResource(id = com.example.karunada_kala.R.string.sign_in_msg), fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Button(
                        onClick = onNavigateToAuth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KarnatakaRed)
                    ) {
                        Text(stringResource(id = com.example.karunada_kala.R.string.sign_in_register), color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Role-Based Section ───────────────────────────────────
            currentUser?.let { user ->
                ProfileSection(title = stringResource(id = com.example.karunada_kala.R.string.my_account)) {
                    if (user.role == "STUDIO") {
                        ProfileMenuItem(
                            icon = Icons.Default.Dashboard,
                            label = stringResource(id = com.example.karunada_kala.R.string.studio_dashboard),
                            tint = KarnatakaRed
                        ) { onNavigateToListings() }
                        ProfileMenuItem(
                            icon = Icons.Default.Verified,
                            label = stringResource(id = com.example.karunada_kala.R.string.verify_identity),
                            tint = Color(0xFF4CAF50)
                        ) {}
                    } else {
                        ProfileMenuItem(
                            icon = Icons.Default.EventNote,
                            label = stringResource(id = com.example.karunada_kala.R.string.my_bookings),
                            tint = KarnatakaRed
                        ) { onNavigateToBookings() }
                        ProfileMenuItem(
                            icon = Icons.Default.Map,
                            label = stringResource(id = com.example.karunada_kala.R.string.passport),
                            tint = Color(0xFF9C27B0),
                            trailing = {
                                // Progress bar for stamps
                                val stamps = user.districtStamps.size
                                val total = 31
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("$stamps/$total " + stringResource(id = com.example.karunada_kala.R.string.districts), fontSize = 11.sp, color = Color.Gray)
                                    LinearProgressIndicator(
                                        progress = { stamps / total.toFloat() },
                                        modifier = Modifier.width(80.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = KarnatakaYellow,
                                        trackColor = Color.LightGray
                                    )
                                }
                            }
                        ) {}
                    }
                        ProfileMenuItem(
                            icon = Icons.Default.Edit,
                            label = stringResource(id = com.example.karunada_kala.R.string.edit_profile),
                            tint = Color.Gray
                        ) {}
                }
            }

            // ── Settings Section ─────────────────────────────────────
            ProfileSection(title = stringResource(id = com.example.karunada_kala.R.string.settings_support)) {
                // Language toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = KarnatakaRed)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ಸಿರಿ-ಗನ್ನಡ / English",
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isKannada) stringResource(id = com.example.karunada_kala.R.string.kannada_mode_on) else stringResource(id = com.example.karunada_kala.R.string.kannada_mode_off),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = isKannada,
                        onCheckedChange = { authViewModel.setKannadaMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = KarnatakaRed
                        )
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(icon = Icons.Default.Gavel, label = stringResource(id = com.example.karunada_kala.R.string.terms_of_service), tint = Color.Gray) {}
                ProfileMenuItem(icon = Icons.Default.Support, label = stringResource(id = com.example.karunada_kala.R.string.contact_support), tint = Color.Gray) {}
            }

            // ── Logout ───────────────────────────────────────────────
            if (currentUser != null) {
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { authViewModel.logout() },
                    colors = CardDefaults.cardColors(containerColor = KarnatakaRed.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = KarnatakaRed)
                        Spacer(Modifier.width(16.dp))
                        val logoutText = if (isGuest) {
                            stringResource(id = com.example.karunada_kala.R.string.exit_guest_mode)
                        } else {
                            stringResource(id = com.example.karunada_kala.R.string.logout)
                        }
                        Text(
                            text = logoutText,
                            color = KarnatakaRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Reusable composables ─────────────────────────────────────────────────────

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        letterSpacing = 1.sp
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(content = content)
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
