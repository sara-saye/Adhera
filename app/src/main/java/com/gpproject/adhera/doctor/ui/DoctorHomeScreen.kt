package com.gpproject.adhera.doctor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpproject.adhera.doctor.data.DoctorViewModel
import com.gpproject.adhera.doctor.data.PatientEntity
import com.gpproject.adhera.doctor.data.PatientWithResults
import com.gpproject.adhera.ui.components.adheraScreenPadding
import com.gpproject.adhera.ui.theme.AppBackground
import com.gpproject.adhera.ui.theme.NavyPrimary
import com.gpproject.adhera.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class DoctorTab(
    val title: String,
    val icon: ImageVector
) {
    DetectionTypes("Detection Types", Icons.Default.Biotech),
    Patients("Patients", Icons.Default.Groups),
    TreatmentTools("Treatment Tools", Icons.Default.Healing)
}

data class DoctorActionCard(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun DoctorHomeScreen(
    doctorViewModel: DoctorViewModel,
    onOpenFullDetection: () -> Unit,
    onOpenEeg: () -> Unit,
    onOpenMri: () -> Unit,
    onOpenFocusTest: () -> Unit,
    onOpenAssessment: () -> Unit,
    onOpenTodo: () -> Unit,
    onOpenHabitTracker: () -> Unit,
    onOpenChatbot: () -> Unit,
    onOpenFocusGames: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(DoctorTab.DetectionTypes) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .adheraScreenPadding(),
        containerColor = AppBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                DoctorTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Doctor Home",
                style = MaterialTheme.typography.headlineMedium,
                color = NavyPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = selectedTab.title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(18.dp))

            when (selectedTab) {
                DoctorTab.DetectionTypes -> DetectionTypesTab(
                    onOpenFullDetection = onOpenFullDetection,
                    onOpenEeg = onOpenEeg,
                    onOpenMri = onOpenMri,
                    onOpenFocusTest = onOpenFocusTest,
                    onOpenAssessment = onOpenAssessment
                )
                DoctorTab.Patients -> PatientsScreen(doctorViewModel)
                DoctorTab.TreatmentTools -> TreatmentHomeScreen(
                    onOpenTodo = onOpenTodo,
                    onOpenHabitTracker = onOpenHabitTracker,
                    onOpenChatbot = onOpenChatbot,
                    onOpenFocusGames = onOpenFocusGames
                )
            }
        }
    }
}

@Composable
private fun DetectionTypesTab(
    onOpenFullDetection: () -> Unit,
    onOpenEeg: () -> Unit,
    onOpenMri: () -> Unit,
    onOpenFocusTest: () -> Unit,
    onOpenAssessment: () -> Unit
) {
    val cards = listOf(
        DoctorActionCard("Full Detection Flow", "Run the existing guided detection sequence.", Icons.Default.Biotech, onOpenFullDetection),
        DoctorActionCard("EEG Analysis", "Open the existing EEG detection screen.", Icons.Default.Psychology, onOpenEeg),
        DoctorActionCard("MRI Scan", "Open the existing MRI detection screen.", Icons.Default.Visibility, onOpenMri),
        DoctorActionCard("Focus Test", "Open the existing focus observation test.", Icons.Default.Videocam, onOpenFocusTest),
        DoctorActionCard("Questionnaire", "Open the existing assessment screen.", Icons.Default.Extension, onOpenAssessment)
    )
    ActionCardList(cards)
}

@Composable
fun TreatmentHomeScreen(
    onOpenTodo: () -> Unit,
    onOpenHabitTracker: () -> Unit,
    onOpenChatbot: () -> Unit,
    onOpenFocusGames: () -> Unit
) {
    val cards = listOf(
        DoctorActionCard("To-Do Treatment", "Open the existing task treatment tool.", Icons.Default.Extension, onOpenTodo),
        DoctorActionCard("Habit Tracker", "Open the existing habit treatment screens.", Icons.Default.Healing, onOpenHabitTracker),
        DoctorActionCard("Chatbot", "Open the existing support chatbot.", Icons.Default.Psychology, onOpenChatbot),
        DoctorActionCard("Focus Games", "Open the existing treatment games menu.", Icons.Default.Biotech, onOpenFocusGames)
    )
    ActionCardList(cards)
}

@Composable
private fun ActionCardList(cards: List<DoctorActionCard>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(cards) { card ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { card.onClick() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(card.icon, contentDescription = null, tint = NavyPrimary)
                    Column(Modifier.weight(1f)) {
                        Text(card.title, color = NavyPrimary, fontWeight = FontWeight.Bold)
                        Text(card.description, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsScreen(viewModel: DoctorViewModel) {
    val patients by viewModel.patientsWithResults.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var expandedPatientId by remember { mutableStateOf<Long?>(null) }
    var patientPendingDelete by remember { mutableStateOf<PatientEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.updateSearchQuery(it)
            },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Search patients") },
            singleLine = true
        )

        Spacer(Modifier.height(14.dp))

        if (patients.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (query.isBlank()) "No patients yet" else "No matching patients",
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(patients, key = { it.patient.patientId }) { item ->
                    PatientCard(
                        item = item,
                        expanded = expandedPatientId == item.patient.patientId,
                        onToggle = {
                            expandedPatientId = if (expandedPatientId == item.patient.patientId) {
                                null
                            } else {
                                item.patient.patientId
                            }
                        },
                        onDelete = { patientPendingDelete = item.patient }
                    )
                }
            }
        }
    }

    patientPendingDelete?.let { patient ->
        AlertDialog(
            onDismissRequest = { patientPendingDelete = null },
            title = { Text("Delete patient?") },
            text = { Text("This will delete the patient and all linked test results.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePatient(patient)
                        patientPendingDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { patientPendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PatientCard(
    item: PatientWithResults,
    expanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.patient.patientName,
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete patient", tint = Color(0xFFB3261E))
                }
            }

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                if (item.results.isEmpty()) {
                    Text("No test results yet", color = TextSecondary)
                } else {
                    item.results.sortedByDescending { it.createdAt }.forEach { result ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(result.testType, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                            Text(result.testResult, color = TextSecondary)
                            Text(formatDate(result.createdAt), color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(value: Long): String {
    return SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date(value))
}
