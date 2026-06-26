package com.gpproject.adhera.detection.reports


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.roundToInt

// ====================== UI Model ======================

data class ModelResult(
    val title: String,
    val percentage: Int,        // 0–100 to display
    val iconType: ModelIconType
)

enum class ModelIconType {
    ENGAGEMENT,   // Facial
    EEG,
    MRI,
    EYE_TRACKING,
    QUESTIONNAIRE
}

data class DetectionResultsUiState(
    val isLoading: Boolean = true,
    val modelResults: List<ModelResult> = emptyList(),
    val finalProbability: Int = 0          // average of available models
)

// ====================== ViewModel ======================

class DetectionResultsViewModel(
    private val dataStore: AdheraDataStore
) : ViewModel() {

    val uiState = combine(
        dataStore.questionnaireResult,
        dataStore.mriResult,
        dataStore.eegResult,
        dataStore.facialResult,
        dataStore.eyeTrackingResult
    ) { questionnaire, mri, eeg, facial, eyeTracking ->

        val results = mutableListOf<ModelResult>()

        // --- Questionnaire ---
        // status is non-empty → was saved
        // prediction is 0 or 1; treat prediction==1 as "positive" probability
        // The API returns prediction (0/1). We store it raw.
        // For display we show probability as (prediction * 100) but we need to
        // check how the questionnaire endpoint really works.
        // Based on QuestionnaireResponse the field is just "prediction" (0 or 1),
        // so we derive a fake 0%/100% is not useful – skip if no real probability.
        // ► We treat questionnaire as "available" when status != "" and prediction != -1
        //   and show probability based on prediction (0→0%, 1→100%) which is a binary.
        //   If you later add a probability field to questionnaire, swap it in here.
        if (questionnaire.first.isNotEmpty() && questionnaire.second != -1) {
            val pct = if (questionnaire.second == 1) 100 else 0
            results += ModelResult(
                title      = "Questionnaire",
                percentage = pct,
                iconType   = ModelIconType.QUESTIONNAIRE
            )
        }

        // --- MRI ---
        if (mri.status.isNotEmpty() && mri.prediction != -1) {
            val pct = (mri.probability * 100).roundToInt().coerceIn(0, 100)
            results += ModelResult(
                title      = "MRI Scan",
                percentage = pct,
                iconType   = ModelIconType.MRI
            )
        }

        // --- EEG ---
        if (eeg.status.isNotEmpty() && eeg.prediction != -1) {
            val pct = (eeg.probability * 100).roundToInt().coerceIn(0, 100)
            results += ModelResult(
                title      = "EEG Analysis",
                percentage = pct,
                iconType   = ModelIconType.EEG
            )
        }

        // --- Facial ---
        if (facial.status.isNotEmpty() && facial.engagementLevel != -1) {
            val pct = (facial.confidence * 100).roundToInt().coerceIn(0, 100)
            results += ModelResult(
                title      = "Engagement",
                percentage = pct,
                iconType   = ModelIconType.ENGAGEMENT
            )
        }

        // --- Eye Tracking ---
        if (eyeTracking.status.isNotEmpty() && eyeTracking.prediction != -1) {
            val pct = (eyeTracking.probability * 100).roundToInt().coerceIn(0, 100)
            results += ModelResult(
                title      = "Focus Persistence",
                percentage = pct,
                iconType   = ModelIconType.EYE_TRACKING
            )
        }

        val finalProbability = if (results.isEmpty()) 0
        else results.sumOf { it.percentage } / results.size

        DetectionResultsUiState(
            isLoading        = false,
            modelResults     = results,
            finalProbability = finalProbability
        )

    }.stateIn(
        scope            = viewModelScope,
        started          = SharingStarted.WhileSubscribed(5_000),
        initialValue     = DetectionResultsUiState(isLoading = true)
    )
}

// ====================== Factory ======================

class DetectionResultsViewModelFactory(
    private val dataStore: AdheraDataStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == DetectionResultsViewModel::class.java)
        return DetectionResultsViewModel(dataStore) as T
    }
}