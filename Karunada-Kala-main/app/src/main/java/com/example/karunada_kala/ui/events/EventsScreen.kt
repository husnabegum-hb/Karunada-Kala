package com.example.karunada_kala.ui.events

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.karunada_kala.domain.model.Event
import com.example.karunada_kala.ui.components.ShimmerCardLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    viewModel: EventsViewModel = hiltViewModel(),
    authViewModel: com.example.karunada_kala.ui.auth.AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookSuccess by viewModel.bookSuccess.collectAsState()
    val myBookedEventIds by viewModel.myBookedEventIds.collectAsState()
    val showOnlyMyBookings by viewModel.showOnlyMyBookings.collectAsState()
    val isKannada by authViewModel.isKannadaMode.collectAsState()
    val context = LocalContext.current
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bookSuccess) {
        if (bookSuccess) {
            val msg = context.getString(com.example.karunada_kala.R.string.event_booked_success)
            snackbarHostState.showSnackbar(msg)
            viewModel.resetBookSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.example.karunada_kala.R.string.events)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = if (showOnlyMyBookings) 1 else 0,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Tab(
                    selected = !showOnlyMyBookings,
                    onClick = { viewModel.setFilterMyBookings(false) },
                    text = { Text(stringResource(id = com.example.karunada_kala.R.string.all_events)) }
                )
                Tab(
                    selected = showOnlyMyBookings,
                    onClick = { viewModel.setFilterMyBookings(true) },
                    text = { Text(stringResource(id = com.example.karunada_kala.R.string.my_bookings)) }
                )
            }

            when (val state = uiState) {
                is EventsUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(5) {
                            ShimmerCardLoading()
                        }
                    }
                }
                is EventsUiState.Success -> {
                    val filteredEvents = if (showOnlyMyBookings) {
                        state.events.filter { it.id in myBookedEventIds }
                    } else {
                        state.events
                    }

                    if (filteredEvents.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            val emptyMsg = if (showOnlyMyBookings) {
                                stringResource(id = com.example.karunada_kala.R.string.no_bookings)
                            } else {
                                stringResource(id = com.example.karunada_kala.R.string.no_events)
                            }
                            Text(emptyMsg, style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredEvents) { event ->
                                EventCard(
                                    event = event, 
                                    onBookEvent = { viewModel.bookEvent(event.id) },
                                    isKannada = isKannada,
                                    isBooked = event.id in myBookedEventIds
                                )
                            }
                        }
                    }
                }
                is EventsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event, 
    onBookEvent: () -> Unit, 
    isKannada: Boolean,
    isBooked: Boolean
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = event.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = event.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = event.locationName, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, event.title)
                            putExtra(CalendarContract.Events.EVENT_LOCATION, event.locationName)
                            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                ) {
                    Text(stringResource(id = com.example.karunada_kala.R.string.add_to_calendar))
                }
                
                if (event.status == "Upcoming" && !isBooked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onBookEvent,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(stringResource(id = com.example.karunada_kala.R.string.book_now))
                    }
                } else if (isBooked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    ) {
                        Text(stringResource(id = com.example.karunada_kala.R.string.booked))
                    }
                }
            }
        }
    }
}
