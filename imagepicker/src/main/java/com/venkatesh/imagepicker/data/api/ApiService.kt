package com.venkatesh.imagepicker.data.api

import com.venkatesh.imagepicker.data.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("files/upload")
    @Multipart
    suspend fun uploadAttachments(@Part file: MultipartBody.Part): Response<UploadResponse>

}