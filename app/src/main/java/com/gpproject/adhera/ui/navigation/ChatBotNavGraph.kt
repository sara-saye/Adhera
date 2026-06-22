package com.gpproject.adhera.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gpproject.adhera.data.local.chatbot.DatabaseProvider
import com.gpproject.adhera.data.repository.ChatBotRepositoryImpl
import com.gpproject.adhera.ui.chatbot.ChatBotScreen
import com.gpproject.adhera.viewmodels.ChatBotViewModel
import com.gpproject.adhera.viewmodels.ChatBotViewModelFactory

// ─── Route ───────────────────────────────────────────────────────────────────

object ChatBotRoute {
    const val ROUTE = "chatbot"
}

// ─── NavController Extensions ────────────────────────────────────────────────

/**
 * روح لشاشة الـ ChatBot
 * الاستخدام: navController.navigateToChatBot()
 */
fun NavHostController.navigateToChatBot() {
    navigate(ChatBotRoute.ROUTE)
}

/**
 * روح لشاشة الـ ChatBot وامسح كل الـ back stack اللي قبله
 * مفيد لو هتفتح الـ ChatBot من Splash أو Onboarding مثلاً
 * الاستخدام: navController.navigateToChatBotClearStack()
 */
fun NavHostController.navigateToChatBotClearStack() {
    navigate(ChatBotRoute.ROUTE) {
        popUpTo(0) { inclusive = true }
    }
}

// ─── NavGraph Extension ───────────────────────────────────────────────────────

/**
 * سجّل الـ ChatBot في أي NavGraph ببساطة
 *
 * الاستخدام داخل NavHost:
 *
 *   NavHost(navController, startDestination = "home") {
 *       chatBotScreen(
 *           onBackClick = { navController.popBackStack() }
 *       )
 *   }
 *
 * @param onBackClick  اللي هيتنفذ لما المستخدم يضغط Back —
 *                     بتحدده إنت من بره على حسب مكان الـ ChatBot في الـ app
 */
fun NavGraphBuilder.chatBotScreen(
    onBackClick: () -> Unit
) {
    composable(route = ChatBotRoute.ROUTE) {
        ChatBotEntry(onBackClick = onBackClick)
    }
}

// ─── Internal Entry Point ─────────────────────────────────────────────────────

/**
 * هنا بيتعمل كل الـ wiring للـ ViewModel من غير Hilt
 * مش محتاج تعدّل فيه
 */
@Composable
private fun ChatBotEntry(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // بناء الـ Repository بشكل تلقائي من الـ DatabaseProvider
    val factory = ChatBotViewModelFactory(
        repository = ChatBotRepositoryImpl(
            dao = DatabaseProvider
                .getDatabase(context)
                .chatDao()
        )
    )

    val viewModel: ChatBotViewModel = viewModel(factory = factory)

    ChatBotScreen(
        viewModel = viewModel,
        onBackClick = onBackClick
    )
}