package com.gpproject.adhera.detection.screens.focustest


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.detection.AdheraRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class FocusTestViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "FocusTestViewModel"

    private val repository = AdheraRepositoryImpl(
        dataStore = AdheraDataStore(application)
    )

    // ── UI State ──────────────────────────────────────────────────────────────

    sealed class SubmitState {
        object Idle : SubmitState()
        object Loading : SubmitState()
        object Success : SubmitState()
        data class Error(val message: String) : SubmitState()
    }

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    // Individual results for showing partial success if needed
    private val _eyeTrackingDone = MutableStateFlow(false)
    val eyeTrackingDone: StateFlow<Boolean> = _eyeTrackingDone

    private val _facialDone = MutableStateFlow(false)
    val facialDone: StateFlow<Boolean> = _facialDone

    // ── Main submit function ──────────────────────────────────────────────────

    /**
     * Called from the screen when the focus test is complete.
     *
     * @param eyeFeatures  80-element list from EyeTrackingFeatureCollector
     * @param facialVideoFile  the .mp4 recorded by PupilTracker (nullable if camera failed)
     */
    fun submitResults(
        eyeFeatures: List<Double>,
        facialVideoFile: File?
    ) {
        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            _eyeTrackingDone.value = false
            _facialDone.value = false

            var anyError = false

            // ── 1. Eye Tracking ───────────────────────────────────────────────
            try {
                Log.d(TAG, "Sending eye tracking features (${eyeFeatures.size} features)...")
                val response = repository.predictEyeTracking(eyeFeatures)
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(
                        TAG,
                        "Eye tracking ✅ → prediction=${body?.prediction}, probability=${body?.probability}"
                    )
                    _eyeTrackingDone.value = true
                } else {
                    Log.e(
                        TAG,
                        "Eye tracking ❌ → HTTP ${response.code()}: ${
                            response.errorBody()?.string()
                        }"
                    )
                    anyError = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Eye tracking exception: ${e.message}", e)
                anyError = true
            }

            // ── 2. Facial ─────────────────────────────────────────────────────
            if (facialVideoFile != null && facialVideoFile.exists()) {
                try {
                    Log.d(
                        TAG,
                        """
                                        Eye features=${eyeFeatures.size}
                                        Video exists=${facialVideoFile?.exists()}
                                        Video size=${facialVideoFile?.length()}
                            """.trimIndent()
                    )
                    Log.d(
                        TAG,
                        "Sending facial video: ${facialVideoFile.name} (${facialVideoFile.length()} bytes)..."
                    )
                    val response = repository.predictFacial(facialVideoFile)
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d(
                            TAG,
                            """
    Facial raw response:
    status=${body?.status}
    engagementLevel=${body?.engagementLevel}
    engagementLabel=${body?.engagementLabel}
    confidence=${body?.confidence}
    message=${body?.message}
    """.trimIndent()
                        )
                        _facialDone.value = true
                    } else {
                        Log.e(
                            TAG,
                            "Facial ❌ → HTTP ${response.code()}: ${response.errorBody()?.string()}"
                        )
                        anyError = true
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Facial exception: ${e.message}", e)
                    anyError = true
                } finally {
                    // Always delete the temp video after upload attempt
                    facialVideoFile.delete()
                    Log.d(TAG, "Temp video deleted: ${facialVideoFile.name}")
                }
            } else {
                Log.w(TAG, "No facial video file — skipping facial API call")
                // Not a hard error — camera might have failed but eye tracking still ran
            }

            // ── Done ──────────────────────────────────────────────────────────
            _submitState.value = if (anyError) {
                SubmitState.Error("Some results may not have been submitted. Check your connection.")
            } else {
                SubmitState.Success
            }
        }
    }

    fun resetState() {
        _submitState.value = SubmitState.Idle
    }
}