package com.gpproject.adhera.detection

import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.detection.api.AdheraApiService
import com.gpproject.adhera.detection.api.EyeTrackingRequest
import com.gpproject.adhera.detection.api.EyeTrackingResponse
import com.gpproject.adhera.detection.api.FacialResponse
import com.gpproject.adhera.detection.api.HealthStatusResponse
import com.gpproject.adhera.detection.api.PredictionResponse
import com.gpproject.adhera.detection.api.QuestionnaireRequest
import com.gpproject.adhera.detection.api.QuestionnaireResponse
import com.gpproject.adhera.detection.api.RetrofitClient
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
    suspend fun predictEyeTracking(features: List<Double>): Response<EyeTrackingResponse>
}

class AdheraRepositoryImpl(
    private val apiService: AdheraApiService = RetrofitClient.apiService,
    private val dataStore: AdheraDataStore
) : AdheraRepository {

    override suspend fun checkHealth(): Response<HealthStatusResponse> {
        return apiService.checkHealth()
    }

    override suspend fun predictQuestionnaire(features: List<Double>): Response<QuestionnaireResponse> {
        val response = apiService.predictQuestionnaire(QuestionnaireRequest(features))
        if (response.isSuccessful) {
            response.body()?.let { body ->
                dataStore.saveQuestionnaireResult(
                    status     = body.status,
                    prediction = body.prediction,
                    message    = body.message
                )
            }
        }
        return response
    }

    override suspend fun predictMri(file: File): Response<PredictionResponse> {
        val response = apiService.predictMri(prepareFilePart(file, "file"))
        if (response.isSuccessful) {
            response.body()?.let { body ->
                dataStore.saveMriResult(
                    status      = body.status,
                    prediction  = body.prediction,
                    probability = body.probability,
                    message     = body.message
                )
            }
        }
        return response
    }

    override suspend fun predictEeg(file: File): Response<PredictionResponse> {
        val response = apiService.predictEeg(prepareFilePart(file, "file"))
        if (response.isSuccessful) {
            response.body()?.let { body ->
                dataStore.saveEegResult(
                    status      = body.status,
                    prediction  = body.prediction,
                    probability = body.probability,
                    message     = body.message
                )
            }
        }
        return response
    }

    override suspend fun predictFacial(file: File): Response<FacialResponse> {
        val response = apiService.predictFacial(prepareFilePart(file, "file"))
        if (response.isSuccessful) {
            response.body()?.let { body ->
                dataStore.saveFacialResult(
                    status          = body.status,
                    engagementLevel = body.engagementLevel,
                    engagementLabel = body.engagementLabel,
                    confidence      = body.confidence,
                    message         = body.message
                )
            }
        }
        return response
    }

    override suspend fun predictEyeTracking(features: List<Double>): Response<EyeTrackingResponse> {
        val response = apiService.predictEyeTracking(EyeTrackingRequest(features))
        if (response.isSuccessful) {
            response.body()?.let { body ->
                dataStore.saveEyeTrackingResult(
                    status      = body.status,
                    prediction  = body.prediction,
                    probability = body.probability,
                    message     = body.message
                )
            }
        }
        return response
    }

    private fun prepareFilePart(file: File, partName: String): MultipartBody.Part {
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}