package com.ajay.seenu.expensetracker.android.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import kotlinx.datetime.LocalDateTime
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.UUID

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