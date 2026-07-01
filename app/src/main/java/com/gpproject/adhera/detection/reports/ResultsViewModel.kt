package com.gpproject.adhera.detection.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.roundToInt

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
) {
    companion object {
        fun fromRawResults(
            questionnaire: Triple<String, Int, String?>,
            mriStatus: String,
            mriPrediction: Int,
            mriProbability: Double,
            eegStatus: String,
            eegPrediction: Int,
            eegProbability: Double,
            facialStatus: String,
            facialEngagementLevel: Int,
            facialConfidence: Double,
            eyeTrackingStatus: String,
            eyeTrackingPrediction: Int,
            eyeTrackingProbability: Double
        ): DetectionResultsUiState {
            val results = mutableListOf<ModelResult>()

            if (questionnaire.first.isCompletedStatus() && questionnaire.second != -1) {
                results += ModelResult(
                    title = "Questionnaire",
                    percentage = if (questionnaire.second == 1) 100 else 0,
                    iconType = ModelIconType.QUESTIONNAIRE
                )
            }

            if (isOptionalMedicalResultComplete(mriStatus, mriPrediction, mriProbability)) {
                results += ModelResult(
                    title = "MRI Scan",
                    percentage = mriProbability.roundToInt().coerceIn(0, 100),
                    iconType = ModelIconType.MRI
                )
            }

            if (isOptionalMedicalResultComplete(eegStatus, eegPrediction, eegProbability)) {
                results += ModelResult(
                    title = "EEG Analysis",
                    percentage = eegProbability.roundToInt().coerceIn(0, 100),
                    iconType = ModelIconType.EEG
                )
            }

            if (facialStatus.isCompletedStatus() && facialEngagementLevel != -1) {
                results += ModelResult(
                    title = "Engagement",
                    percentage = (facialConfidence * 100).roundToInt().coerceIn(0, 100),
                    iconType = ModelIconType.ENGAGEMENT
                )
            }

            if (eyeTrackingStatus.isCompletedStatus() && eyeTrackingPrediction != -1) {
                results += ModelResult(
                    title = "Focus Persistence",
                    percentage = eyeTrackingProbability.roundToInt().coerceIn(0, 100),
                    iconType = ModelIconType.EYE_TRACKING
                )
            }

            return DetectionResultsUiState(
                isLoading = false,
                modelResults = results,
                finalProbability = if (results.isEmpty()) 0 else results.sumOf { it.percentage } / results.size
            )
        }
    }
}

private fun String.isCompletedStatus(): Boolean {
    val normalized = trim().lowercase()
    return normalized.isNotBlank()
        && normalized !in setOf("default", "empty", "null", "none", "not_performed", "not performed", "skipped")
}

private fun isOptionalMedicalResultComplete(
    status: String,
    prediction: Int,
    probability: Double
): Boolean {
    return status.isCompletedStatus()
            && prediction != -1
}

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
        DetectionResultsUiState.fromRawResults(
            questionnaire = questionnaire,
            mriStatus = mri.status,
            mriPrediction = mri.prediction,
            mriProbability = mri.probability,
            eegStatus = eeg.status,
            eegPrediction = eeg.prediction,
            eegProbability = eeg.probability,
            facialStatus = facial.status,
            facialEngagementLevel = facial.engagementLevel,
            facialConfidence = facial.confidence,
            eyeTrackingStatus = eyeTracking.status,
            eyeTrackingPrediction = eyeTracking.prediction,
            eyeTrackingProbability = eyeTracking.probability
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetectionResultsUiState(isLoading = true)
    )
}

class DetectionResultsViewModelFactory(
    private val dataStore: AdheraDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(modelClass == DetectionResultsViewModel::class.java)
        return DetectionResultsViewModel(dataStore) as T
    }
}
