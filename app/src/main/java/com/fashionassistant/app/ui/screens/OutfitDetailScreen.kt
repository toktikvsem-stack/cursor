package com.fashionassistant.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fashionassistant.app.data.model.ClothingItem
import com.fashionassistant.app.data.model.Outfit
import com.fashionassistant.app.ui.viewmodel.OutfitViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailScreen(
    outfitId: Long,
    onNavigateBack: () -> Unit,
    viewModel: OutfitViewModel = hiltViewModel()
) {
    var outfit by remember { mutableStateOf<Outfit?>(null) }
    var items by remember { mutableStateOf<List<ClothingItem>>(emptyList()) }

    LaunchedEffect(outfitId) {
        viewModel.getOutfitWithItems(outfitId)?.let { (o, i) ->
            outfit = o
            items = i
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(outfit?.name ?: "Образ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            outfit?.let { o ->
                // Score card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            o.score >= 70 -> MaterialTheme.colorScheme.primaryContainer
                            o.score >= 50 -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Оценка образа: ${o.score.toInt()}/100",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        if (o.feedback.isNotEmpty()) {
                            Divider()
                            Text(
                                text = o.feedback,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Items grid
                Text(
                    text = "Вещи в образе",
                    style = MaterialTheme.typography.titleMedium
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.75f)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = File(item.imagePath),
                                    contentDescription = item.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
