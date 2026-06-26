package com.gpproject.adhera.detection.sensor

import com.gpproject.adhera.detection.screens.focustest.TrialResult

/**
 * Collects pupil + performance features per block during the Focus Test.
 *
 * The model expects 80 features ordered exactly as in feature_order.json:
 *   [max_pupil_1, median_pupil_1, max_contraction_1, mean_contraction_1,
 *    max_dilation_1, mean_dilation_1, low_load_correct_1, low_load_incorrect_1,
 *    high_load_correct_1, high_load_incorrect_1,
 *    ... repeated for blocks 2-8]
 *
 * Usage:
 *  1. Call [onBlockStart] when a new trial begins (passes control to PupilTracker).
 *  2. Call [onBlockEnd] with the TrialResult when the user answers.
 *  3. Call [buildFeatureVector] after all 8 blocks → List<Double> of length 80.
 */
class EyeTrackingFeatureCollector(
    private val pupilTracker: PupilTracker
) {
    data class BlockFeatures(
        // Pupil (approximated from camera)
        val maxPupil: Double,
        val medianPupil: Double,
        val maxContraction: Double,
        val meanContraction: Double,
        val maxDilation: Double,
        val meanDilation: Double,
        // Performance (from trial result)
        val lowLoadCorrect: Int,
        val lowLoadIncorrect: Int,
        val highLoadCorrect: Int,
        val highLoadIncorrect: Int,
    )

    private val blockFeatures = mutableMapOf<Int, BlockFeatures>() // blockIndex 1-8

    /** Call at the start of each trial/block — resets pupil sample buffer. */
    fun onBlockStart() {
        pupilTracker.resetSamples()
    }

    /**
     * Call when trial ends (user answered or timed out).
     * [blockIndex] is 1-based (1..8), matching feature_order.json.
     */
    fun onBlockEnd(blockIndex: Int, result: TrialResult) {
        val pupil = pupilTracker.computeBlockMetrics()

        val isCorrect = result.isCorrect == true

        // low_load = load 1, high_load = load 2
        val lowLoadCorrect   = if (result.load == 1 &&  isCorrect) 1 else 0
        val lowLoadIncorrect = if (result.load == 1 && !isCorrect) 1 else 0
        val highLoadCorrect  = if (result.load == 2 &&  isCorrect) 1 else 0
        val highLoadIncorrect= if (result.load == 2 && !isCorrect) 1 else 0

        blockFeatures[blockIndex] = BlockFeatures(
            maxPupil        = pupil.maxPupil,
            medianPupil     = pupil.medianPupil,
            maxContraction  = pupil.maxContraction,
            meanContraction = pupil.meanContraction,
            maxDilation     = pupil.maxDilation,
            meanDilation    = pupil.meanDilation,
            lowLoadCorrect  = lowLoadCorrect,
            lowLoadIncorrect= lowLoadIncorrect,
            highLoadCorrect = highLoadCorrect,
            highLoadIncorrect= highLoadIncorrect,
        )
    }

    /**
     * Builds the 80-feature vector in the exact order of feature_order.json.
     * Missing blocks get zeros.
     */
    fun buildFeatureVector(): List<Double> {
        val features = mutableListOf<Double>()
        for (block in 1..8) {
            val b = blockFeatures[block]
            features += listOf(
                b?.maxPupil         ?: 0.0,
                b?.medianPupil      ?: 0.0,
                b?.maxContraction   ?: 0.0,
                b?.meanContraction  ?: 0.0,
                b?.maxDilation      ?: 0.0,
                b?.meanDilation     ?: 0.0,
                (b?.lowLoadCorrect  ?: 0).toDouble(),
                (b?.lowLoadIncorrect?: 0).toDouble(),
                (b?.highLoadCorrect ?: 0).toDouble(),
                (b?.highLoadIncorrect?: 0).toDouble(),
            )
        }
        return features // length = 80
    }

    fun reset() {
        blockFeatures.clear()
    }
}