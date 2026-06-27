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
    val percentage: Int,
    val iconType: ModelIconType
)

enum class ModelIconType {
    ENGAGEMENT,
    EEG,
    MRI,
    EYE_TRACKING,
    QUESTIONNAIRE
}

data class DetectionResultsUiState(
    val isLoading: Boolean = true,
    val modelResults: List<ModelResult> = emptyList(),
    val finalProbability: Int = 0
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

        // ================= Questionnaire =================
        if (
            questionnaire.first.isNotEmpty()
            && questionnaire.second != -1
        ) {

            // API بيرجع prediction فقط
            val pct =
                if (questionnaire.second == 1) 100
                else 0

            results += ModelResult(
                title = "Questionnaire",
                percentage = pct,
                iconType = ModelIconType.QUESTIONNAIRE
            )
        }

        // ================= MRI =================
        if (
            mri.status.isNotEmpty()
            && mri.prediction != -1
        ) {

            val pct = mri.probability
                .roundToInt()
                .coerceIn(0,100)

            results += ModelResult(
                title = "MRI Scan",
                percentage = pct,
                iconType = ModelIconType.MRI
            )
        }

        // ================= EEG =================
        if (
            eeg.status.isNotEmpty()
            && eeg.prediction != -1
        ) {

            val pct = eeg.probability
                .roundToInt()
                .coerceIn(0,100)

            results += ModelResult(
                title = "EEG Analysis",
                percentage = pct,
                iconType = ModelIconType.EEG
            )
        }

        // ================= Facial =================
        if (
            facial.status.isNotEmpty()
            && facial.engagementLevel != -1
        ) {

            // confidence من 0→1
            val pct = (facial.confidence * 100)
                .roundToInt()
                .coerceIn(0,100)

            results += ModelResult(
                title = "Engagement",
                percentage = pct,
                iconType = ModelIconType.ENGAGEMENT
            )
        }

        // ================= Eye Tracking =================
        if (
            eyeTracking.status.isNotEmpty()
            && eyeTracking.prediction != -1
        ) {

            val pct = eyeTracking.probability
                .roundToInt()
                .coerceIn(0,100)

            results += ModelResult(
                title = "Focus Persistence",
                percentage = pct,
                iconType = ModelIconType.EYE_TRACKING
            )
        }

        // ================= Final Probability =================

        val finalProbability =
            if (results.isEmpty()) {
                0
            } else {
                results.sumOf { it.percentage } / results.size
            }

        DetectionResultsUiState(
            isLoading = false,
            modelResults = results,
            finalProbability = finalProbability
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetectionResultsUiState(
            isLoading = true
        )
    )
}

// ====================== Factory ======================

class DetectionResultsViewModelFactory(
    private val dataStore: AdheraDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        require(
            modelClass == DetectionResultsViewModel::class.java
        )

        return DetectionResultsViewModel(
            dataStore
        ) as T
    }
}