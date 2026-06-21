package com.gpproject.adhera.data.remote.api

import com.google.gson.annotations.SerializedName

// 1. Questionnaire Models
data class QuestionnaireRequest(
    @SerializedName("features") val features: List<Double>
)

data class QuestionnaireResponse(
    @SerializedName("status") val status: String,
    @SerializedName("prediction") val prediction: Int,
    @SerializedName("message") val message: String? = null
)

// 2. MRI & EEG Response (شبه بعض في الـ JSON اللي راجع)
data class PredictionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("prediction") val prediction: Int,
    @SerializedName("probability") val probability: Double,
    @SerializedName("message") val message: String? = null
)

// 3. Facial Response
data class FacialResponse(
    @SerializedName("status") val status: String,
    @SerializedName("engagement_level") val engagementLevel: Int,
    @SerializedName("engagement_label") val engagementLabel: String,
    @SerializedName("confidence") val confidence: Double,
    @SerializedName("message") val message: String? = null
)

// 4. Health Check Response
data class HealthStatusResponse(
    @SerializedName("mri_model") val mriModel: String,
    @SerializedName("questionnaire_model") val questionnaireModel: String,
    @SerializedName("eeg_model") val eegModel: String,
    @SerializedName("facial_model") val facialModel: String
)