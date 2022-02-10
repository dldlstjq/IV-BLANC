package com.strait.ivblanc.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MultiPartUtil {
    companion object {
        fun makeMultiPartBodyFile(name: String, absolutePath: String, mediaType: String): MultipartBody.Part {
            val file = File(absolutePath)
            return MultipartBody.Part.createFormData(name, file.name, file.asRequestBody(mediaType.toMediaType()))
        }

        fun makeMultiPartBody(name: String, value: String): MultipartBody.Part {
            return MultipartBody.Part.createFormData(name, value)
        }
    }
}