package com.gpproject.adhera.ui.screens.treatment.games.colormatchgame

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gpproject.adhera.ui.screens.treatment.games.colormatchgame.ColorMatchConstants.AccentOrange
import com.gpproject.adhera.ui.screens.treatment.games.colormatchgame.ColorMatchConstants.CardBg
import com.gpproject.adhera.ui.screens.treatment.games.colormatchgame.ColorMatchConstants.TealBtn
import com.gpproject.adhera.ui.screens.treatment.games.colormatchgame.ColorMatchConstants.TextLight
import com.gpproject.adhera.ui.screens.treatment.games.colormatchgame.ColorMatchConstants.TextMuted
import com.gpproject.adhera.ui.screens.treatment.games.colormatchgame.ColorMatchConstants.WoodDark
import kotlinx.coroutines.launch

@Composable
fun WoodGrainOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val lineColor = Color(0x18000000)
        val positions = listOf(0.08f, 0.18f, 0.27f, 0.35f, 0.45f, 0.55f, 0.65f, 0.75f, 0.85f, 0.93f)
        positions.forEach { x ->
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(size.width * x, 0f),
                end   = androidx.compose.ui.geometry.Offset(size.width * x + 20f, size.height),
                strokeWidth = 2f
            )
        }
    }
}

// ── HOME SCREEN ──
@Composable
fun HomeScreen(onPlay: () -> Unit, onHowToPlay: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(width = 280.dp, height = 120.dp).clip(RoundedCornerShape(16.dp)).background(CardBg),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(8.dp)).background(AccentOrange).padding(horizontal = 28.dp, vertical = 14.dp)
            ) {
                Text("BLUE", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            }
        }
        Spacer(Modifier.height(32.dp))
        Text("Color Match", fontSize = 40.sp, fontWeight = FontWeight.Light, color = Color.White, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            "Exercise your response inhibition by\ncomparing one word's meaning to\nanother word's color",
            fontSize = 14.sp, color = TextMuted, textAlign = TextAlign.Center, lineHeight = 22.sp
        )
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onPlay,
            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("▶  PLAY", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onHowToPlay) {
            Text("❓  HOW TO PLAY", color = TextMuted, fontSize = 13.sp, letterSpacing = 1.sp)
        }
    }
}

// ── HOW TO PLAY SCREEN ──
@Composable
fun HowToPlayScreen(onBack: () -> Unit, onStart: () -> Unit) {
    val steps = listOf(
        Triple("1", "Identify the Rule", "You will see two word cards. The LEFT card shows a color word — its MEANING matters. The RIGHT card also shows a color word — its TEXT COLOR matters."),
        Triple("2", "Does it Match?", "Ask yourself: does the meaning of the left word match the ink color of the right word? Answer YES or NO as fast as you can!"),
        Triple("3", "Speed = More Points", "Every correct answer scores 100 × multiplier. Answer 4 in a row correctly to increase your multiplier (up to ×10). Mistakes reset the streak!")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().background(WoodDark).padding(top = 48.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)) {
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Text("← Back", color = TextLight, fontSize = 14.sp)
            }
            Text("Color Match", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.Center))
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("How to Play", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text("Follow these steps to master Color Match.", fontSize = 14.sp, color = TextMuted)
            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(WoodDark).padding(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Example", color = TextMuted, fontSize = 12.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(CardBg).padding(16.dp), contentAlignment = Alignment.Center) {
                                Text("red", color = Color(0xFF212121), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(6.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFBCAAA4)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                Text("meaning", color = Color(0xFF4E342E), fontSize = 11.sp)
                            }
                        }
                        Box(Modifier.size(28.dp).align(Alignment.CenterVertically), contentAlignment = Alignment.Center) {
                            Text("✓", color = Color(0xFF4CAF50), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(CardBg).padding(16.dp), contentAlignment = Alignment.Center) {
                                Text("blue", color = Color(0xFFE53935), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(6.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFBCAAA4)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                Text("text color", color = Color(0xFF4E342E), fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("\"red\" means RED → text color is RED ✓ Answer: YES", color = TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.height(20.dp))

            steps.forEachIndexed { i, (num, title, desc) ->
                val numColors = listOf(Color(0xFF5B8DB8), Color(0xFFE6A020), Color(0xFF5B8DB8))
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp).clip(RoundedCornerShape(14.dp)).background(WoodDark).padding(16.dp), verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(numColors[i]), contentAlignment = Alignment.Center) {
                        Text(num, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(desc, color = TextMuted, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(onClick = onStart, colors = ButtonDefaults.buttonColors(containerColor = TealBtn), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("START GAME", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── PLAYING GAME SCREEN ──
@Composable
fun GameplayScreen(
    timeLeft: Int,
    score: Int,
    streak: Int,
    multiplier: Int,
    round: RoundData,
    countdown: Int,
    phase: String,
    feedbackOk: Boolean,
    showPause: Boolean,
    onAnswer: (Boolean, suspend () -> Unit) -> Unit,
    onPauseClick: () -> Unit,
    onPauseContinue: () -> Unit,
    onPauseRestart: () -> Unit,
    onPauseExit: () -> Unit
) {
    val teal = Color(0xFF26C6DA)
    val multScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    val onAnimateMultiplier: suspend () -> Unit = {
        multScale.animateTo(1.5f, tween(120))
        multScale.animateTo(1f, tween(120))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 48.dp).align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopChip("TIME", "0:${timeLeft.toString().padStart(2, '0')}")
            TopChip("SCORE", score.toString())
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(4) { i ->
                    Box(modifier = Modifier.size(10.dp).padding(1.dp).clip(CircleShape).background(if (i < (streak % 4)) teal else Color(0xFF8D6E63)))
                }
                Spacer(Modifier.width(6.dp))
                Text("x$multiplier", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.scale(multScale.value))
            }
            IconButton(onClick = onPauseClick) {
                Text("⏸", fontSize = 20.sp, color = Color.White)
            }
        }

        // Center Area
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 120.dp, bottom = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (phase == "countdown") {
                AnimatedContent(targetState = countdown, transitionSpec = {
                    scaleIn(tween(200)) togetherWith scaleOut(tween(200))
                }, label = "countdown_anim") { cd ->
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(teal), contentAlignment = Alignment.Center) {
                        Text("$cd", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text("Does the meaning match the text color?", color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WordCard(Modifier.weight(1f), round.left, "meaning")
                    Box(Modifier.size(36.dp).align(Alignment.CenterVertically)) {
                        if (phase == "feedback") {
                            Text(if (feedbackOk) "✓" else "✗", color = if (feedbackOk) Color(0xFF4CAF50) else Color(0xFFE53935), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    WordCard(Modifier.weight(1f), round.right, "text color")
                }
            }
        }

        // Bottom Controls
        if (phase != "countdown") {
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 36.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { scope.launch { onAnswer(false, onAnimateMultiplier) } },
                    border = BorderStroke(1.dp, Color(0xFF8D6E63)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("NO ←", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
                Spacer(Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { scope.launch { onAnswer(true, onAnimateMultiplier) } },
                    border = BorderStroke(1.dp, Color(0xFF8D6E63)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("→ YES", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }
        }

        if (showPause) {
            PauseDialog(onContinue = onPauseContinue, onRestart = onPauseRestart, onExit = onPauseExit)
        }
    }
}

// ── GAME OVER SCREEN ──
@Composable
fun GameOverScreen(score: Int, best: Int, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Time's Up!", fontSize = 38.sp, color = Color.White, fontWeight = FontWeight.Light, letterSpacing = 1.sp)
        Spacer(Modifier.height(10.dp))
        Text("Game Over", fontSize = 16.sp, color = TextMuted)
        Spacer(Modifier.height(40.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ScoreCard("YOUR SCORE", "$score", Color.White, Modifier.weight(1f))
            ScoreCard("BEST", "$best", Color(0xFF26C6DA), Modifier.weight(1f))
        }
        Spacer(Modifier.height(48.dp))
        Button(onClick = onPlayAgain, colors = ButtonDefaults.buttonColors(containerColor = AccentOrange), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("▶  PLAY AGAIN", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(14.dp))
        TextButton(onClick = onHome) {
            Text("← Back to Home", color = TextMuted, fontSize = 14.sp)
        }
    }
}

// ── Subcomponents ──
@Composable
fun PauseDialog(onContinue: () -> Unit, onRestart: () -> Unit, onExit: () -> Unit) {
    Dialog(onDismissRequest = onContinue, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xCC0A0A0A)), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.width(280.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PAUSED", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                Spacer(Modifier.height(36.dp))
                Button(onClick = onContinue, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3A6B)), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("▶  CONTINUE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onRestart, border = BorderStroke(1.dp, Color(0xFF3A5A8A)), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("⟳  NEW GAME", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Spacer(Modifier.height(20.dp))
                TextButton(onClick = onExit) {
                    Text("EXIT", color = Color(0xFF8899AA), fontSize = 13.sp, letterSpacing = 2.sp)
                }
            }
        }
    }
}

@Composable
fun WordCard(modifier: Modifier = Modifier, colorWord: ColorWord, label: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.3f).clip(RoundedCornerShape(14.dp)).background(CardBg), contentAlignment = Alignment.Center) {
            Text(colorWord.word, color = colorWord.textColor, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(Color(0xFFBCAAA4)).padding(horizontal = 12.dp, vertical = 5.dp)) {
            Text(label, color = Color(0xFF4E342E), fontSize = 12.sp)
        }
    }
}

@Composable
fun TopChip(label: String, value: String) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0x55000000)).padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, color = Color(0xFFBCAAA4), fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ScoreCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(WoodDark).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(8.dp))
        Text(value, color = valueColor, fontSize = 38.sp, fontWeight = FontWeight.Bold)
    }
}