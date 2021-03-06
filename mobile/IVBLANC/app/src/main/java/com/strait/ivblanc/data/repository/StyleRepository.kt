package com.strait.ivblanc.data.repository

import android.util.Log
import com.strait.ivblanc.config.ApplicationClass
import com.strait.ivblanc.data.api.StyleApi
import com.strait.ivblanc.data.model.response.StyleAllResponse
import com.strait.ivblanc.data.model.response.StyleResponse
import com.strait.ivblanc.util.Resource
import com.strait.ivblanc.util.StatusCode
import okhttp3.MultipartBody
import java.lang.Exception

private const val TAG = "StyleRepository_debuk"
class StyleRepository {
    val styleApi = ApplicationClass.sRetrofit.create(StyleApi::class.java)

    // Multipart form data 요청
    suspend fun addStyle(image: MultipartBody.Part, clothesList: MultipartBody.Part): Resource<StyleResponse> {
        return try {
            val response = styleApi.addStyle(image, clothesList)
            if(response.isSuccessful) {
                return if(response.code() == StatusCode.OK && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(response.body(), "스타일 등록에 실패했습니다.")
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Resource.error(null, "네트워크 연결을 확인해 주세요")
        }
    }

    suspend fun getAllStyles(): Resource<StyleAllResponse> {
        return try {
            val response = styleApi.getAllStyles()
            if(response.isSuccessful) {
                return if(response.code() == StatusCode.OK && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(response.body(), "등록된 스타일 조회를 할 수 없습니다.")
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getAllStyles: error - ${e.message}")
            Resource.error(null, "네트워크 연결을 확인해 주세요.")
        }
    }

    suspend fun getAllFriendStyles(FriendEmail:String): Resource<StyleAllResponse> {
        return try {
            val response = styleApi.getAllFriendStyles(FriendEmail)
            if(response.isSuccessful) {
                return if(response.code() == StatusCode.OK && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(response.body(), "등록된 스타일 조회를 할 수 없습니다.")
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getAllStyles: error - ${e.message}")
            Resource.error(null, "네트워크 연결을 확인해 주세요.")
        }
    }

    suspend fun deleteStyleById(styleId: Int): Resource<StyleResponse> {
        return try {
            val response = styleApi.deleteStyleById(styleId)
            if(response.isSuccessful) {
                return if(response.code() == StatusCode.OK && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(response.body(), response.body()!!.message!!)
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getAllStyles: error - ${e.message}")
            Resource.error(null, "네트워크 연결을 확인해 주세요.")
        }
    }

    suspend fun updateStyle(image: MultipartBody.Part, clothesList: MultipartBody.Part, styleId: MultipartBody.Part): Resource<StyleResponse> {
        return try {
            val response = styleApi.updateStyle(image, clothesList, styleId)
            if(response.isSuccessful) {
                return if(response.code() == StatusCode.OK && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(response.body(), "스타일 변경에 실패했습니다.")
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Resource.error(null, "네트워크 연결을 확인해 주세요")
        }
    }
}