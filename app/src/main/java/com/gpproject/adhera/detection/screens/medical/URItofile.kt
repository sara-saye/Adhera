package com.gpproject.adhera.detection.screens.medical

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File {
    val contentResolver = context.contentResolver
    // بنجيب امتداد الملف الأصلي أو بنعمل ملف مؤقت
    val file = File(context.cacheDir, "uploaded_file_${System.currentTimeMillis()}")
    contentResolver.openInputStream(uri)?.use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return file
}