package com.venkatesh.imagepicker.data.repository

import com.venkatesh.imagepicker.data.model.UploadResponse
import com.venkatesh.imagepicker.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ImageRepository {

    suspend fun uploadAttachments(file: File) : Flow<Resource<UploadResponse>>
}