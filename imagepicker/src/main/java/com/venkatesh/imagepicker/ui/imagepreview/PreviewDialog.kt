package com.venkatesh.imagepicker.ui.imagepreview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import com.venkatesh.imagepicker.R
import java.io.InputStream

class PreviewDialog(context: Context,imageUri: Uri) : AppCompatDialog(context) {

    private val imageView:ImageView?


    init {
        // Set a custom dialog layout
        setContentView(R.layout.fragment_image_preview)

        // Initialize UI components
        val closeButton: ImageButton? = findViewById(R.id.closeButton)
        imageView = findViewById(R.id.previewImageView)
        loadImage(imageUri)
        // Set a click listener to dismiss the dialog
        closeButton?.setOnClickListener {
            dismiss()
        }
    }

     private fun loadImage(imageUri: Uri) {
        // Use an image loading library like Glide to load the image into the ImageView
        try {
            val inputStream: InputStream? = context?.contentResolver?.openInputStream(imageUri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            imageView?.setImageBitmap(bitmap)

        } catch (e:Exception){
            e.printStackTrace()
        }
    }
}