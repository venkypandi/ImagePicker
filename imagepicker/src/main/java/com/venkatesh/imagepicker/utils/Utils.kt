package com.venkatesh.imagepicker.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun getFileType(context: Context, imageUri: Uri): String? {
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()

        // Query the MediaStore to get the MIME type
        val mimeType: String? = contentResolver.getType(imageUri)

        // If the MIME type is not available, try to get it from the file extension
        if (mimeType == null) {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())
            return mimeTypeMap.getMimeTypeFromExtension(fileExtension.toLowerCase())
        }

        return mimeType
    }

    fun getFileName(context: Context, imageUri: Uri): String? {
        val contentResolver = context.contentResolver

        // Query the MediaStore to get the file name
        val cursor = contentResolver.query(imageUri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    return it.getString(displayNameIndex)
                }
            }
        }

        return null
    }

     fun createTempImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
        val imageFileName: String = "SG_${timeStamp}_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

}