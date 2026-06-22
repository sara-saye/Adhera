package com.gpproject.adhera.ui.screens.treatment.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R

// ─── Screen 2: Game Introduction / Start Screen ───────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EbbAndFlowIntroScreen(
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onNewGame: () -> Unit = {},
    onHowToPlay: () -> Unit = {},
) {
    // استخدمنا Scaffold و TopAppBar بنفس الطريقة الشغالة في السكرين الأولى لمنع تداخل اللمس تماماً
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Adhera",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GameColors.TextPrimary,
                            ),
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GameColors.TextPrimary)
                        Spacer(Modifier.width(4.dp))
                        Text("Back", color = GameColors.TextPrimary, fontWeight = FontWeight.Medium)
                    }
                },
                actions = {
                    // أيقونة وهمية شفافة في اليمين عشان تحافظ على توازن كلمة Adhera في النص بالظبط
                    IconButton(onClick = {}, enabled = false) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Transparent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        containerColor = GameColors.Background,
        modifier = Modifier.drawBehind { drawDotGrid(this) } // رسم النقط على الخلفية بالكامل
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Centered Title Block + Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "EbbAndFlow",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GameColors.NavyDark,
                            letterSpacing = 2.sp,
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "COGNITIVE FOCUS TRAINING",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GameColors.TextSecondary,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(24.dp))

                    // الصورة بحوافها الدائرية الناعمة واسمها الصحيح
                    Image(
                        painter = painterResource(id = R.drawable.ic_game_logo),
                        contentDescription = "Game Logo Illustration",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(280.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }

            // Buttons Block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                // زرار الـ PLAY
                Button(
                    onClick = onNewGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GameColors.NavyDark),
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "PLAY",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            color = Color.White,
                            fontSize = 22.sp
                        ),
                    )
                }

                // HOW TO PLAY
                Button(
                    onClick = onHowToPlay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GameColors.Background,
                        contentColor = GameColors.TextSecondary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                ) {
                    Icon(Icons.Default.Help, contentDescription = null, tint = GameColors.TextSecondary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "HOW TO PLAY",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = GameColors.TextSecondary,
                        ),
                    )
                }
            }
        }
    }
}

// ─── Dot grid background painter ─────────────────────────────────────────────
private fun drawDotGrid(scope: DrawScope) {
    val dotColor = Color(0xFFB0C0D0).copy(alpha = 0.45f)
    val spacing = 28.dp.value * scope.density
    val dotRadius = 1.5f
    var x = spacing
    while (x < scope.size.width) {
        var y = spacing
        while (y < scope.size.height) {
            scope.drawCircle(color = dotColor, radius = dotRadius, center = Offset(x, y))
            y += spacing
        }
        x += spacing
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun EbbAndFlowIntroPreview() {
    MaterialTheme {
        EbbAndFlowIntroScreen()
    }
}