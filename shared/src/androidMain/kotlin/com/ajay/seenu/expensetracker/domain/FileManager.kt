package com.ajay.seenu.expensetracker.domain

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.ajay.seenu.expensetracker.data.data_source.local.ExportLocalDataSource
import com.ajay.seenu.expensetracker.domain.model.ExportFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AndroidFileManager constructor(
    private val context: Context
) : FileManager {

    override suspend fun saveFile(content: String, fileName: String, mimeType: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val file = File(getExportsDirectory(), fileName)
                file.parentFile?.mkdirs()

                FileOutputStream(file).use { outputStream ->
                    outputStream.write(content.toByteArray(Charsets.UTF_8))
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    override suspend fun shareFile(content: String, fileName: String, mimeType: String): Boolean =
        withContext(Dispatchers.Main) {
            try {
                // First save the file
                val saved = saveFile(content, fileName, mimeType)
                if (!saved) return@withContext false

                val file = File(getExportsDirectory(), fileName)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Expense Tracker Export")
                    putExtra(Intent.EXTRA_TEXT, "Your expense data export is attached.")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooserIntent = Intent.createChooser(shareIntent, "Share export file")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    override fun getExportsDirectory(): String {
        val externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val exportsDir = File(externalFilesDir, "exports")
        if (!exportsDir.exists()) {
            exportsDir.mkdirs()
        }
        return exportsDir.absolutePath
    }
}

// Android-specific extension functions
fun ExportLocalDataSource.saveAndShare(context: Context, format: ExportFormat) {
    // This can be called from Android UI
}