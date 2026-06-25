package com.gpproject.adhera.ui.screens.detection

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import kotlinx.coroutines.delay
import android.util.Log

import com.gpproject.adhera.data.usecase.CalculatePerformanceFeaturesUseCase
import com.gpproject.adhera.data.model.PerformanceFeatures
import com.gpproject.adhera.data.model.TrialResult


data class Trial(
    val arrays: List<Int>,
    val probe: Int,
    val correctAnswer: Boolean,
    val load: Int
)


val trials = listOf(

    Trial(
        arrays = listOf(
            R.drawable.array_01,
            R.drawable.array_02,
            R.drawable.array_03
        ),
        probe = R.drawable.probe_01,
        correctAnswer = true,
        load = 2
    ),

    Trial(
        arrays = listOf(
            R.drawable.array_04,
            R.drawable.array_05,
            R.drawable.array_06
        ),
        probe = R.drawable.probe_02,
        correctAnswer = false,
        load = 1
    ),

    Trial(
        arrays = listOf(
            R.drawable.array_07,
            R.drawable.array_08,
            R.drawable.array_09
        ),
        probe = R.drawable.probe_03,
        correctAnswer = true,
        load = 2
    ),

    Trial(
        arrays = listOf(
            R.drawable.array_10,
            R.drawable.array_11,
            R.drawable.array_12
        ),
        probe = R.drawable.probe_04,
        correctAnswer = true,
        load = 2
    ),

    Trial(
        arrays = listOf(
            R.drawable.array_13,
            R.drawable.array_14,
            R.drawable.array_15
        ),
        probe = R.drawable.probe_05,
        correctAnswer = true,
        load = 2
    ),

    Trial(
        arrays = listOf(
            R.drawable.array_16,
            R.drawable.array_17,
            R.drawable.array_18
        ),
        probe = R.drawable.probe_06,
        correctAnswer = false,
        load = 1
    ),

    Trial(
        arrays = listOf(
            R.drawable.array_19,
            R.drawable.array_20,
            R.drawable.array_21
        ),
        probe = R.drawable.probe_07,
        correctAnswer = false,
        load = 2
    ),

    Trial(
        arrays = listOf(
            R.drawable.array_22,
            R.drawable.array_23,
            R.drawable.array_24
        ),
        probe = R.drawable.probe_08,
        correctAnswer = true,
        load = 2
    )
)

enum class Phase {
    FIXATION_1,
    ARRAY_1,
    FIXATION_2,
    ARRAY_2,
    FIXATION_3,
    ARRAY_3,
    FIXATION_4,
    DISTRACTOR,
    FIXATION_5,
    PROBE,
    FEEDBACK,
    DONE
}
val calculateFeatures= CalculatePerformanceFeaturesUseCase()

@Composable
fun SynapticFlowObservationScreen(
    stageIndex: Int = 2,
    totalStages: Int = 3,
    onBack: () -> Unit,
    onFlowComplete: () -> Unit
) {


    var trialIndex by remember { mutableStateOf(0) }

    var phase by remember {
        mutableStateOf(Phase.FIXATION_1)
    }
    var userAnswer by remember {
        mutableStateOf<Boolean?>(null)
    }
    var probeStartTime by remember {
        mutableStateOf(0L)
    }

    var reactionTime by remember {
        mutableStateOf<Long?>(null)
    }
    val results = remember {
        mutableStateListOf<TrialResult>()
    }

    val trial = trials[trialIndex]

    LaunchedEffect(trialIndex) {

        phase = Phase.FIXATION_1

        delay(500)
        phase = Phase.ARRAY_1

        delay(750)
        phase = Phase.FIXATION_2

        delay(500)
        phase = Phase.ARRAY_2

        delay(750)
        phase = Phase.FIXATION_3

        delay(500)
        phase = Phase.ARRAY_3

        delay(750)
        phase = Phase.FIXATION_4

        delay(500)
        phase = Phase.DISTRACTOR

        delay(500)
        phase = Phase.FIXATION_5

        delay(500)
        phase = Phase.PROBE
        probeStartTime = System.currentTimeMillis()
    }
    LaunchedEffect(phase) {

        if (phase == Phase.PROBE) {

            delay(1500)

            if (phase == Phase.PROBE) {

                reactionTime = null
                userAnswer = null
                phase = Phase.FEEDBACK
                results.add(
                    TrialResult(
                        trialNumber = trialIndex + 1,
                        load = trial.load,
                        correctAnswer = trial.correctAnswer,
                        userAnswer = null,
                        isCorrect = null,
                        reactionTime = null
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(10.dp)
    ) {
        // Header
        HeaderWithBack(
            title = "Focus Test",
            onBack = onBack,
            progress = stageIndex.toFloat() / totalStages.toFloat(),
            stepText = "Stage $stageIndex of $totalStages"
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .offset(y = (-40).dp),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                when (phase) {

                    Phase.FIXATION_1,
                    Phase.FIXATION_2,
                    Phase.FIXATION_3,
                    Phase.FIXATION_4,
                    Phase.FIXATION_5 -> {

                        Text(
                            text = "+",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Phase.ARRAY_1 -> {

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .aspectRatio(1f),
                            shape = RoundedCornerShape(28.dp),
                            color = CardBackground
                        ) {
                            Image(
                                painter = painterResource(trial.arrays[0]),
                                contentDescription = null,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Phase.ARRAY_2 -> {

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .aspectRatio(1f),
                            shape = RoundedCornerShape(28.dp),
                            color = CardBackground
                        ) {
                            Image(
                                painter = painterResource(trial.arrays[1]),
                                contentDescription = null,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Phase.ARRAY_3 -> {

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .aspectRatio(1f),
                            shape = RoundedCornerShape(28.dp),
                            color = CardBackground
                        ) {
                            Image(
                                painter = painterResource(trial.arrays[2]),
                                contentDescription = null,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Phase.DISTRACTOR -> {
                        Box(
                            Modifier
                                .size(250.dp)
                                .background(Color.LightGray)
                        )
                    }
                    Phase.PROBE -> {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .aspectRatio(1f),
                                shape = RoundedCornerShape(28.dp),
                                color = CardBackground
                            ) {
                                Image(
                                    painter = painterResource(trial.probe),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                PrimaryButton(
                                    text = "Yes",
                                    onClick = {
                                        val answer = true

                                        reactionTime =
                                            System.currentTimeMillis() - probeStartTime

                                        results.add(
                                            TrialResult(
                                                trialNumber = trialIndex + 1,
                                                load = trial.load,
                                                correctAnswer = trial.correctAnswer,
                                                userAnswer = answer,
                                                isCorrect = (answer == trial.correctAnswer),
                                                reactionTime = reactionTime
                                            )
                                        )

                                        userAnswer = answer
                                        phase = Phase.FEEDBACK
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                SecondaryButton(
                                    text = "No",
                                    onClick = {
                                        val answer = false

                                        reactionTime =
                                            System.currentTimeMillis() - probeStartTime

                                        results.add(
                                            TrialResult(
                                                trialNumber = trialIndex + 1,
                                                load = trial.load,
                                                correctAnswer = trial.correctAnswer,
                                                userAnswer = answer,
                                                isCorrect = (answer == trial.correctAnswer),
                                                reactionTime = reactionTime
                                            )
                                        )

                                        userAnswer = answer
                                        phase = Phase.FEEDBACK
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    Phase.FEEDBACK -> {
                        val feedbackText = when(userAnswer) {
                            null -> "NO RESPONSE"
                            true -> if(trial.correctAnswer) "CORRECT ✔" else "WRONG ✖"
                            false -> if(!trial.correctAnswer) "CORRECT ✔" else "WRONG ✖"
                        }
                        LaunchedEffect(Unit) {
                            delay(1500)
                            if (trialIndex < trials.lastIndex) {

                                userAnswer = null
                                trialIndex++
                                phase = Phase.FIXATION_1

                            } else {
                                phase = Phase.DONE
                            }
                        }
                        Text(
                            text = feedbackText,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Phase.DONE -> {
                        LaunchedEffect(Unit) {
                            val features =calculateFeatures(results)
                            onFlowComplete()
                        }
                    }
                    else -> {}
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
fun SynapticFlowTestScreen(
    stageIndex: Int = 2,
    totalStages: Int = 3,
    testImage: Int = R.drawable.photo_3,
    onBack: () -> Unit,
    onAnswer: (Boolean) -> Unit
) {
    var isReady by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
    ) {
        // Header
        HeaderWithBack(
            title = "Focus Test",
            onBack = onBack,
            progress = stageIndex.toFloat() / totalStages.toFloat(),
            stepText = "Stage $stageIndex of $totalStages"
        )

        if (!isReady) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    AnimatedEntrance(delayMillis = 100) {
                        Text(
                            text = "Visual Recognition",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedEntrance(delayMillis = 150) {
                        Text(
                            text = "We'll show you an image. Tell us if you've seen it in the previous step.",
                            textAlign = TextAlign.Center,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedEntrance(delayMillis = 200) {
                        PrimaryButton(
                            text = "I'm Ready!",
                            onClick = { isReady = true }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                AnimatedEntrance(delayMillis = 100) {
                    Text(
                        text = "Have you seen this image before?",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        textAlign = TextAlign.Center,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f),
                    shape = RoundedCornerShape(28.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Image(
                        painter = painterResource(id = testImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Yes / No Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrimaryButton(
                        text = "Yes",
                        onClick = { onAnswer(true) },
                        modifier = Modifier.weight(1f)
                    )

                    SecondaryButton(
                        text = "No",
                        onClick = { onAnswer(false) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}