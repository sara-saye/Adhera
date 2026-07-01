package com.gpproject.adhera.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.auth.FirebaseRepository
import com.gpproject.adhera.auth.firebase.FirebaseModule
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.detection.reports.DetectionReportHistoryRepository
import com.gpproject.adhera.detection.reports.DetectionResultsUiState
import com.gpproject.adhera.detection.reports.DiagnosticReportHistoryItem
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AccountSettingsUiState(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

data class DiagnosticArchiveUiState(
    val reports: List<DiagnosticReportHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val message: String? = null,
    val error: String? = null
)

class AccountSettingsViewModel(
    private val repository: FirebaseRepository = FirebaseRepository(
        FirebaseModule.auth,
        FirebaseModule.firestore
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountSettingsUiState(isLoading = true))
    val uiState: StateFlow<AccountSettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getCurrentUserProfile()
                .onSuccess { profile ->
                    _uiState.value = AccountSettingsUiState(
                        nickname = profile.nickname ?: profile.name.orEmpty(),
                        email = profile.email
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Could not load account"
                    )
                }
        }
    }

    fun onNicknameChange(value: String) {
        _uiState.value = _uiState.value.copy(nickname = value, message = null, error = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, message = null, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, message = null, error = null)
    }

    fun saveChanges() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.value = state.copy(error = "Email address is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, message = null, error = null)

            repository.updateAccountProfile(
                nickname = state.nickname,
                email = state.email,
                password = state.password.takeIf { it.isNotBlank() }
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        password = "",
                        message = "Account changes saved"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Could not save account changes"
                    )
                }
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)

            repository.deleteCurrentAccount()
                .onSuccess {
                    _uiState.value = AccountSettingsUiState()
                    onDeleted()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Could not delete account"
                    )
                }
        }
    }
}

class DiagnosticArchiveViewModel(
    private val dataStore: AdheraDataStore,
    private val repository: DetectionReportHistoryRepository = DetectionReportHistoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticArchiveUiState(isLoading = true))
    val uiState: StateFlow<DiagnosticArchiveUiState> = _uiState.asStateFlow()

    private var lastDocument: DocumentSnapshot? = null

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = DiagnosticArchiveUiState(isLoading = true)
            lastDocument = null

            val firstReport = currentDataStoreReport()
            repository.loadReports()
                .onSuccess { (items, last) ->
                    lastDocument = last
                    _uiState.value = DiagnosticArchiveUiState(
                        reports = listOfNotNull(firstReport) + items,
                        canLoadMore = items.isNotEmpty()
                    )
                }
                .onFailure { error ->
                    _uiState.value = DiagnosticArchiveUiState(
                        reports = listOfNotNull(firstReport),
                        canLoadMore = false,
                        error = error.message ?: "Could not load report history"
                    )
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoadingMore = true, error = null)

            repository.loadReports(after = lastDocument)
                .onSuccess { (items, last) ->
                    lastDocument = last
                    _uiState.value = _uiState.value.copy(
                        reports = _uiState.value.reports + items,
                        isLoadingMore = false,
                        canLoadMore = items.isNotEmpty()
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        error = error.message ?: "Could not load older records"
                    )
                }
        }
    }

    fun deleteReport(reportId: String) {
        if (reportId == FIRST_REPORT_ID) return

        viewModelScope.launch {
            repository.deleteReport(reportId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        reports = _uiState.value.reports.filterNot { it.id == reportId },
                        message = "Report deleted"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Could not delete report"
                    )
                }
        }
    }

    private suspend fun currentDataStoreReport(): DiagnosticReportHistoryItem? {
        val questionnaire = dataStore.questionnaireResult.first()
        val mri = dataStore.mriResult.first()
        val eeg = dataStore.eegResult.first()
        val facial = dataStore.facialResult.first()
        val eyeTracking = dataStore.eyeTrackingResult.first()

        val uiState = DetectionResultsUiState.fromRawResults(
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

        if (uiState.modelResults.isEmpty()) return null

        return DiagnosticReportHistoryItem(
            id = FIRST_REPORT_ID,
            reportNumber = 1,
            timestamp = 0L,
            finalProbability = uiState.finalProbability,
            modelResults = uiState.modelResults
        )
    }

    companion object {
        const val FIRST_REPORT_ID = "first_report_datastore"
    }
}

class DiagnosticArchiveViewModelFactory(
    private val dataStore: AdheraDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == DiagnosticArchiveViewModel::class.java)
        return DiagnosticArchiveViewModel(dataStore) as T
    }
}
