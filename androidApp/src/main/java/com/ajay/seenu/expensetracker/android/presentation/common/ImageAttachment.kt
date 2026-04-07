package com.ajay.seenu.expensetracker.android.presentation.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
internal fun ImageAttachment(maxFilesAllowed: Int,
                             maxAllowedSize: Long = 10 * 1024 * 1024,
                             onAttachmentUpdate: (List<Uri>) -> Unit,
                             onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName +
                ".provider", file
    )

    var currentFilesCount by rememberSaveable { mutableIntStateOf(0) }
    var currentTotalSize by rememberSaveable { mutableLongStateOf(0) }
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    LaunchedEffect(capturedImageUri) {
        if(capturedImageUri != Uri.EMPTY)
            currentFilesCount++
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxFilesAllowed)) { selectedUriList ->

        if(maxAllowedSize != -1L) {
            selectedUriList.forEach { selectedUri ->
                val cursor: Cursor? = context.contentResolver.query(selectedUri, null, null, null, null)
                cursor?.use {
                    val sizeIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                    it.moveToFirst()
                    val size = it.getLong(sizeIndex)
                    currentTotalSize+= size
                }
            }
            if(currentTotalSize > maxAllowedSize) {

            } else {
                onAttachmentUpdate.invoke(selectedUriList)
                onDismissRequest.invoke()
            }
        } else {

        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if(it) {
            capturedImageUri = uri
            onAttachmentUpdate.invoke(listOf(capturedImageUri))
            onDismissRequest.invoke()
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 15.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 10.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    //onDismissRequest.invoke()
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Face,
                    contentDescription = "image gallery",
                )
                Text(
                    text = "Choose Photo",
                    modifier = Modifier.padding(start = 15.dp),
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Face,
                    contentDescription = "image camera",
                )
                Text(
                    text = "Take Photo",
                    modifier = Modifier.padding(start = 15.dp),
                )
            }
        }
    }
}

private fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "IMG" + timeStamp + "_"
    return File.createTempFile(imageFileName, ".jpg", externalCacheDir)
}