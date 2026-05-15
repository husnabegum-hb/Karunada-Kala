package com.example.karunada_kala.ui.listings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.karunada_kala.domain.model.Event
import com.example.karunada_kala.domain.model.Question
import com.example.karunada_kala.ui.auth.AuthViewModel
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Image

private val KRed = Color(0xFFC1272D)
private val KYellow = Color(0xFFF9E106)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    viewModel: ListingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val myEvents by viewModel.myEvents.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showBioDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(id = com.example.karunada_kala.R.string.my_listings),
        stringResource(id = com.example.karunada_kala.R.string.studio_showcase),
        "Questions"
    )
    val studioDescription by viewModel.studioDescription.collectAsState()
    val studioImages by viewModel.studioImages.collectAsState()
    val artisanLat by viewModel.artisanLat.collectAsState()
    val artisanLng by viewModel.artisanLng.collectAsState()
    val artisanName by viewModel.artisanName.collectAsState()
    val artisanType by viewModel.artisanType.collectAsState()
    val artisanImageUrl by viewModel.artisanImageUrl.collectAsState()
    val studioSaveSuccess by viewModel.studioSaveSuccess.collectAsState()

    // Snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("Listing created! ✅")
            viewModel.resetSaveSuccess()
        }
    }
    LaunchedEffect(studioSaveSuccess) {
        if (studioSaveSuccess) {
            snackbarHostState.showSnackbar("Studio showcase updated! 🎭")
            viewModel.resetStudioSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.karunada_kala.R.string.my_studio), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KRed,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showBioDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Studio Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = KRed,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(stringResource(id = com.example.karunada_kala.R.string.new_workshop)) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Studio hero banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.horizontalGradient(listOf(KRed, Color(0xFF8B0000)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎭 ${currentUser?.name ?: "Studio Artist"}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Surface(
                        color = KYellow,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            "Studio Account",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            color = KRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tab row
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                        selectedContentColor = KRed,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            when (selectedTab) {
                0 -> ListingsTab(myEvents)
                1 -> StudioShowcaseEditorTab(
                    description = studioDescription,
                    existingImageUrls = studioImages,
                    initialLat = artisanLat,
                    initialLng = artisanLng,
                    initialName = artisanName,
                    initialType = artisanType,
                    initialProfileImage = artisanImageUrl,
                    isSaving = isSaving,
                    onSave = { desc, newUris, lat, lng, name, type, profileImg ->
                        viewModel.saveStudioProfile(desc, newUris, studioImages, lat, lng, name, type, profileImg)
                    }
                )
                2 -> QuestionsTab(viewModel)
            }
        }
    }

    if (isSaving) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = KRed)
        }
    }

    if (showCreateDialog) {
        CreateListingDialog(
            onDismiss = { showCreateDialog = false },
            onSubmit = { title, desc, date, location, artFormId, imageUri ->
                viewModel.createListing(title, desc, date, location, artFormId, imageUri)
                showCreateDialog = false
            }
        )
    }

    if (showBioDialog) {
        EditBioDialog(
            onDismiss = { showBioDialog = false },
            onSubmit = { bio ->
                viewModel.updateBio(bio)
                showBioDialog = false
            }
        )
    }
}

// ── Tab 1: My Listings ────────────────────────────────────────────────────────

@Composable
private fun ListingsTab(events: List<Event>) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎪", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "No workshops yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Text(
                    "Tap '+ New Workshop' to create your first listing",
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events, key = { it.id }) { event ->
                StudioEventCard(event)
            }
        }
    }
}

@Composable
private fun StudioEventCard(event: Event) {
    val statusColor = when (event.status) {
        "Upcoming" -> Color(0xFF4CAF50)
        "Past" -> Color.Gray
        else -> KRed
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(event.date, fontSize = 13.sp, color = Color.Gray)
                    Text(event.locationName, fontSize = 13.sp, color = Color.Gray)
                }
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        event.status,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            if (event.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(event.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), maxLines = 2)
            }
            
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(Modifier.height(10.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp), tint = KRed)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${event.bookingsCount} people booked",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ── Tab 2: Studio Showcase Editor ─────────────────────────────────────────────

@Composable
private fun StudioShowcaseEditorTab(
    description: String,
    existingImageUrls: List<String>,
    initialLat: Double,
    initialLng: Double,
    initialName: String,
    initialType: String,
    initialProfileImage: String,
    isSaving: Boolean,
    onSave: (String, List<String>, Double, Double, String, String, String?) -> Unit
) {
    // Local draft state — pre-filled from current saved values
    var draftDescription by remember(description) { mutableStateOf(description) }
    var draftLat by remember(initialLat) { mutableStateOf(initialLat.toString()) }
    var draftLng by remember(initialLng) { mutableStateOf(initialLng.toString()) }
    var draftName by remember(initialName) { mutableStateOf(initialName) }
    var draftType by remember(initialType) { mutableStateOf(initialType) }
    var draftProfileImage by remember(initialProfileImage) { mutableStateOf(initialProfileImage) }
    var newProfileImageUri by remember { mutableStateOf<String?>(null) }
    
    // New URIs picked from gallery (not yet uploaded)
    val newLocalUris = remember { mutableStateListOf<String>() }

    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        draftLat = location.latitude.toString()
                        draftLng = location.longitude.toString()
                    } else {
                        Toast.makeText(context, "Location not found. Ensure GPS is on.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) {
                // Ignore since permission was just checked
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val profileImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            newProfileImageUri = it.toString()
            draftProfileImage = it.toString()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { newLocalUris.add(it.toString()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section title
        // ── Basic Info ────────────────────────────────────────────────────────
        Text("Basic Information", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KRed)
        
        OutlinedTextField(
            value = draftName,
            onValueChange = { draftName = it },
            label = { Text("Artisan / Studio Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = draftType,
            onValueChange = { draftType = it },
            label = { Text("Art Form (Kala) - e.g. Wood Carver") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )

        Text("Profile Banner Image", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray.copy(alpha = 0.1f))
                .clickable { profileImagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (draftProfileImage.isNotBlank()) {
                AsyncImage(
                    model = draftProfileImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.White)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray)
                    Text("Select Banner Image", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        HorizontalDivider()

        Text(
            "Studio Showcase",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = KRed
        )
        Text(
            "Add photos and a description of your studio. Users will see this when they visit your profile.",
            fontSize = 13.sp,
            color = Color.Gray,
            lineHeight = 18.sp
        )

        HorizontalDivider()

        // ── Description ────────────────────────────────────────────────────────
        Text("Studio Description", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = draftDescription,
            onValueChange = { draftDescription = it },
            label = { Text("Tell visitors about your art, history, and techniques...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
            maxLines = 10,
            shape = RoundedCornerShape(12.dp)
        )

        // ── Location ────────────────────────────────────────────────────────
        Text("Studio Location (Coordinates)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = draftLat,
                onValueChange = { draftLat = it },
                label = { Text("Latitude") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = draftLng,
                onValueChange = { draftLng = it },
                label = { Text("Longitude") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
        
        OutlinedButton(
            onClick = {
                val hasPermission = ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            draftLat = location.latitude.toString()
                            draftLng = location.longitude.toString()
                        } else {
                            Toast.makeText(context, "Location not found. Ensure GPS is on.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Use Current Location")
        }

        // ── Photos ─────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Studio Photos", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            val totalCount = existingImageUrls.size + newLocalUris.size
            if (totalCount > 0) {
                Text("$totalCount photo${if (totalCount != 1) "s" else ""}", fontSize = 12.sp, color = Color.Gray)
            }
        }

        // Existing saved images grid
        if (existingImageUrls.isNotEmpty()) {
            Text("Saved", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(existingImageUrls) { url ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Studio image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Saved indicator
                        Surface(
                            modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "Saved",
                                color = Color.White,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Newly picked (pending upload) images
        if (newLocalUris.isNotEmpty()) {
            Text("New (will upload on save)", fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(newLocalUris.toList()) { uri ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "New photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Delete button
                        IconButton(
                            onClick = { newLocalUris.remove(uri) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.55f), shape = RoundedCornerShape(50))
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Add photos button
        OutlinedButton(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = KRed),
            border = BorderStroke(1.dp, KRed)
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add Studio Photos")
        }

        // Save button
        Button(
            onClick = {
                val lat = draftLat.toDoubleOrNull() ?: 0.0
                val lng = draftLng.toDoubleOrNull() ?: 0.0
                onSave(draftDescription, newLocalUris.toList(), lat, lng, draftName, draftType, newProfileImageUri)
                newLocalUris.clear()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KRed),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Saving...", color = Color.White)
            } else {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Showcase", color = Color.White)
            }
        }

        Spacer(Modifier.height(80.dp)) // bottom padding for FAB
    }
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

@Composable
private fun CreateListingDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var artFormId by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("New Workshop / Event", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                // Image picker section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clickable { imagePicker.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                            )
                            // Re-pick overlay
                            Surface(
                                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    "Change",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = KRed, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(6.dp))
                                Text("Tap to add event image", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = date, onValueChange = { date = it },
                    label = { Text("Date (e.g. Dec 20, 2025) *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = location, onValueChange = { location = it },
                    label = { Text("Location / Venue *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = artFormId, onValueChange = { artFormId = it },
                    label = { Text("Art Form (e.g. Yakshagana)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && date.isNotBlank() && location.isNotBlank()) {
                                onSubmit(title, description, date, location, artFormId, selectedImageUri)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KRed)
                    ) { Text("Create", color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun EditBioDialog(onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var bio by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Update Studio Bio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = bio, onValueChange = { bio = it },
                    label = { Text("Describe your art form and studio...") },
                    modifier = Modifier.fillMaxWidth(), minLines = 4, maxLines = 8,
                    shape = RoundedCornerShape(12.dp)
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { if (bio.isNotBlank()) onSubmit(bio) },
                        colors = ButtonDefaults.buttonColors(containerColor = KRed)
                    ) { Text("Save", color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun QuestionsTab(viewModel: ListingsViewModel) {
    val questions by viewModel.myQuestions.collectAsState(initial = emptyList())
    var answerText by remember { mutableStateOf("") }
    var replyingToId by remember { mutableStateOf<String?>(null) }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions from users yet", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(questions) { q: Question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Question:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KRed)
                        Text(q.questionText, fontSize = 15.sp)
                        
                        val answer = q.answerText
                        if (!answer.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Your Answer:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = answer,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        } else {
                            Spacer(Modifier.height(12.dp))
                            if (replyingToId == q.id) {
                                OutlinedTextField(
                                    value = answerText,
                                    onValueChange = { answerText = it },
                                    label = { Text("Write your answer...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = { replyingToId = null }) { Text("Cancel") }
                                    Button(
                                        onClick = {
                                            viewModel.answerQuestion(q.id, answerText)
                                            replyingToId = null
                                            answerText = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = KRed)
                                    ) { Text("Send Answer") }
                                }
                            } else {
                                Button(
                                    onClick = { replyingToId = q.id },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(containerColor = KRed)
                                ) {
                                    Text("Reply")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}