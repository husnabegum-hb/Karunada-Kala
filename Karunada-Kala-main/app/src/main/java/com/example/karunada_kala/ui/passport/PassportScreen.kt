package com.example.karunada_kala.ui.passport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassportScreen(
    viewModel: PassportViewModel = hiltViewModel()
) {
    val stamps by viewModel.stamps.collectAsState()
    
    val districts = listOf(
        "Bagalkot", "Ballari", "Belagavi", "Bengaluru Rural", "Bengaluru Urban",
        "Bidar", "Chamarajanagar", "Chikkaballapur", "Chikkamagaluru", "Chitradurga",
        "Dakshina Kannada", "Davanagere", "Dharwad", "Gadag", "Hassan", "Haveri",
        "Kalaburagi", "Kodagu", "Kolar", "Koppal", "Mandya", "Mysuru", "Raichur",
        "Ramanagara", "Shivamogga", "Tumakuru", "Udupi", "Uttara Kannada", "Vijayapura",
        "Yadgir", "Vijayanagara"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cultural Passport") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("State Explorer", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { stamps.size / 31f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("${stamps.size}/31 Districts Discovered", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = { viewModel.simulateCheckIn() }) {
                Text("Simulate Check-in (Demo)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(80.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(districts.size) { index ->
                    val district = districts[index]
                    val isUnlocked = stamps.contains(district)
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) MaterialTheme.colorScheme.secondary else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = district.take(2).uppercase(),
                                color = if (isUnlocked) MaterialTheme.colorScheme.onSecondary else Color.DarkGray,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = district, 
                            style = MaterialTheme.typography.labelSmall, 
                            maxLines = 1,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
