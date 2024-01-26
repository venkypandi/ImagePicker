package com.venkatesh.imageupload

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import com.venkatesh.imagepicker.ImageUploadComponent
import com.venkatesh.imagepicker.data.api.RetrofitClient
import com.venkatesh.imagepicker.data.repository.ImageRepositoryImpl
import com.venkatesh.imagepicker.utils.Status
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var imageUploadComponent: ImageUploadComponent
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setView(com.venkatesh.imageupload.R.layout.progress)
        builder.setCancelable(false)
        dialog = builder.create()

        imageUploadComponent = findViewById(R.id.uploadComponent)

        val repository = ImageRepositoryImpl(RetrofitClient.apiService)
        var etName = findViewById<EditText>(R.id.et_name)

        imageUploadComponent.findViewById<Button>(com.venkatesh.imagepicker.R.id.submitButton).setOnClickListener {
            val selectedImageUri = imageUploadComponent.selectedImageUri
            lifecycleScope.launch {
                if (selectedImageUri != null) {
                    val file = imageUploadComponent.convertImageUriToFile(selectedImageUri)
                    //optional
                    val compressedImage = imageUploadComponent.compressImage(file.path)

                    repository.uploadAttachments(compressedImage).collect {
                        when(it.status){
                            Status.LOADING->{
                                setDialog(true)
                            }
                            Status.SUCCESS->{
                                setDialog(false)
                                etName.setText(it.data?.location)
                                Toast.makeText(this@MainActivity, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            }
                            Status.ERROR->{
                                setDialog(false)
                                Toast.makeText(this@MainActivity, "Upload Error..", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                }
            }
        }
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }
}