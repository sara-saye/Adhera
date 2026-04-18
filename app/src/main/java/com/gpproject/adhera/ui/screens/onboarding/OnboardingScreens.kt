package com.gpproject.adhera.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

@Composable
fun OnboardingScreen1(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        OnboardingHeader(onSkip)

        Column {
            Spacer(Modifier.height(16.dp))

            AnimatedEntrance(delayMillis = 100) {
                Text(
                    text = "Your Journey",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }

            AnimatedEntrance(delayMillis = 150) {
                Text(
                    text = "to Clarity",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NavyPrimary,
                    fontSize = 32.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            AnimatedEntrance(delayMillis = 250) {
                Text(
                    text = "Vitality offers a complete, guided path from clinical detection to daily management, helping you navigate ADHD with professional confidence and personal ease.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 18.sp,
                    lineHeight = 25.sp
                )
            }

            Spacer(Modifier.height(35.dp))

            AnimatedEntrance(delayMillis = 350) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(end = 16.dp)
                ) {
                    GridBackground()
                    JourneyPath()
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        AnimatedEntrance(delayMillis = 500) {
            PrimaryButton(
                text = "Continue",
                onClick = onContinue
            )
        }
    }
}

@Composable
fun OnboardingScreen3(
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingHeader(onSkip)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedEntrance(delayMillis = 100) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(NavyLight)
                    )

                    Image(
                        painter = painterResource(R.drawable.brain_image_adhera),
                        contentDescription = null,
                        modifier = Modifier
                            .size(140.dp)
                            .scale(scale)
                            .clip(CircleShape)
                    )

                    FloatingIcon(Icons.Default.Favorite, Modifier.offset((-90).dp, 40.dp))
                    FloatingIcon(Icons.Default.Check, Modifier.offset(90.dp, 20.dp))
                    FloatingIcon(Icons.Default.List, Modifier.offset(70.dp, (-70).dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedEntrance(delayMillis = 200) {
                Text(
                    text = "Daily Support for",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedEntrance(delayMillis = 250) {
                Text(
                    text = "Every Step",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NavyPrimary,

                )
            }

            Spacer(Modifier.height(16.dp))

            AnimatedEntrance(delayMillis = 300) {
                Text(
                    text = "Empower your focus with intuitive tools for to-dos, habit tracking, and cognitive focus games designed to simplify life after detection.",
                    textAlign = TextAlign.Center,
                    color = TextSecondary,
                    fontSize = 18.sp,
                    lineHeight = 25.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        AnimatedEntrance(delayMillis = 450) {
            PrimaryButton(
                text = "Get Started",
                onClick = onStart
            )
        }
    }
}

// ====================== Helper Components ======================

@Composable
fun FloatingIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF6D6E8)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NavyPrimary
        )
    }
}

@Composable
fun GridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 60f
        for (x in 0..(size.width / gridSize).toInt()) {
            drawLine(
                color = TextHint,
                start = Offset(x * gridSize, 0f),
                end = Offset(x * gridSize, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..(size.height / gridSize).toInt()) {
            drawLine(
                color = TextHint,
                start = Offset(0f, y * gridSize),
                end = Offset(size.width, y * gridSize),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun JourneyNode(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(if (isActive) 44.dp else 36.dp)
                .clip(CircleShape)
                .background(if (isActive) NavyPrimary else Color(0xFFEDE7F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isActive) Color.White else NavyPrimary)
        }
        Spacer(Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
fun JourneyPath() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = ""
    )

    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path()
            path.moveTo(40f, size.height * 0.4f)
            path.cubicTo(
                size.width * 0.3f, size.height * 0.1f,
                size.width * 0.6f, size.height * 0.9f,
                size.width * 0.9f, size.height * 0.5f
            )

            drawPath(
                path = path,
                color = Color(0xFFD6C8E6),
                style = Stroke(width = 5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f)))
            )

            val measure = android.graphics.PathMeasure(path.asAndroidPath(), false)
            val distance = measure.length * progress
            val pos = FloatArray(2)
            measure.getPosTan(distance, pos, null)

            drawCircle(color = NavyPrimary, radius = 16f, center = Offset(pos[0], pos[1]))
        }

        Icon(
            Icons.Default.DirectionsRun,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.offset(150.dp, 70.dp).size(18.dp)
        )

        JourneyNode(Icons.Default.Lightbulb, "Detection", modifier = Modifier.offset(0.dp, 60.dp))
        JourneyNode(Icons.Default.MedicalServices, "Diagnosis", modifier = Modifier.offset(90.dp, 20.dp))
        JourneyNode(Icons.Default.Schedule, "Routine", modifier = Modifier.offset(200.dp, 45.dp))
        JourneyNode(Icons.Default.Check, "Progress", modifier = Modifier.offset(260.dp, 70.dp))
    }
}

@Composable
fun OnboardingHeader(onSkip: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 24.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onSkip,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NavyLight.copy(alpha = 0.5f),
                contentColor = NavyPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = "Skip",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    var selectedRole by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        AnimatedEntrance(delayMillis = 100) {
            Surface(
                modifier = Modifier.size(80.dp),
                color = NavyLight,
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("🧠", fontSize = 38.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedEntrance(delayMillis = 150) {
            Text(
                text = "So, who are you?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }

        AnimatedEntrance(delayMillis = 200) {
            Text(
                text = "No worries, we're not judging! 😄\nJust help us personalize your experience.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedEntrance(delayMillis = 300) {
            RoleCard(
                emoji = "🩺",
                title = "Doctor",
                subtitle = "I'm a healthcare professional\nmonitoring my patients",
                selected = selectedRole == "Doctor",
                onClick = { selectedRole = "Doctor" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedEntrance(delayMillis = 350) {
            RoleCard(
                emoji = "🙋",
                title = "Adult / Child",
                subtitle = "I'm here for myself or\nfor my own assessment",
                selected = selectedRole == "Adult/Child",
                onClick = { selectedRole = "Adult/Child" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedEntrance(delayMillis = 400) {
            RoleCard(
                emoji = "👨‍👧",
                title = "Parent",
                subtitle = "I'm tracking my child's\nfocus and attention",
                selected = selectedRole == "Parent",
                onClick = { selectedRole = "Parent" }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedEntrance(delayMillis = 500) {
            PrimaryButton(
                text = "Let's Go →",
                onClick = { selectedRole?.let { onRoleSelected(it) } },
                enabled = selectedRole != null
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun RoleCard(
    emoji: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) NavyPrimary else CardBackground,
        border = BorderStroke(
            width = if (selected) 0.dp else 1.5.dp,
            color = if (selected) NavyPrimary else DividerColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (selected) Color.White.copy(alpha = 0.2f) else NavyLight
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(emoji, fontSize = 26.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (selected) Color.White else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = if (selected) Color.White.copy(alpha = 0.8f) else TextSecondary,
                    lineHeight = 18.sp
                )
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}