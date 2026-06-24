package com.gpproject.adhera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.credentials.CredentialManager
import com.gpproject.adhera.ui.navigation.AdheraNavGraph
// 👇 استيراد شاشة اللعبة الجديدة (تأكدي من صحة الباكيدج عندك)
import com.gpproject.adhera.ui.screens.treatment.games.colormatchgame.ColorMatchGameScreen
// 👇 استيراد شاشة الهابيت تراكر (تأكدي من اسم الكومبوزابل والباكيدج الصح ليها)
// import com.gpproject.adhera.ui.screens.treatment.habit_tracker.HabitTrackerScreen
import com.gpproject.adhera.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val credentialManager = CredentialManager.create(this)

        setContent {
            MaterialTheme {
                // 1️⃣ لتجربة لعبة Color Match:
                ColorMatchGameScreen(
                    onExitGame = {
                        // هنا لما تضغطي EXIT جوه اللعبة، ممكن تطبعي Log أو تقفلي الأكتيفتي للتيست
                        finish()
                    }
                )

                // 2️⃣ لتجربة الـ Habit Tracker (شيل الكومنت لما تحبي تستيه):
                // HabitTrackerScreen()

                // 3️⃣ الـ NavGraph الأصلي (معطل مؤقتاً للـ Test)
                // AdheraNavGraph()
            }
        }
    }
}