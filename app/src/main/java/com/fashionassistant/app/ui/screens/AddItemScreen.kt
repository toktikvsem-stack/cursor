package com.fashionassistant.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fashionassistant.app.data.model.ClothingCategory
import com.fashionassistant.app.data.model.ClothingItem
import com.fashionassistant.app.ui.components.ImagePickerDialog
import com.fashionassistant.app.ui.viewmodel.WardrobeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onNavigateBack: () -> Unit,
    viewModel: WardrobeViewModel = hiltViewModel()
) {
    var showImagePicker by remember { mutableStateOf(false) }
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    var itemName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ClothingCategory.TOP) }
    var itemColor by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }

    val categories = ClothingCategory.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить вещь") },
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
        },
        floatingActionButton = {
            if (selectedImagePath != null && itemName.isNotBlank() && itemColor.isNotBlank()) {
                FloatingActionButton(
                    onClick = {
                        val item = ClothingItem(
                            name = itemName,
                            imagePath = selectedImagePath!!,
                            category = selectedCategory,
                            color = itemColor
                        )
                        viewModel.addClothingItem(item)
                        onNavigateBack()
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Сохранить")
                }
            }
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
            // Image preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                onClick = { showImagePicker = true }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImagePath != null) {
                        AsyncImage(
                            model = File(selectedImagePath!!),
                            contentDescription = "Фото вещи",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            "Нажмите для добавления фото",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Name field
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Название вещи") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.displayName) },
                            onClick = {
                                selectedCategory = category
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Color field
            OutlinedTextField(
                value = itemColor,
                onValueChange = { itemColor = it },
                label = { Text("Цвет") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Например: черный, белый, синий") }
            )
        }
    }

    if (showImagePicker) {
        ImagePickerDialog(
            onImageSelected = { path ->
                selectedImagePath = path
            },
            onDismiss = { showImagePicker = false }
        )
    }
}
