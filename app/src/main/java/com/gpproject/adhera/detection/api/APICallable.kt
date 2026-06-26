package com.gpproject.adhera.detection.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AdheraApiService {

    @GET("health")
    suspend fun checkHealth(): Response<HealthStatusResponse>

    @POST("predict/questionnaire")
    suspend fun predictQuestionnaire(
        @Body request: QuestionnaireRequest
    ): Response<QuestionnaireResponse>

    @Multipart
    @POST("predict/mri")
    suspend fun predictMri(
        @Part file: MultipartBody.Part
    ): Response<PredictionResponse>

    @Multipart
    @POST("predict/eeg")
    suspend fun predictEeg(
        @Part file: MultipartBody.Part
    ): Response<PredictionResponse>

    @Multipart
    @POST("predict/facial")
    suspend fun predictFacial(
        @Part file: MultipartBody.Part
    ): Response<FacialResponse>

    @POST("predict/eye-tracking")
    suspend fun predictEyeTracking(
        @Body request: EyeTrackingRequest
    ): Response<EyeTrackingResponse>
}