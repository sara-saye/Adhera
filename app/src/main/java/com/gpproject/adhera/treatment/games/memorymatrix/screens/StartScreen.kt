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
import com.example.memorymatrix.ui.theme.CellBase

@Composable
fun StartScreen(
    hasSavedGame: Boolean,
    onNewGameClick: () -> Unit,
    onResumeGameClick: () -> Unit,
    onInstructionsClick: () -> Unit
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
                            Color.Black.copy(alpha = 0.45f),
                            DeepSpace.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MEMORY\nMATRIX",
                color = Color.White,
                fontSize = 46.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )

            Text(
                text = "CHALLENGE YOUR COGNITION",
                color = NeonBlue.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            if (hasSavedGame) {
                Button(
                    onClick = onResumeGameClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Text(
                        text = "CONTINUE GAME",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepSpace
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = onNewGameClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                Text(
                    text = "NEW GAME",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepSpace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onInstructionsClick,
                colors = ButtonDefaults.buttonColors(containerColor = CellBase.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                Text(
                    text = "HOW TO PLAY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}
