package com.gpproject.adhera.detection.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

val Context.adheraDataStore: DataStore<Preferences> by preferencesDataStore(name = "adhera_results")

class AdheraDataStore(private val context: Context) {

    private val TAG = "AdheraDataStore"

    // Firestore helpers
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUid get() = FirebaseAuth.getInstance().currentUser?.uid

    private fun userResultsDoc(modelName: String) =
        currentUid?.let { uid ->
            firestore.collection("users").document(uid)
                .collection("results").document(modelName)
        }

    companion object {
        // --- Questionnaire ---
        val QUESTIONNAIRE_STATUS     = stringPreferencesKey("questionnaire_status")
        val QUESTIONNAIRE_PREDICTION = intPreferencesKey("questionnaire_prediction")
        val QUESTIONNAIRE_MESSAGE    = stringPreferencesKey("questionnaire_message")

        // --- MRI ---
        val MRI_STATUS      = stringPreferencesKey("mri_status")
        val MRI_PREDICTION  = intPreferencesKey("mri_prediction")
        val MRI_PROBABILITY = doublePreferencesKey("mri_probability")
        val MRI_MESSAGE     = stringPreferencesKey("mri_message")

        // --- EEG ---
        val EEG_STATUS      = stringPreferencesKey("eeg_status")
        val EEG_PREDICTION  = intPreferencesKey("eeg_prediction")
        val EEG_PROBABILITY = doublePreferencesKey("eeg_probability")
        val EEG_MESSAGE     = stringPreferencesKey("eeg_message")

        // --- Facial ---
        val FACIAL_STATUS           = stringPreferencesKey("facial_status")
        val FACIAL_ENGAGEMENT_LEVEL = intPreferencesKey("facial_engagement_level")
        val FACIAL_ENGAGEMENT_LABEL = stringPreferencesKey("facial_engagement_label")
        val FACIAL_CONFIDENCE       = doublePreferencesKey("facial_confidence")
        val FACIAL_MESSAGE          = stringPreferencesKey("facial_message")

        // --- Eye Tracking ---
        val EYE_TRACKING_STATUS      = stringPreferencesKey("eye_tracking_status")
        val EYE_TRACKING_PREDICTION  = intPreferencesKey("eye_tracking_prediction")
        val EYE_TRACKING_PROBABILITY = doublePreferencesKey("eye_tracking_probability")
        val EYE_TRACKING_MESSAGE     = stringPreferencesKey("eye_tracking_message")

        val REPORT_SAVE_COUNT = intPreferencesKey("report_save_count")
    }

    // ======================== Save Functions ========================

    suspend fun saveQuestionnaireResult(status: String, prediction: Int, message: String?) {
        // 1. DataStore
        context.adheraDataStore.edit { prefs ->
            prefs[QUESTIONNAIRE_STATUS]     = status
            prefs[QUESTIONNAIRE_PREDICTION] = prediction
            message?.let { prefs[QUESTIONNAIRE_MESSAGE] = it }
        }
        Log.d(TAG, "[DataStore] questionnaire saved → prediction=$prediction")

        // 2. Firestore
        val data = buildMap<String, Any> {
            put("status", status)
            put("prediction", prediction)
            put("timestamp", System.currentTimeMillis())
            message?.let { put("message", it) }
        }
        saveToFirestore("questionnaire", data)
    }

    suspend fun saveMriResult(status: String, prediction: Int, probability: Double, message: String?) {
        context.adheraDataStore.edit { prefs ->
            prefs[MRI_STATUS]      = status
            prefs[MRI_PREDICTION]  = prediction
            prefs[MRI_PROBABILITY] = probability
            message?.let { prefs[MRI_MESSAGE] = it }
        }
        Log.d(TAG, "[DataStore] mri saved → prediction=$prediction, probability=$probability")

        val data = buildMap<String, Any> {
            put("status", status)
            put("prediction", prediction)
            put("probability", probability)
            put("timestamp", System.currentTimeMillis())
            message?.let { put("message", it) }
        }
        saveToFirestore("mri", data)
    }

    suspend fun saveEegResult(status: String, prediction: Int, probability: Double, message: String?) {
        context.adheraDataStore.edit { prefs ->
            prefs[EEG_STATUS]      = status
            prefs[EEG_PREDICTION]  = prediction
            prefs[EEG_PROBABILITY] = probability
            message?.let { prefs[EEG_MESSAGE] = it }
        }
        Log.d(TAG, "[DataStore] eeg saved → prediction=$prediction, probability=$probability")

        val data = buildMap<String, Any> {
            put("status", status)
            put("prediction", prediction)
            put("probability", probability)
            put("timestamp", System.currentTimeMillis())
            message?.let { put("message", it) }
        }
        saveToFirestore("eeg", data)
    }

    suspend fun saveFacialResult(
        status: String,
        engagementLevel: Int,
        engagementLabel: String,
        confidence: Double,
        message: String?
    ) {
        context.adheraDataStore.edit { prefs ->
            prefs[FACIAL_STATUS]           = status
            prefs[FACIAL_ENGAGEMENT_LEVEL] = engagementLevel
            prefs[FACIAL_ENGAGEMENT_LABEL] = engagementLabel
            prefs[FACIAL_CONFIDENCE]       = confidence
            message?.let { prefs[FACIAL_MESSAGE] = it }
        }
        Log.d(TAG, "[DataStore] facial saved → engagementLevel=$engagementLevel, confidence=$confidence")

        val data = buildMap<String, Any> {
            put("status", status)
            put("engagement_level", engagementLevel)
            put("engagement_label", engagementLabel)
            put("confidence", confidence)
            put("timestamp", System.currentTimeMillis())
            message?.let { put("message", it) }
        }
        saveToFirestore("facial", data)
    }

    suspend fun saveEyeTrackingResult(status: String, prediction: Int, probability: Double, message: String?) {
        context.adheraDataStore.edit { prefs ->
            prefs[EYE_TRACKING_STATUS]      = status
            prefs[EYE_TRACKING_PREDICTION]  = prediction
            prefs[EYE_TRACKING_PROBABILITY] = probability
            message?.let { prefs[EYE_TRACKING_MESSAGE] = it }
        }
        Log.d(TAG, "[DataStore] eye_tracking saved → prediction=$prediction, probability=$probability")

        val data = buildMap<String, Any> {
            put("status", status)
            put("prediction", prediction)
            put("probability", probability)
            put("timestamp", System.currentTimeMillis())
            message?.let { put("message", it) }
        }
        saveToFirestore("eye_tracking", data)
    }

    // ======================== Firestore Helper ========================

    private suspend fun saveToFirestore(modelName: String, data: Map<String, Any>) {
        val doc = userResultsDoc(modelName)
        if (doc == null) {
            Log.w(TAG, "[Firestore] skipped $modelName — no authenticated user")
            return
        }
        try {
            doc.set(data).await()
            Log.d(TAG, "[Firestore] $modelName saved → uid=${currentUid}, data=$data")
        } catch (e: Exception) {
            Log.e(TAG, "[Firestore] $modelName FAILED → ${e.message}", e)
            // DataStore already saved, so we don't rethrow — silent fail on Firestore
        }
    }

    // ======================== Read Flows ========================

    val questionnaireResult: Flow<Triple<String, Int, String?>> =
        context.adheraDataStore.data.map { prefs ->
            Triple(
                prefs[QUESTIONNAIRE_STATUS] ?: "",
                prefs[QUESTIONNAIRE_PREDICTION] ?: -1,
                prefs[QUESTIONNAIRE_MESSAGE]
            )
        }

    val mriResult: Flow<MriResult> =
        context.adheraDataStore.data.map { prefs ->
            MriResult(
                status      = prefs[MRI_STATUS] ?: "",
                prediction  = prefs[MRI_PREDICTION] ?: -1,
                probability = prefs[MRI_PROBABILITY] ?: 0.0,
                message     = prefs[MRI_MESSAGE]
            )
        }

    val eegResult: Flow<EegResult> =
        context.adheraDataStore.data.map { prefs ->
            EegResult(
                status      = prefs[EEG_STATUS] ?: "",
                prediction  = prefs[EEG_PREDICTION] ?: -1,
                probability = prefs[EEG_PROBABILITY] ?: 0.0,
                message     = prefs[EEG_MESSAGE]
            )
        }

    val facialResult: Flow<FacialResult> =
        context.adheraDataStore.data.map { prefs ->
            FacialResult(
                status          = prefs[FACIAL_STATUS] ?: "",
                engagementLevel = prefs[FACIAL_ENGAGEMENT_LEVEL] ?: -1,
                engagementLabel = prefs[FACIAL_ENGAGEMENT_LABEL] ?: "",
                confidence      = prefs[FACIAL_CONFIDENCE] ?: 0.0,
                message         = prefs[FACIAL_MESSAGE]
            )
        }

    val eyeTrackingResult: Flow<EyeTrackingResult> =
        context.adheraDataStore.data.map { prefs ->
            EyeTrackingResult(
                status      = prefs[EYE_TRACKING_STATUS] ?: "",
                prediction  = prefs[EYE_TRACKING_PREDICTION] ?: -1,
                probability = prefs[EYE_TRACKING_PROBABILITY] ?: 0.0,
                message     = prefs[EYE_TRACKING_MESSAGE]
            )
        }

    val reportSaveCount: Flow<Int> =
        context.adheraDataStore.data.map { prefs ->
            prefs[REPORT_SAVE_COUNT] ?: 0
        }

    suspend fun markReportSaved(): Int {
        var updatedCount = 0
        context.adheraDataStore.edit { prefs ->
            updatedCount = (prefs[REPORT_SAVE_COUNT] ?: 0) + 1
            prefs[REPORT_SAVE_COUNT] = updatedCount
        }
        return updatedCount
    }

    // ======================== Clear ========================

    suspend fun clearAll() {
        context.adheraDataStore.edit { it.clear() }
        Log.d(TAG, "[DataStore] all results cleared")
    }
}

// ======================== Local Data Classes ========================

data class MriResult(
    val status: String,
    val prediction: Int,
    val probability: Double,
    val message: String?
)

data class EegResult(
    val status: String,
    val prediction: Int,
    val probability: Double,
    val message: String?
)

data class FacialResult(
    val status: String,
    val engagementLevel: Int,
    val engagementLabel: String,
    val confidence: Double,
    val message: String?
)

data class EyeTrackingResult(
    val status: String,
    val prediction: Int,
    val probability: Double,
    val message: String?
)
