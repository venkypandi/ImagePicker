package com.venkatesh.imagepicker

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.venkatesh.imagepicker.ui.imagepreview.PreviewDialog
import com.venkatesh.imagepicker.utils.Utils.createTempImageFile
import com.venkatesh.imagepicker.utils.Utils.getFileName
import com.venkatesh.imagepicker.utils.Utils.getFileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageUploadComponent(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val fileNameTextView: TextView
    private val fileTypeTextView: TextView
    private val previewButton: Button
    private val submitButton: Button
    private val selectButton: Button
    var fileName:String? = null
    var fileType:String? = null
    private lateinit var previewDialog: AppCompatDialog


    var selectedImageUri: Uri? = null
    private var currentPhotoPath: String? = null


    private var permissionLauncher =
        (context as? ComponentActivity)?.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Permission granted, perform the desired action
                openCameraPick()
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val getGalleryContent = (context as? ComponentActivity)?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                selectedImageUri = imageUri
                displaySelectedImage(imageUri,false)
            }
        }
    }

    private val getCameraContent = (context as? ComponentActivity)?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = currentPhotoPath?.let { File(it).toUri() }
            selectedImageUri?.let { displaySelectedImage(it,true) }
        }
    }


    init {
        LayoutInflater.from(context).inflate(R.layout.image_upload_component, this, true)

        fileNameTextView = findViewById(R.id.fileName)
        fileTypeTextView = findViewById(R.id.fileTypeTextView)
        previewButton = findViewById(R.id.previewButton)
        submitButton = findViewById(R.id.submitButton)
        selectButton = findViewById(R.id.selectImage)

        setListeners()
    }

    private fun setListeners() {
        selectButton.setOnClickListener {
            showBottomSheet()
        }

        previewButton.setOnClickListener {
            selectedImageUri?.let {
                openImagePreview(it)

            }
        }

    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getGalleryContent?.launch(intent)

    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.fragment_bottom_sheet, null)

        // Customize the content of your bottom sheet
        // For example, you can find views and set click listeners here
        val cameraText:TextView = bottomSheetView.findViewById(R.id.selectCamera)
        val galleryText:TextView = bottomSheetView.findViewById(R.id.selectGallery)

        cameraText.setOnClickListener {
            requestCameraPermission()
            bottomSheetDialog.dismiss()
        }

        galleryText.setOnClickListener {
            openImageChooser()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

    }

    private fun openCameraPick() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.venkatesh.imageupload.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    getCameraContent?.launch(takePictureIntent)
                }
            }
        }


    }


    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission
            permissionLauncher?.launch(android.Manifest.permission.CAMERA)
        } else {
            // Permission is already granted
            // Perform your camera-related operations here
            openCameraPick()
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "sg_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun displaySelectedImage(imageUri: Uri,isCamera: Boolean) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

            // Display the file type
            fileType = getFileType(context,imageUri)
            fileTypeTextView.text = "File Type: $fileType"

            fileName = if (isCamera){
                imageUri.pathSegments.last()
            } else getFileName(context, imageUri)

            fileNameTextView.text = "File Name: $fileName"

            Log.d("imageupload", "displaySelectedImage: $fileType ")
            fileNameTextView.visibility = View.VISIBLE
            fileTypeTextView.visibility = View.VISIBLE

            // Show preview and submit buttons
            previewButton.visibility = View.VISIBLE
            submitButton.visibility = View.VISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openImagePreview(imageUri: Uri) {
        // Implement code to open a preview activity or dialog
        // You can use an Intent or a custom DialogFragment for this purpose
        // For simplicity, we will just print a log message here
        println("Open Image Preview: $imageUri")
        previewDialog = PreviewDialog(context,imageUri)
        previewDialog.show()
    }

    suspend fun convertImageUriToFile(imageUri: Uri): File {
        return withContext(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)

            val outputFile: File = createTempImageFile(context)

            try {
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            } finally {
                inputStream?.close()
            }

            outputFile
        }
    }

     fun compressImage(imagePath: String): File {
        val originalFile = File(imagePath)
        val compressedImageFile = File(context.cacheDir, "compressed_image.jpg")

        try {
            val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
            val stream = ByteArrayOutputStream()

            // Compress the bitmap to JPEG format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

            // Write the compressed data to the file
            val fos = FileOutputStream(compressedImageFile)
            fos.write(stream.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return compressedImageFile
    }

    fun showPreviewButton(isVisible:Boolean){
        previewButton.isVisible = isVisible
    }

    fun showSelectButton(isVisible:Boolean){
        selectButton.isVisible = isVisible
    }

    fun showSubmitButton(isVisible:Boolean){
        previewButton.isVisible = isVisible
    }

    fun showFileType(isVisible:Boolean){
        fileTypeTextView.isVisible = isVisible
    }

    fun showFileName(isVisible:Boolean){
        fileNameTextView.isVisible = isVisible
    }



}
