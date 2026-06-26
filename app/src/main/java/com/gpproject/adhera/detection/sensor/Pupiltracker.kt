package com.gpproject.adhera.detection.sensor

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume

class PupilTracker(private val context: Context) {

    private val TAG = "PupilTracker"

    // ── ML Kit ─────────────────────────────────────────────
    private val faceDetector: FaceDetector by lazy {
        val opts = FaceDetectorOptions.Builder()
            .setPerformanceMode(
                FaceDetectorOptions.PERFORMANCE_MODE_FAST
            )
            .setContourMode(
                FaceDetectorOptions.CONTOUR_MODE_ALL
            )
            .setMinFaceSize(0.15f)
            .build()

        FaceDetection.getClient(opts)
    }

    // ── Camera ─────────────────────────────────────────────
    private var cameraProvider: ProcessCameraProvider? = null
    private val cameraExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()

    // ── State ──────────────────────────────────────────────
    val pupilSamples = mutableListOf<Double>()

    private var activeRecording: Recording? = null
    private var videoOutputFile: File? = null

    // مهم جدًا
    private var stopCallback: ((File?) -> Unit)? = null

    // ───────────────────────────────────────────────────────

    fun startTracking(
        lifecycleOwner: LifecycleOwner
    ) {
        val future =
            ProcessCameraProvider.getInstance(context)

        future.addListener({

            cameraProvider = future.get()

            bindUseCases(
                lifecycleOwner
            )

        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * ننتظر التسجيل يخلص فعلاً قبل إرجاع الملف
     */
    suspend fun stopTracking(): File? =
        suspendCancellableCoroutine { continuation ->

            stopCallback = { file ->
                continuation.resume(file)
            }

            activeRecording?.stop()

            activeRecording = null

            cameraProvider?.unbindAll()
        }

    fun resetSamples() {
        pupilSamples.clear()
    }

    fun computeBlockMetrics(): PupilBlockMetrics {

        if (pupilSamples.isEmpty())
            return PupilBlockMetrics()

        val sorted =
            pupilSamples.sorted()

        val diffs =
            pupilSamples.zipWithNext { a, b ->
                b - a
            }

        val neg =
            diffs.filter { it < 0 }

        val pos =
            diffs.filter { it > 0 }

        return PupilBlockMetrics(
            maxPupil = sorted.last(),

            medianPupil =
                sorted[sorted.size / 2],

            maxContraction =
                neg.minOrNull() ?: 0.0,

            meanContraction =
                neg.averageOrNull(),

            maxDilation =
                pos.maxOrNull() ?: 0.0,

            meanDilation =
                pos.averageOrNull()
        )
    }

    // ───────────────────────────────────────────────────────

    private fun bindUseCases(
        lifecycleOwner: LifecycleOwner
    ) {

        val provider =
            cameraProvider ?: return

        val analysis =
            ImageAnalysis.Builder()
                .setBackpressureStrategy(
                    ImageAnalysis
                        .STRATEGY_KEEP_ONLY_LATEST
                )
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        ::processFrame
                    )
                }

        // أقل جودة لتقليل حجم الملف
        val recorder =
            Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.LOWEST
                    )
                )
                .build()

        val videoCapture =
            VideoCapture.withOutput(
                recorder
            )

        val outputFile =
            File(
                context.cacheDir,
                "facial_${
                    System.currentTimeMillis()
                }.mp4"
            )

        videoOutputFile = outputFile

        try {

            provider.unbindAll()

            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                analysis,
                videoCapture
            )

            Log.d(
                TAG,
                "Camera bound"
            )

            val outputOptions =
                FileOutputOptions
                    .Builder(outputFile)
                    .build()

            activeRecording =
                videoCapture.output
                    .prepareRecording(
                        context,
                        outputOptions
                    )
                    .start(
                        ContextCompat
                            .getMainExecutor(
                                context
                            )
                    ) { event ->

                        when (event) {

                            is VideoRecordEvent.Start -> {

                                Log.d(
                                    TAG,
                                    "Recording started"
                                )
                            }

                            is VideoRecordEvent.Finalize -> {

                                if (event.hasError()) {

                                    Log.e(
                                        TAG,
                                        "Recording failed"
                                    )

                                    stopCallback?.invoke(
                                        null
                                    )

                                } else {

                                    Log.d(
                                        TAG,
                                        "Saved: ${outputFile.length()} bytes"
                                    )

                                    stopCallback?.invoke(
                                        outputFile
                                    )
                                }

                                stopCallback = null
                            }
                        }
                    }

        } catch (e: Exception) {

            Log.e(
                TAG,
                e.message ?: ""
            )

            videoOutputFile = null
        }
    }

    // ───────────────────────────────────────────────────────

    @OptIn(
        ExperimentalGetImage::class
    )
    private fun processFrame(
        imageProxy: ImageProxy
    ) {

        val mediaImage =
            imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image =
            InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

        faceDetector
            .process(image)

            .addOnSuccessListener { faces ->

                faces.firstOrNull()?.let {

                    val d =
                        estimatePupilDiameter(
                            it
                        )

                    if (d > 0.0) {

                        synchronized(
                            pupilSamples
                        ) {
                            pupilSamples.add(
                                d
                            )
                        }
                    }
                }
            }

            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun estimatePupilDiameter(
        face: Face
    ): Double {

        val contour =
            (
                    face.getContour(
                        FaceContour.LEFT_EYE
                    )

                        ?: face.getContour(
                            FaceContour.RIGHT_EYE
                        )

                    )?.points ?: return 0.0

        if (contour.isEmpty())
            return 0.0

        val span =
            contour.maxOf {
                it.y
            } -
                    contour.minOf {
                        it.y
                    }

        val faceHeight =
            face.boundingBox.height()
                .toDouble()

        return if (faceHeight > 0)
            span / faceHeight
        else
            0.0
    }

    fun release() {

        faceDetector.close()

        cameraExecutor.shutdown()
    }
}

data class PupilBlockMetrics(
    val maxPupil: Double = 0.0,
    val medianPupil: Double = 0.0,
    val maxContraction: Double = 0.0,
    val meanContraction: Double = 0.0,
    val maxDilation: Double = 0.0,
    val meanDilation: Double = 0.0
)

private fun List<Double>.averageOrNull(): Double {
    return if (isEmpty()) 0.0 else average()
}