package com.gpproject.adhera.doctor.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SaveResultUiState(
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class DoctorViewModel(
    private val repository: DoctorRepository
) : ViewModel() {
    val patients: StateFlow<List<PatientEntity>> = repository.observePatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val patientsWithResults: StateFlow<List<PatientWithResults>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.observePatientsWithResults()
            } else {
                repository.searchPatientsWithResults(query.trim())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _saveState = MutableStateFlow(SaveResultUiState())
    val saveState: StateFlow<SaveResultUiState> = _saveState.asStateFlow()

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun saveResultForExistingPatient(patientId: Long, testType: String, testResult: String) {
        viewModelScope.launch {
            runSave {
                repository.saveResultForExistingPatient(patientId, testType, testResult)
            }
        }
    }

    fun createPatientAndSaveResult(patientName: String, testType: String, testResult: String) {
        val trimmedName = patientName.trim()
        if (trimmedName.isBlank()) {
            _saveState.value = SaveResultUiState(errorMessage = "Patient name is required")
            return
        }

        viewModelScope.launch {
            runSave {
                repository.createPatientAndSaveResult(trimmedName, testType, testResult)
            }
        }
    }

    fun deletePatient(patient: PatientEntity) {
        viewModelScope.launch {
            try {
                repository.deletePatient(patient)
            } catch (e: Exception) {
                _saveState.value = SaveResultUiState(errorMessage = e.message ?: "Could not delete patient")
            }
        }
    }

    fun clearSaveMessage() {
        _saveState.value = _saveState.value.copy(successMessage = null, errorMessage = null)
    }

    private suspend fun runSave(block: suspend () -> Unit) {
        _saveState.value = SaveResultUiState(isSaving = true)
        try {
            block()
            _saveState.value = SaveResultUiState(successMessage = "Test result saved successfully")
        } catch (e: Exception) {
            _saveState.value = SaveResultUiState(errorMessage = e.message ?: "Could not save result")
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dao = DoctorDatabase.getDatabase(context).doctorDao()
                return DoctorViewModel(DoctorRepository(dao)) as T
            }
        }
    }
}
