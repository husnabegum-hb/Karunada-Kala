package com.example.karunada_kala.ui.artform

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.karunada_kala.domain.model.Artisan
import com.example.karunada_kala.ui.components.ImageWithPlaceholder
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtFormScreen(
    onNavigateToProfile: (String) -> Unit,
    viewModel: ArtFormViewModel = hiltViewModel()
) {
    val artForm by viewModel.artForm.collectAsState()
    val gurus by viewModel.gurus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artForm?.title ?: stringResource(id = com.example.karunada_kala.R.string.art_form_hub)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        artForm?.let { form ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            webChromeClient = WebChromeClient()
                            webViewClient = WebViewClient()
                            loadUrl(form.videoUrl)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(id = com.example.karunada_kala.R.string.history_significance), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = form.history, style = MaterialTheme.typography.bodyLarge)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(text = stringResource(id = com.example.karunada_kala.R.string.meet_gurus), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(gurus) { guru ->
                        GuruCard(guru = guru) {
                            onNavigateToProfile(guru.id)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun GuruCard(guru: Artisan, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        ImageWithPlaceholder(
            model = guru.imageUrl,
            contentDescription = guru.name,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = guru.name, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
    }
}
