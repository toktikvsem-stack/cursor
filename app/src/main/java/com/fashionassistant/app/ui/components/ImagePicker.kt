package com.fashionassistant.app.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerDialog(
    onImageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showPermissionRationale by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val photoFile = getLatestPhotoFile(context)
            photoFile?.let {
                onImageSelected(it.absolutePath)
                onDismiss()
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(context, it)
            savedPath?.let { path ->
                onImageSelected(path)
                onDismiss()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить фото") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        when {
                            cameraPermissionState.status.isGranted -> {
                                val photoUri = createImageFileUri(context)
                                takePictureLauncher.launch(photoUri)
                            }
                            cameraPermissionState.status.shouldShowRationale -> {
                                showPermissionRationale = true
                            }
                            else -> {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сфотографировать")
                }

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выбрать из галереи")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Разрешение камеры") },
            text = { Text("Для съёмки фото необходимо разрешение на использование камеры") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cameraPermissionState.launchPermissionRequest()
                        showPermissionRationale = false
                    }
                ) {
                    Text("Разрешить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

private fun createImageFileUri(context: Context): Uri {
    val photoFile = File(
        context.getExternalFilesDir(null),
        "photo_${System.currentTimeMillis()}.jpg"
    )
    
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
}

private fun getLatestPhotoFile(context: Context): File? {
    val directory = context.getExternalFilesDir(null)
    return directory?.listFiles()
        ?.filter { it.name.startsWith("photo_") && it.extension == "jpg" }
        ?.maxByOrNull { it.lastModified() }
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val photoFile = File(
            context.getExternalFilesDir(null),
            "photo_${System.currentTimeMillis()}.jpg"
        )
        
        FileOutputStream(photoFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        
        photoFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
