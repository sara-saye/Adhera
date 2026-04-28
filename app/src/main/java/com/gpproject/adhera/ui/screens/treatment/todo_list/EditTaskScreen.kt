package com.gpproject.adhera.ui.screens.treatment.todo_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.ui.components.HeaderWithBack
import com.gpproject.adhera.ui.components.PrimaryButton
import com.gpproject.adhera.ui.components.SecondaryButton
import com.gpproject.adhera.ui.components.adheraScreenPadding
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.TaskViewModel

@Composable
fun EditTaskScreen(
    taskId: String,
    viewModel: TaskViewModel = viewModel(),
    onBack: () -> Unit
) {
    // جلب المهمة من الـ ViewModel باستخدام الـ ID
    val task = remember { viewModel.getTaskById(taskId) }

    // State محلي للبيانات القابلة للتعديل
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var reminderEnabled by remember { mutableStateOf(task?.reminderEnabled ?: false) }

    // تحميل الـ Sub-tasks في قائمة الـ ViewModel عند فتح الشاشة
    LaunchedEffect(key1 = taskId) {
        task?.let { viewModel.loadSubTasksForEditing(it.subTasks) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .adheraScreenPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // الهيدر اللي بعتيه في الملفات
        HeaderWithBack(
            title = "Edit Task",
            onBack = onBack
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // 1. تعديل العنوان
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. تعديل الوصف + زر الـ AI
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            if (description.length > 20) {
                TextButton(
                    onClick = { viewModel.regenerateSubTasks(description) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Regenerate with Gemini", color = NavySecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. قسم الـ Sub-tasks (إدارة الخطوات)
            Text(
                text = "Sub-tasks Management",
                style = MaterialTheme.typography.titleMedium,
                color = NavyPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // عرض الخطوات الموجودة حالياً في الـ ViewModel
            viewModel.editingSubTasks.forEach { subTask ->
                SubTaskEditItem(
                    title = subTask.title,
                    onNameChange = { newName -> viewModel.updateSubTaskName(subTask.id, newName) },
                    onDelete = { viewModel.removeSubTask(subTask.id) }
                )
            }

            // 4. زر إضافة خطوة يدوية (من ملف Buttons.kt)
            SecondaryButton(
                text = "+ Add New Step",
                onClick = { viewModel.addNewSubTask() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = DividerColor)

            // 5. سويتش المنبه (Reminder Logic)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Enable Reminders", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Text("Alert 5 mins before start", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = { reminderEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = NavyPrimary)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 6. زر الحفظ النهائي (PrimaryButton)
            PrimaryButton(
                text = "Update Changes",
                onClick = {
                    viewModel.updateTask(
                        taskId = taskId,
                        newTitle = title,
                        newDescription = description,
                        isReminderEnabled = reminderEnabled
                    )
                    onBack()
                }
            )
        }
    }
}

@Composable
fun SubTaskEditItem(
    title: String,
    onNameChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onNameChange,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = ErrorColor)
        }
    }
}