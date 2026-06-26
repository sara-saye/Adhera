package com.gpproject.adhera.treatment.games.ebbandflow

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.R
import kotlin.math.abs
import kotlin.math.roundToInt

// ─── Palette ──────────────────────────────────────────────────────────────────
private val LeafGreen  = Color(0xFF4CAF50)
private val LeafOrange = Color(0xFFFF9800)
private val BgTop      = Color(0xFF0D1B2A)
private val BgBot      = Color(0xFF162638)
private val HudBg      = Color(0xF00D1822)

@Composable
fun EbbAndFlowScreen(
    onBack: () -> Unit = {},
    vm: EbbAndFlowViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (!state.isPlaying && !state.isGameOver) vm.startNewGame()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        val blurMod = if (state.isPaused) Modifier.blur(20.dp) else Modifier
        Box(modifier = Modifier.fillMaxSize().then(blurMod)) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(BgTop, BgBot, BgTop)))
            )

            state.lastAnswerCorrect?.let { ok ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (ok) LeafGreen.copy(alpha = 0.14f)
                            else Color.Red.copy(alpha = 0.14f)
                        )
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

                GameHud(state = state, onPause = { vm.pauseGame() })

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clipToBounds()
                        .detectSwipeGesture { dir -> vm.onPlayerSwipe(dir) }
                ) {
                    val density   = LocalDensity.current
                    val canvasWPx = with(density) { maxWidth.toPx() }
                    val canvasHPx = with(density) { maxHeight.toPx() }

                    state.leaves.forEach { leaf ->
                        key(leaf.id) {
                            InfiniteLeaf(
                                leaf      = leaf,
                                cW        = canvasWPx,
                                cH        = canvasHPx,
                                showPulse = state.showPulseRings,
                            )
                        }
                    }
                }

                ModeBar(mode = state.gameMode)
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }

        if (state.isPaused) {
            PauseOverlay(
                onResume  = { vm.resumeGame() },
                onNewGame = { vm.startNewGame() },
                onExit    = onBack,
            )
        }

        if (state.isGameOver) {
            GameOverDialog(
                score     = state.score,
                onNewGame = { vm.startNewGame() },
                onBack    = onBack,
            )
        }
    }
}

@Composable
fun InfiniteLeaf(
    leaf: LeafState,
    cW: Float,
    cH: Float,
    showPulse: Boolean,
) {
    val density = LocalDensity.current

    val leafSizePx = (cW * 0.60f).coerceAtLeast(100f)
    val leafSizeDp = with(density) { leafSizePx.toDp() }

    val leafColor = when (leaf.color) {
        LeafColor.GREEN  -> LeafGreen
        LeafColor.ORANGE -> LeafOrange
    }

    val cycleDurationMs = 3200 + (leaf.id % 7) * 220
    val phaseOffset     = (leaf.id * 0.11f + leaf.xFraction * 0.4f) % 1f

    val inf = rememberInfiniteTransition(label = "t${leaf.id}")
    val raw by inf.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(cycleDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "raw${leaf.id}",
    )
    val t = (raw + phaseOffset) % 1f

    val pxX: Float
    val pxY: Float

    when (leaf.movingDirection) {
        LeafDirection.RIGHT -> {
            pxX = t * (cW + leafSizePx) - leafSizePx
            pxY = leaf.yFraction * cH - leafSizePx / 2f
        }
        LeafDirection.LEFT -> {
            pxX = (1f - t) * (cW + leafSizePx) - leafSizePx
            pxY = leaf.yFraction * cH - leafSizePx / 2f
        }
        LeafDirection.DOWN -> {
            pxX = leaf.xFraction * cW - leafSizePx / 2f
            pxY = t * (cH + leafSizePx) - leafSizePx
        }
        LeafDirection.UP -> {
            pxX = leaf.xFraction * cW - leafSizePx / 2f
            pxY = (1f - t) * (cH + leafSizePx) - leafSizePx
        }
    }

    val rippleInf = rememberInfiniteTransition(label = "ripple_${leaf.id}")

    val rippleScale by rippleInf.animateFloat(
        initialValue  = 0.3f,
        targetValue   = 2.2f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rscale_${leaf.id}",
    )
    val rippleAlpha by rippleInf.animateFloat(
        initialValue  = 0f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation  = keyframes {
                durationMillis = 900
                0f   at 0   using LinearEasing
                0.55f at 450 using LinearEasing
                0f   at 900 using LinearEasing
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "ralpha_${leaf.id}",
    )

    val showRipple = showPulse && !leaf.isDistractor

    Box(
        modifier = Modifier
            .offset { IntOffset(pxX.roundToInt(), pxY.roundToInt()) }
            .size(leafSizeDp)
            .alpha(leaf.alpha),
        contentAlignment = Alignment.Center,
    ) {
        if (showRipple) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(rippleScale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = rippleAlpha * 0.38f))
            )
        }

        Image(
            painter            = painterResource(id = R.drawable.ic_leaf_template),
            contentDescription = "Leaf ${leaf.facingDirection}",
            modifier           = Modifier
                .fillMaxSize(0.92f)
                .rotate(leaf.facingDirection.rotationDegrees()),
            contentScale       = ContentScale.Fit,
        )
    }
}

fun Modifier.detectSwipeGesture(
    minPx: Float = 35f,
    onSwipe: (LeafDirection) -> Unit,
): Modifier = this.pointerInput(onSwipe) {
    var start    = Offset.Zero
    var dragging = false
    awaitPointerEventScope {
        while (true) {
            val ev = awaitPointerEvent()
            val ch = ev.changes.firstOrNull() ?: continue
            when {
                ch.pressed && !dragging -> {
                    start    = ch.position
                    dragging = true
                    ch.consume()
                }
                !ch.pressed && dragging -> {
                    dragging = false
                    val dx = ch.position.x - start.x
                    val dy = ch.position.y - start.y
                    if (abs(dx) >= minPx || abs(dy) >= minPx) {
                        val dir = if (abs(dx) >= abs(dy)) {
                            if (dx > 0) LeafDirection.RIGHT else LeafDirection.LEFT
                        } else {
                            if (dy > 0) LeafDirection.DOWN else LeafDirection.UP
                        }
                        onSwipe(dir)
                    }
                    ch.consume()
                }
            }
        }
    }
}

@Composable
fun GameHud(state: EbbAndFlowUiState, onPause: () -> Unit) {
    val mins = (state.timeRemainingMs / 60_000L).toInt()
    val secs = ((state.timeRemainingMs % 60_000L) / 1000L).toInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HudBg)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPause, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color.White)
        }

        HudLabel("TIME",  "%d:%02d".format(mins, secs))
        HudLabel("SCORE", state.score.toString())

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (i < state.streakDots) LeafGreen
                            else Color.White.copy(alpha = 0.20f)
                        )
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFF1A3550))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text  = "x${state.multiplier}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                ),
            )
        }
    }
}

@Composable
private fun HudLabel(label: String, value: String) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color         = Color(0xFF7090B0),
                letterSpacing = 0.8.sp,
            ),
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color      = Color.White,
            ),
        )
    }
}

// ─── Mode Bar ─────────────────────────────────────────────────────────────────
@Composable
fun ModeBar(mode: LeafColor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
    ) {
        ModeCell(
            label  = "POINTING",
            active = mode == LeafColor.GREEN,
            color  = LeafGreen,
        )
        ModeCell(
            label  = "MOVING",
            active = mode == LeafColor.ORANGE,
            color  = LeafOrange,
        )
    }
}

@Composable
private fun RowScope.ModeCell(label: String, active: Boolean, color: Color) {
    Box(
        modifier         = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(if (active) color else Color(0xFF1E2F3F)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp,
                color         = if (active) Color.White else Color(0xFF4A6070),
            ),
        )
    }
}

// ─── Pause Overlay ────────────────────────────────────────────────────────────
@Composable
fun PauseOverlay(onResume: () -> Unit, onNewGame: () -> Unit, onExit: () -> Unit) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier            = Modifier.padding(horizontal = 44.dp),
        ) {
            Text(
                text  = "PAUSED",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight    = FontWeight.ExtraBold,
                    color         = Color.White,
                    letterSpacing = 4.sp,
                ),
            )
            Spacer(Modifier.height(6.dp))
            Button(
                onClick  = onResume,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E4976)),
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(
                    "CONTINUE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color         = Color.White,
                    ),
                )
            }
            Button(
                onClick  = onNewGame,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF132030)),
            ) {
                Icon(Icons.Default.AddCircle, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(
                    "NEW GAME",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color         = Color.White,
                    ),
                )
            }

            TextButton(
                onClick  = onExit,
                modifier = Modifier.fillMaxWidth().height(44.dp),
            ) {
                Text(
                    "EXIT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color         = Color.White.copy(alpha = 0.65f),
                    ),
                )
            }
        }
    }
}

// ─── Game Over Dialog ─────────────────────────────────────────────────────────
@Composable
fun GameOverDialog(score: Int, onNewGame: () -> Unit, onBack: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        containerColor   = Color(0xFF0F2436),
        title = {
            Text(
                "Game Over!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, color = Color.White
                ),
            )
        },
        text = {
            // تم التعديل هنا لحذف جملة البونص التوضيحية والإكتفاء بالسكور النهائي مباشرة
            Text(
                "Final Score: $score",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.85f)
                ),
            )
        },
        confirmButton = {
            Button(
                onClick = onNewGame,
                colors  = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                shape   = RoundedCornerShape(10.dp),
            ) { Text("Play Again", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onBack) {
                Text("Exit", color = Color.White.copy(alpha = 0.7f))
            }
        },
    )
}

@Composable
fun StaticLeafWithRing(
    xFrac: Float,
    yFrac: Float,
    cW: Float,
    cH: Float,
    leafColor: Color,
    dir: LeafDirection,
) {
    val leafSizePx = (cW * 0.30f).coerceAtLeast(100f)
    val leafSizeDp = with(LocalDensity.current) { leafSizePx.toDp() }

    val pxX = xFrac * cW - leafSizePx / 2f
    val pxY = yFrac * cH - leafSizePx / 2f

    Box(
        modifier         = Modifier
            .offset { IntOffset(pxX.roundToInt(), pxY.roundToInt()) }
            .size(leafSizeDp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(1.3f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.21f))
        )
        Image(
            painter            = painterResource(id = R.drawable.ic_leaf_template),
            contentDescription = null,
            modifier           = Modifier
                .fillMaxSize(0.92f)
                .rotate(dir.rotationDegrees()),
            contentScale       = ContentScale.Fit,
        )
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun EbbFlowPreview() {
    val dir   = LeafDirection.UP
    val color = LeafColor.GREEN

    val positions = listOf(
        0.15f to 0.12f,  0.72f to 0.08f,
        0.38f to 0.25f,  0.80f to 0.30f,
        0.10f to 0.45f,  0.55f to 0.50f,
        0.30f to 0.68f,  0.78f to 0.65f,
        0.48f to 0.82f,  0.18f to 0.80f,
    )

    val fakeLeaves = positions.mapIndexed { i, (x, y) ->
        LeafState(
            id              = i,
            facingDirection = dir,
            movingDirection = dir,
            xFraction       = x,
            yFraction       = y,
            color           = color,
            isDistractor    = false,
            alpha           = 1f,
        )
    }

    val fakeState = EbbAndFlowUiState(
        isPlaying        = true,
        gameMode         = color,
        leaves           = fakeLeaves,
        score            = 350,
        multiplier       = 2,
        streakDots       = 3,
        timeRemainingMs  = 45_000L,
        correctDirection = dir,
        showPulseRings   = true,
    )

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgTop, BgBot, BgTop)))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(Modifier.height(24.dp))
                GameHud(state = fakeState, onPause = {})
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clipToBounds()
                ) {
                    val density = LocalDensity.current
                    val cW = with(density) { maxWidth.toPx() }
                    val cH = with(density) { maxHeight.toPx() }
                    fakeLeaves.forEach { leaf ->
                        key(leaf.id) {
                            StaticLeafWithRing(
                                xFrac     = leaf.xFraction,
                                yFrac     = leaf.yFraction,
                                cW        = cW,
                                cH        = cH,
                                leafColor = if (leaf.color == LeafColor.GREEN) LeafGreen else LeafOrange,
                                dir       = leaf.facingDirection,
                            )
                        }
                    }
                }
                ModeBar(mode = fakeState.gameMode)
            }
        }
    }
}