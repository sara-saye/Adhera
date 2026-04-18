package com.gpproject.adhera.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.theme.AppBackground
import com.gpproject.adhera.ui.theme.TextPrimary
import com.gpproject.adhera.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdheraAnimatedSplash(onAnimationFinished: () -> Unit) {

    val appName = "Adhera"
    var animatedText by remember { mutableStateOf("") }
    val descriptionAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.8f) }

    // التحكم في تسلسل الأنيميشن
    LaunchedEffect(key1 = true) {
        // 1. تكبير اللوجو
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
        }

        // 2. Typewriter Effect لكلمة "Adhera"
        appName.forEach { char ->
            animatedText += char
            delay(150)        // ← تقدري تغيري السرعة هنا (أقل = أسرع)
        }

        // 3. Fade-in للوصف بعد ما الاسم يخلص
        delay(300)
        descriptionAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )

        // 4. انتظر شوية عشان اليوزر يشوف الأنيميشن كويس
        delay(1400)

        // 5. خلص الأنيميشن وروح للشاشة اللي بعدها
        onAnimationFinished()
    }

    // التصميم
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),        // غيرتها من Color.White إلى AppBackground عشان تكون متسقة مع باقي التطبيق
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // اسم التطبيق المتحرك
            Text(
                text = animatedText,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = logoScale.value,
                        scaleY = logoScale.value
                    )
                    .padding(bottom = 32.dp)
            )

            // الوصف
            Text(
                text = "Your journey to focus.",
                fontSize = 18.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp,
                modifier = Modifier
                    .alpha(descriptionAlpha.value)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}