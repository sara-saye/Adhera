package com.gpproject.adhera.data.repository

import com.gpproject.adhera.data.remote.api.AdheraApiService
import com.gpproject.adhera.data.remote.api.FacialResponse
import com.gpproject.adhera.data.remote.api.HealthStatusResponse
import com.gpproject.adhera.data.remote.api.PredictionResponse
import com.gpproject.adhera.data.remote.api.QuestionnaireRequest
import com.gpproject.adhera.data.remote.api.QuestionnaireResponse
import com.gpproject.adhera.data.remote.api.RetrofitClient
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response

interface AdheraRepository {
    suspend fun checkHealth(): Response<HealthStatusResponse>
    suspend fun predictQuestionnaire(features: List<Double>): Response<QuestionnaireResponse>
    suspend fun predictMri(file: File): Response<PredictionResponse>
    suspend fun predictEeg(file: File): Response<PredictionResponse>
    suspend fun predictFacial(file: File): Response<FacialResponse>
}

class AdheraRepositoryImpl(
    private val apiService: AdheraApiService = RetrofitClient.apiService
) : AdheraRepository {

    override suspend fun checkHealth(): Response<HealthStatusResponse> {
        return apiService.checkHealth()
    }

    override suspend fun predictQuestionnaire(features: List<Double>): Response<QuestionnaireResponse> {
        return apiService.predictQuestionnaire(QuestionnaireRequest(features))
    }

    override suspend fun predictMri(file: File): Response<PredictionResponse> {
        return apiService.predictMri(prepareFilePart(file, "file"))
    }

    override suspend fun predictEeg(file: File): Response<PredictionResponse> {
        return apiService.predictEeg(prepareFilePart(file, "file"))
    }

    override suspend fun predictFacial(file: File): Response<FacialResponse> {
        return apiService.predictFacial(prepareFilePart(file, "file"))
    }

    // دالة مساعدة لتحويل الـ File لـ MultipartPart
    private fun prepareFilePart(file: File, partName: String): MultipartBody.Part {
        // تحديد نوع الـ Media Type بناءً على الملف، أو تعميمه كـ stream
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}