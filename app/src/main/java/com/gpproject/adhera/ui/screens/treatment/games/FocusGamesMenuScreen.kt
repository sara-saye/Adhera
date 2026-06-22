package com.gpproject.adhera.ui.screens.treatment.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R

// ─── Color Palette ────────────────────────────────────────────────────────────
internal object GameColors {
    val Background        = Color(0xFFF0F4F8)
    val CardBackground    = Color(0xFFFFFFFF)
    val NavyDark          = Color(0xFF1B3A5C)
    val NavyMedium        = Color(0xFF1E4976)
    val IconBackground    = Color(0xFFDDE8F5)
    val TagBackground     = Color(0xFFEDF2FB)
    val TextPrimary       = Color(0xFF1B3A5C)
    val TextSecondary     = Color(0xFF6B7A8D)
    val TagText           = Color(0xFF3A6EA5)
    val StreakActive      = Color(0xFF4CAF50)
    val StreakInactive    = Color(0xFF5A6A7A)
    val PointingGreen     = Color(0xFF4CAF50)
    val MovingOrange      = Color(0xFFFF9800)
    val MovingGray        = Color(0xFF607080)
    val GameBackground    = Color(0xFF162436)
    val GameBackgroundAlt = Color(0xFF1A2B40)
    val LeafPulse         = Color(0xFF2A4A6A)
}

// ─── Data Model ───────────────────────────────────────────────────────────────
data class FocusGame(
    val id: String,
    val title: String,
    val tag: String,
    val description: String,
    val iconRes: Int,
    val tagColor: Color = GameColors.TagText,
)

// تم تحديث مصادر الصور هنا بناءً على أسماء الملفات الجديدة لتطابق الألعاب الأصلية
val focusGamesList = listOf(
    FocusGame(
        id          = "ebb_and_flow",
        title       = "EbbAndFlow",
        tag         = "COGNITIVE SHIFT",
        description = "Train your mental agility by navigating shifting patterns and rhythms.",
        iconRes     = R.drawable.ic_game_logo, // الصورة المحدثة لـ EbbAndFlow
    ),
    FocusGame(
        id          = "memory_matrix",
        title       = "Memory Matrix",
        tag         = "RECALL",
        description = "Strengthen working memory through spatial recognition and sequence tracking.",
        iconRes     = R.drawable.matrix, // الصورة المحدثة لـ Memory Matrix
        tagColor    = Color(0xFF6B7A8D),
    ),
    FocusGame(
        id          = "color_match",
        title       = "Color Match",
        tag         = "REACTION",
        description = "Enhance inhibitory control and processing speed with rapid visual filtering.",
        iconRes     = R.drawable.color, // الصورة المحدثة لـ Color Match
        tagColor    = Color(0xFF8B6A3E),
    ),
)

// ─── Screen 1: Focus Games Menu ───────────────────────────────────────────────
@Composable
fun FocusGamesMenuScreen(
    onBack: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onPlayEbbAndFlow: () -> Unit = {},
    onPlayMemoryMatrix: () -> Unit = {},
    onPlayColorMatch: () -> Unit = {},
) {
    val onPlayActions = mapOf(
        "ebb_and_flow"  to onPlayEbbAndFlow,
        "memory_matrix" to onPlayMemoryMatrix,
        "color_match"   to onPlayColorMatch,
    )

    Scaffold(
        topBar = {
            AdheraTopBar(
                title      = "Adhera",
                onBack     = onBack, // تمرير الأكشن لضمان عمل زر الباك بالكامل
                onSettings = onSettingsClick,
            )
        },
        containerColor = GameColors.Background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text  = "Focus Games",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = GameColors.TextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Sharpen your cognitive clarity with structured play.",
                style = MaterialTheme.typography.bodyMedium.copy(color = GameColors.TextSecondary),
            )

            Spacer(Modifier.height(24.dp))

            focusGamesList.forEach { game ->
                FocusGameCard(
                    game   = game,
                    onPlay = { onPlayActions[game.id]?.invoke() },
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ─── Game Card ────────────────────────────────────────────────────────────────
@Composable
fun FocusGameCard(
    game: FocusGame,
    onPlay: () -> Unit,
) {
    Card(
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = GameColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                // صندوق أيقونة اللعبة المحدثة بحواف دائرية أنيقة ومتناسقة مع الصور الجديدة
                Box(
                    modifier         = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GameColors.IconBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter            = painterResource(id = game.iconRes),
                        contentDescription = game.title,
                        modifier           = Modifier.fillMaxSize(), // تم تعديل الحجم لملء المربع بشكل كامل
                        contentScale       = ContentScale.Crop,      // لتظهر الصور دائرية ومقصوصة بشكل منسق داخل الكرت
                    )
                }

                // Tag Chip
                Surface(
                    shape = CircleShape,
                    color = GameColors.TagBackground,
                ) {
                    Text(
                        text     = game.tag,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            fontWeight    = FontWeight.Bold,
                            color         = game.tagColor,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text  = game.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = GameColors.TextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = game.description,
                style = MaterialTheme.typography.bodySmall.copy(color = GameColors.TextSecondary),
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick  = onPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GameColors.NavyDark),
            ) {
                Text(
                    text  = "Play  ▶",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White,
                    ),
                )
            }
        }
    }
}

// ─── Shared Top Bar ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdheraTopBar(
    title: String,
    onBack: () -> Unit,
    onSettings: () -> Unit,
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text  = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color      = GameColors.TextPrimary,
                    ),
                )
            }
        },
        navigationIcon = {
            // زرار الـ Back لضمان عمل الـ Navigation بسلاسة والـ onClick مربوط بشكل كامل
            TextButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GameColors.TextPrimary)
                Spacer(Modifier.width(4.dp))
                Text("Back", color = GameColors.TextPrimary, fontWeight = FontWeight.Medium)
            }
        },
        actions = {
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = GameColors.TextSecondary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GameColors.Background),
    )
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun FocusGamesMenuPreview() {
    MaterialTheme {
        FocusGamesMenuScreen()
    }
}