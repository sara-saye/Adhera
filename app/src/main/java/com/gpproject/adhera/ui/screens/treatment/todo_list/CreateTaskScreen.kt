package com.gpproject.adhera.ui.screens.treatment.todo_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.ui.components.HeaderWithBack
import com.gpproject.adhera.ui.components.PrimaryButton
import com.gpproject.adhera.ui.components.adheraScreenPadding
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: TaskViewModel = viewModel(),
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .adheraScreenPadding()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderWithBack(
            title = "Create New Task",
            onBack = onBack
        )

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedIconButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NavyPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Select Date")
                }

                OutlinedIconButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = NavyPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Select Time")
                }
            }

            if (description.length > 20) {
                Button(
                    onClick = { viewModel.splitTaskWithGemini(description) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Steps with AI")
                }
            }

            Text(
                text = "Task Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = NavyPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            viewModel.generatedSubTasks.forEach { subTask ->
                EditableSubTaskItem(
                    subTask = subTask,
                    onTitleChange = { newTitle ->
                        // هنا Logic التعديل
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Create Task",
                onClick = { /* Logic الحفظ */ }
            )
        }
    }
}

@Composable
fun EditableSubTaskItem(subTask: Any, onTitleChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
    ) {
        TextField(
            value = "Step title here",
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

// الـ Preview لازم تكون بره خالص كدة
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateTaskScreenPreview() {
    AdheraTheme {
        CreateTaskScreen(
            onBack = { /* Nothing */ }
        )
    }
}