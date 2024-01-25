package com.venkatesh.imageupload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageUploadComponent = findViewById(R.id.uploadComponent)

        val repository = ImageRepositoryImpl(RetrofitClient.apiService)

        imageUploadComponent.findViewById<Button>(com.venkatesh.imagepicker.R.id.submitButton).setOnClickListener {
            val selectedImageUri = imageUploadComponent.selectedImageUri
            lifecycleScope.launch {
                if (selectedImageUri != null) {
                    val file = selectedImageUri.toFile()
                    //optional
                    val compressedImage = imageUploadComponent.compressImage(file.path)

                    repository.uploadAttachments(compressedImage).collect {
                        when(it.status){
                            Status.LOADING->{
                                Toast.makeText(this@MainActivity, "Loading..", Toast.LENGTH_SHORT).show()
                            }
                            Status.SUCCESS->{
                                Toast.makeText(this@MainActivity, "Success..", Toast.LENGTH_SHORT).show()
                            }
                            Status.ERROR->{
                                Toast.makeText(this@MainActivity, "Error..", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                }
            }
        }
    }
}