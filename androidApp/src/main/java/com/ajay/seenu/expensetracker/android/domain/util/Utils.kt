package com.ajay.seenu.expensetracker.android.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
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