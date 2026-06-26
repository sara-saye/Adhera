package com.gpproject.adhera.detection.screens.medical

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.ui.components.UploadBox
import com.gpproject.adhera.ui.theme.*

@Composable
fun EegScreen(
    onNavigateBack: () -> Unit = {},
    onFinished: () -> Unit = {},          // ← أضفنا الـ callback ده
    viewModel: EegViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedFile by viewModel.selectedFile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // لما الـ API يخلص (Success أو Error) روح للشاشة الجاية
    LaunchedEffect(uiState) {
        if (uiState is PredictionUiState.Success || uiState is PredictionUiState.Error) {
            onFinished()
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.selectFile(file)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Electroencephalogram (EEG)",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "EEG tracks and records electrical activity patterns and brainwave signals via small sensors.\nIt is widely used to monitor cognitive fluctuations, neurological disorders, and real-time brain states.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            UploadBox(
                label = "Upload EEG Signal Data File",
                uploadedFileName = selectedFile?.name,
                onUploadClick = { filePickerLauncher.launch("*/*") }
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Loading indicator لما الـ API بيشتغل
            if (uiState is PredictionUiState.Loading) {
                CircularProgressIndicator(
                    color = ButtonPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                )
            } else {
                Button(
                    onClick = { viewModel.predictEeg() },
                    enabled = selectedFile != null,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Finish",
                        color = CardBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}