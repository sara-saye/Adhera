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
import com.example.memorymatrix.ui.theme.NeonOrange
import com.example.memorymatrix.ui.theme.CellBase

@Composable
fun InstructionScreen(
    onBackClick: () -> Unit
) {
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
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HOW TO PLAY",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Train your working memory with precision.",
                    color = Color(0xFF8D99AE),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                InstructionItem(
                    number = "1",
                    title = "Memorize the Pattern",
                    description = "At the start of each round, specific cells will light up in vibrant Blue. Remember their positions quickly!",
                    color = NeonBlue
                )

                InstructionItem(
                    number = "2",
                    title = "Beware of Distractors",
                    description = "In higher levels, Orange cells might appear. Do NOT click them; they are only there to distract you.",
                    color = NeonOrange
                )

                InstructionItem(
                    number = "3",
                    title = "Replicate the Matrix",
                    description = "Once the tiles fade, click the exact correct positions. You are only allowed to click as many times as the required pattern size.",
                    color = Color.White
                )
            }

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = CellBase.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "BACK TO MENU",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun InstructionItem(
    number: String,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CellBase.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = number,
            color = color,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color(0xFF8D99AE),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
