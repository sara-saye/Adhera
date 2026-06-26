package com.gpproject.adhera.detection.screens.medical

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.detection.AdheraRepository
import com.gpproject.adhera.detection.AdheraRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class MriViewModel(
    private val repository: AdheraRepository
) : ViewModel() {

    val _selectedFile = MutableStateFlow<File?>(null)
    val selectedFile: StateFlow<File?> = _selectedFile

    val _uiState = MutableStateFlow<PredictionUiState>(PredictionUiState.Idle)
    val uiState: StateFlow<PredictionUiState> = _uiState

    fun selectFile(file: File) {
        _selectedFile.value = file
    }

    fun predictMri() {
        val file = _selectedFile.value ?: return
        viewModelScope.launch {
            _uiState.value = PredictionUiState.Loading
            try {
                val response = repository.predictMri(file)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = PredictionUiState.Success("Prediction: ${response.body()?.prediction}")
                } else {
                    _uiState.value = PredictionUiState.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = PredictionUiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }

    // ====================== Factory ======================
    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dataStore  = AdheraDataStore(context.applicationContext)
                    val repository = AdheraRepositoryImpl(dataStore = dataStore)
                    return MriViewModel(repository) as T
                }
            }
    }
}