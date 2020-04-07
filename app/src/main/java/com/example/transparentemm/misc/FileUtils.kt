package com.example.transparentemm.misc

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileUtils constructor(private val context: Context) {
    fun getFile(name: String, format: String, path: String): File {
        val root = context.filesDir
        val myDir = File("$root/$path")
        myDir.mkdirs()
        val fname = "$name.$format"
        val file = File(myDir, fname)
        return file
    }

    fun getTempFile(name: String, extension: String, path: String): File {
        val root = context.filesDir
        val myDir = File("$root/$path")
        myDir.mkdirs()
        val file = File.createTempFile(name, extension)
        return file
    }

    fun fileExists(name: String, format: String, path: String): Boolean {
        val file = getFile(name, format, path)
        return file.exists()
    }

    fun deleteFile(name: String, format: String, path: String): Boolean {
        val file = getFile(name, format, path)
        return file.delete()
    }

    suspend fun saveBitmap(name: String, bitmap: Bitmap, forceReload: Boolean = false): Uri =
        withContext(Dispatchers.IO) {
            val file = getFile(name, "jpeg", "app_icons")
            if (!forceReload && file.exists()) {
                Uri.fromFile(file)
            } else {
                with(file.outputStream()) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, this)
                    flush()
                }
                Uri.fromFile(file)
            }
        }
}