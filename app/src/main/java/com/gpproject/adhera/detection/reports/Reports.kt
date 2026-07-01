package com.gpproject.adhera.detection.reports


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gpproject.adhera.R
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.doctor.data.DoctorViewModel
import com.gpproject.adhera.doctor.data.PatientEntity
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ====================== Screen ======================

@Composable
fun DetectionResultsScreen(
    onDone: () -> Unit,
    doctorViewModel: DoctorViewModel? = null,
    onReturnHome: () -> Unit = onDone,
    onNewTest: () -> Unit = onDone
) {
    val context = LocalContext.current
    val dataStore = remember { AdheraDataStore(context) }
    val reportHistoryRepository = remember { DetectionReportHistoryRepository() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveStep by remember { mutableStateOf(ResultSaveStep.Choose) }
    var patientName by remember { mutableStateOf("") }

    val viewModel: DetectionResultsViewModel = viewModel(
        factory = DetectionResultsViewModelFactory(dataStore)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val saveState by doctorViewModel?.saveState?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(null) }
    val patients by doctorViewModel?.patients?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(emptyList()) }

    LaunchedEffect(saveState?.successMessage) {
        val message = saveState?.successMessage ?: return@LaunchedEffect
        saveStep = ResultSaveStep.Saved
        snackbarHostState.showSnackbar(message)
        doctorViewModel?.clearSaveMessage()
    }

    LaunchedEffect(saveState?.errorMessage) {
        val message = saveState?.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        doctorViewModel?.clearSaveMessage()
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .adheraScreenPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedEntrance(delayMillis = 100) {
                Surface(
                    color = NavyLight,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "ANALYSIS COMPLETED",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedEntrance(delayMillis = 150) {
                Text(
                    text = "Clinical Detection\nResults",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Cards — only the models that were actually run
        if (uiState.modelResults.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.modelResults.forEach { result ->
                    AnimatedEntrance(delayMillis = 250) {
                        ResultItemCard(
                            title      = result.title,
                            percentage = "${result.percentage}%",
                            icon       = result.iconType.toIcon()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Final Assessment Card
        AnimatedEntrance(delayMillis = 400) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyPrimary,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Final Assessment",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The overall calculated probability represents the combined weight of all detection modules.",
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "ADHD PROBABILITY",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )

                            Text(
                                text = "${uiState.finalProbability}%",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = uiState.finalProbability / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = NavyPrimary,
                                trackColor = NavyLight
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Done Button
        AnimatedEntrance(delayMillis = 500) {
            PrimaryButton(
                text = "Done & Save Results",
                onClick = {
                    if (doctorViewModel == null) {
                        scope.launch {
                            val savedCount = dataStore.reportSaveCount.first()
                            if (savedCount == 0) {
                                dataStore.markReportSaved()
                            } else {
                                reportHistoryRepository.saveAdditionalReport(uiState)
                                    .onSuccess { reportNumber ->
                                        dataStore.markReportSaved()
                                    }
                                    .onFailure { error ->
                                    }
                            }
                            onDone()
                        }
                    } else {
                        showSaveDialog = true
                        saveStep = ResultSaveStep.Choose
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (showSaveDialog && doctorViewModel != null) {
        SaveResultDialog(
            step = saveStep,
            patients = patients,
            patientName = patientName,
            isSaving = saveState?.isSaving == true,
            resultSummary = uiState.toDoctorResultSummary(),
            onDismiss = {
                if (saveState?.isSaving != true) {
                    showSaveDialog = false
                    patientName = ""
                }
            },
            onExistingPatient = { saveStep = ResultSaveStep.ExistingPatient },
            onNewPatient = { saveStep = ResultSaveStep.NewPatient },
            onPatientNameChange = { patientName = it },
            onSaveExisting = { patient ->
                doctorViewModel.saveResultForExistingPatient(
                    patientId = patient.patientId,
                    testType = "ADHD Detection",
                    testResult = uiState.toDoctorResultSummary()
                )
            },
            onSaveNew = {
                doctorViewModel.createPatientAndSaveResult(
                    patientName = patientName,
                    testType = "ADHD Detection",
                    testResult = uiState.toDoctorResultSummary()
                )
            },
            onReturnHome = onReturnHome,
            onNewTest = onNewTest
        )
    }
}

private enum class ResultSaveStep {
    Choose,
    ExistingPatient,
    NewPatient,
    Saved
}

@Composable
private fun SaveResultDialog(
    step: ResultSaveStep,
    patients: List<PatientEntity>,
    patientName: String,
    isSaving: Boolean,
    resultSummary: String,
    onDismiss: () -> Unit,
    onExistingPatient: () -> Unit,
    onNewPatient: () -> Unit,
    onPatientNameChange: (String) -> Unit,
    onSaveExisting: (PatientEntity) -> Unit,
    onSaveNew: () -> Unit,
    onReturnHome: () -> Unit,
    onNewTest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (step) {
                    ResultSaveStep.Choose -> "Save test result"
                    ResultSaveStep.ExistingPatient -> "Choose patient"
                    ResultSaveStep.NewPatient -> "New patient"
                    ResultSaveStep.Saved -> "Result saved"
                }
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (step) {
                    ResultSaveStep.Choose -> {
                        Text("هل تريد حفظ نتيجة هذا الاختبار لمريض موجود أم إنشاء مريض جديد؟")
                        Text(resultSummary, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    ResultSaveStep.ExistingPatient -> {
                        if (patients.isEmpty()) {
                            Text("No patients yet", color = TextSecondary)
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 280.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(patients, key = { it.patientId }) { patient ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !isSaving) { onSaveExisting(patient) },
                                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                                    ) {
                                        Text(
                                            text = patient.patientName,
                                            modifier = Modifier.padding(14.dp),
                                            color = NavyPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    ResultSaveStep.NewPatient -> {
                        OutlinedTextField(
                            value = patientName,
                            onValueChange = onPatientNameChange,
                            label = { Text("Patient name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        )
                    }
                    ResultSaveStep.Saved -> {
                        Text("Test result saved successfully.")
                    }
                }

                if (isSaving) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text("Saving...", color = TextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            when (step) {
                ResultSaveStep.Choose -> {
                    TextButton(onClick = onExistingPatient, enabled = !isSaving) {
                        Text("Existing Patient")
                    }
                }
                ResultSaveStep.NewPatient -> {
                    TextButton(
                        onClick = onSaveNew,
                        enabled = !isSaving && patientName.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
                ResultSaveStep.Saved -> {
                    TextButton(onClick = onReturnHome) {
                        Text("Back to Home")
                    }
                }
                ResultSaveStep.ExistingPatient -> {}
            }
        },
        dismissButton = {
            when (step) {
                ResultSaveStep.Choose -> {
                    TextButton(onClick = onNewPatient, enabled = !isSaving) {
                        Text("New Patient")
                    }
                }
                ResultSaveStep.ExistingPatient,
                ResultSaveStep.NewPatient -> {
                    TextButton(onClick = onDismiss, enabled = !isSaving) {
                        Text("Cancel")
                    }
                }
                ResultSaveStep.Saved -> {
                    TextButton(onClick = onNewTest) {
                        Text("New Test")
                    }
                }
            }
        }
    )
}

private fun DetectionResultsUiState.toDoctorResultSummary(): String {
    val modelLines = modelResults.joinToString(separator = "\n") { result ->
        "${result.title}: ${result.percentage}%"
    }

    return buildString {
        append("Final ADHD probability: ")
        append(finalProbability)
        append("%")
        if (modelLines.isNotBlank()) {
            append("\n")
            append(modelLines)
        }
    }
}

// ====================== Icon Mapper ======================

private fun ModelIconType.toIcon(): ImageVector = when (this) {
    ModelIconType.ENGAGEMENT    -> Icons.Default.Bolt
    ModelIconType.EEG           -> Icons.Default.Psychology
    ModelIconType.MRI           -> Icons.Default.Visibility
    ModelIconType.EYE_TRACKING  -> Icons.Default.Timer
    ModelIconType.QUESTIONNAIRE -> Icons.Default.Assignment
}

// ====================== Result Item Card ======================

@Composable
fun ResultItemCard(
    title: String,
    percentage: String,
    icon: ImageVector
) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DividerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyLight.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reliability Score",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }

            Text(
                text = percentage,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = NavyPrimary
            )
        }
    }
}

// ====================== Detection Complete Screen ======================

@Composable
fun DetectionCompleteScreen(
    onViewReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        AnimatedEntrance(delayMillis = 100) {
            Text(
                text = "Analysis Complete",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = NavyPrimary
        )

        Spacer(modifier = Modifier.height(80.dp))

        AnimatedEntrance(delayMillis = 200) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.brain)
                    )

                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(220.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Your Results Are Ready",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "We analyzed your responses and focus patterns. Your detailed report is ready to view.",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    PrimaryButton(
                        text = "View My Report",
                        onClick = onViewReport
                    )
                }
            }
        }
    }
}
