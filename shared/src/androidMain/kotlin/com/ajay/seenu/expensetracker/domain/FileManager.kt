package com.ajay.seenu.expensetracker.domain

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AndroidFileManager constructor(
    private val context: Context
) : FileManager {

    override suspend fun saveFile(content: String, fileName: String, mimeType: String): Boolean =
        saveBytes(content.toByteArray(Charsets.UTF_8), fileName, mimeType)

    override suspend fun saveBytes(data: ByteArray, fileName: String, mimeType: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveToDownloadsMediaStore(data, fileName, mimeType)
                } else {
                    saveToDownloadsLegacy(data, fileName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    override suspend fun shareBytes(data: ByteArray, fileName: String, mimeType: String): Boolean =
        withContext(Dispatchers.Main) {
            try {
                val uri = saveToCacheAndGetUri(data, fileName)
                    ?: return@withContext false

                launchShareIntent(uri, mimeType)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    override suspend fun shareFile(content: String, fileName: String, mimeType: String): Boolean =
        shareBytes(content.toByteArray(Charsets.UTF_8), fileName, mimeType)

    override fun getExportsDirectory(): String {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val exportsDir = File(downloadsDir, "ExpenseTracker")
        if (!exportsDir.exists()) {
            exportsDir.mkdirs()
        }
        return exportsDir.absolutePath
    }

    private fun saveToDownloadsMediaStore(data: ByteArray, fileName: String, mimeType: String): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/ExpenseTracker")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return false

        resolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(data)
        } ?: return false

        return true
    }

    @Suppress("DEPRECATION")
    private fun saveToDownloadsLegacy(data: ByteArray, fileName: String): Boolean {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val exportsDir = File(downloadsDir, "ExpenseTracker")
        exportsDir.mkdirs()

        val file = File(exportsDir, fileName)
        FileOutputStream(file).use { it.write(data) }
        return true
    }

    private suspend fun saveToCacheAndGetUri(data: ByteArray, fileName: String): Uri? =
        withContext(Dispatchers.IO) {
            val cacheFile = File(context.cacheDir, fileName)
            FileOutputStream(cacheFile).use { it.write(data) }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )
        }

    private fun launchShareIntent(uri: Uri, mimeType: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Tracker Export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share export file")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}