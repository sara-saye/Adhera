package com.example.memorymatrix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R
import com.example.memorymatrix.ui.theme.DeepSpace
import com.example.memorymatrix.ui.theme.NeonBlue
import com.example.memorymatrix.ui.theme.NeonGreen
import com.example.memorymatrix.ui.theme.NeonRed
import com.example.memorymatrix.ui.theme.CellBase

@Composable
fun SessionSummaryScreen(
    accuracy: Float,
    avgReactionTime: Long,
    onHomeClick: () -> Unit
) {
    val isPassed = accuracy >= 0.7f
    val accuracyPercentage = (accuracy * 100).toInt()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.matrix),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepSpace.copy(alpha = 0.85f),
                            DeepSpace.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PERFORMANCE REPORT",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                // حالة النتيجة (نجاح أو إعادة لتدريب الذاكرة)
                Text(
                    text = if (isPassed) "LEVEL COMPLETED!" else "LEVEL FAILED",
                    color = if (isPassed) NeonGreen else NeonRed,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // كروت عرض الإحصائيات (التقرير الرقمي)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // كارت الدقة
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CellBase.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Average Accuracy", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = "$accuracyPercentage%", color = if (isPassed) NeonGreen else NeonRed, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                // كارت وقت الاستجابة
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CellBase.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Avg Reaction Time", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = "$avgReactionTime ms", color = NeonBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            // زر العودة لشاشة الستارت لتحديث الـ Options
            Button(
                onClick = onHomeClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "CONTINUE TO MENU",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepSpace
                )
            }
        }
    }
}
