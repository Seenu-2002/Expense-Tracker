package com.ajay.seenu.expensetracker.android.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.icu.text.NumberFormat
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.text.format
import kotlin.toString

fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri {
    val fileName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
    val file = File(context.cacheDir, fileName)
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return file.toUri()
}

fun getFileInfoFromUri(context: Context, uri: Uri): Map<String, String?> {
    val fileInfo = mutableMapOf<String, String?>()

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            // Get file name
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val fileName = cursor.getString(nameIndex)
            fileInfo["fileName"] = fileName

            // Get file size
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val fileSize = cursor.getLong(sizeIndex).toString()
            fileInfo["fileSize"] = fileSize
        }
    }

    val mimeType = context.contentResolver.getType(uri)
    fileInfo["fileType"] = mimeType

    val filePath = uri.path
    fileInfo["filePath"] = filePath

    return fileInfo
}

fun getFileInfoFromCamImageUri(context: Context, uri: Uri): Map<String, String?> {
    val fileInfo = mutableMapOf<String, String?>()
    var fileSize: String? = null

    val cursor = context.contentResolver.query(uri, null, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (sizeIndex != -1) {
            fileSize = cursor.getLong(sizeIndex).toString()
        }
        cursor.close()
    }

    // Fallback for file:// Uris
    if (fileSize == null) {
        val path = uri.path
        if (path != null) {
            val file = File(path)
            if (file.exists()) fileSize = file.length().toString()
        }
    }

    fileInfo["fileName"] = generateImageFileName()
    fileInfo["fileSize"] = fileSize
    fileInfo["fileType"] = context.contentResolver.getType(uri)
    fileInfo["filePath"] = uri.path

    return fileInfo
}

fun generateImageFileName(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
    return "${timeStamp}.jpg"
}

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val fileName = cursor.getString(nameIndex)
            return fileName
        }
    }
    return null
}

inline fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun formatDateHeader(inputDate: String): String {
    // TODO: User data format configuration
    val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val parsedDate = formatter.parse(inputDate) ?: return inputDate

    val calendar = Calendar.getInstance()
    calendar.time = parsedDate

    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    val yesterday = Calendar.getInstance()

    yesterday.time = today.time
    yesterday.add(Calendar.DAY_OF_YEAR, -1)

    return when (calendar.timeInMillis) {
        today.timeInMillis -> "Today"
        yesterday.timeInMillis -> "Yesterday"
        else -> inputDate
    }
}

fun Double.asCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return formatter.format(this)
}