package com.venkatesh.imagepicker.data.repository

import android.util.Log
import com.venkatesh.imagepicker.data.api.ApiService
import com.venkatesh.imagepicker.utils.Resource
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ImageRepositoryImpl(private val apiService: ApiService) : ImageRepository {

    override suspend fun uploadAttachments(file: File) = flow {
        val files = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file",file.name,files)
        try {
            emit(Resource.loading(null))
            val response = apiService.uploadAttachments(
                 file = filePart
            )
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
                Log.d("response", "uploadAttachments:${response.body()} ")
            } else {
                emit(Resource.error("Error uploading", response.code(), null))

            }

        } catch (e: Exception) {
            emit(Resource.error(e.message.toString(), 404, null))

        }

    }

}