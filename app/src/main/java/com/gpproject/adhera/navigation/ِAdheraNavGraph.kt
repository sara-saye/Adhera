package com.gpproject.adhera.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memorymatrix.game.GameStorage
import com.example.memorymatrix.navigation.NavGraph as MemoryMatrixNavGraph
import com.example.memorymatrix.viewmodel.GameViewModel

// ── Splash ────────────────────────────────────────────────────────────────────
import com.gpproject.adhera.ui.screens.splash.AdheraAnimatedSplash

// ── Onboarding ────────────────────────────────────────────────────────────────
import com.gpproject.adhera.ui.screens.onboarding.OnboardingScreen1
import com.gpproject.adhera.ui.screens.onboarding.OnboardingScreen3
import com.gpproject.adhera.ui.screens.onboarding.RoleSelectionScreen

// ── Auth ──────────────────────────────────────────────────────────────────────
import com.gpproject.adhera.auth.UserRole
import com.gpproject.adhera.auth.screens.LoginScreen
import com.gpproject.adhera.auth.screens.SignupScreen
import com.gpproject.adhera.auth.screens.ForgotPasswordScreen
import com.gpproject.adhera.auth.screens.EmailVerificationScreen
import com.gpproject.adhera.auth.screens.AdditionalInfoScreen
import com.gpproject.adhera.auth.screens.AccountCreatedScreen

// ── Home ──────────────────────────────────────────────────────────────────────
import com.gpproject.adhera.ui.screens.home.HomeHubScreen
import com.gpproject.adhera.navigation.TaskNavGraph
import com.gpproject.adhera.doctor.data.DoctorViewModel
import com.gpproject.adhera.doctor.ui.DoctorHomeScreen
import com.gpproject.adhera.treatment.chatbot.ChatBotRepositoryImpl
import com.gpproject.adhera.treatment.chatbot.ChatBotScreen
import com.gpproject.adhera.treatment.chatbot.ChatBotViewModel
import com.gpproject.adhera.treatment.chatbot.ChatBotViewModelFactory
import com.gpproject.adhera.treatment.chatbot.chatbotdb.DatabaseProvider
import com.gpproject.adhera.treatment.games.colormatchgame.ColorMatchGameScreen
import com.gpproject.adhera.treatment.games.ebbandflow.EbbAndFlowHowToPlayScreen
import com.gpproject.adhera.treatment.games.ebbandflow.EbbAndFlowIntroScreen
import com.gpproject.adhera.treatment.games.ebbandflow.EbbAndFlowScreen
import com.gpproject.adhera.treatment.games.ebbandflow.FocusGamesMenuScreen
import com.gpproject.adhera.treatment.todo_list.data.TaskRepositoryImpl
import com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel
import com.gpproject.adhera.treatment.todo_list.screens.TaskViewModelFactory
import com.gpproject.adhera.treatment.todo_list.tododb.AppDatabase
import com.gpproject.adhera.treatment.todo_list.usecase.ClearCompletedTasksUseCase
import com.gpproject.adhera.treatment.todo_list.usecase.DeleteTaskUseCase
import com.gpproject.adhera.treatment.todo_list.usecase.GetAllTasksUseCase
import com.gpproject.adhera.treatment.todo_list.usecase.GetTaskByIdUseCase
import com.gpproject.adhera.treatment.todo_list.usecase.TaskUseCases
import com.gpproject.adhera.treatment.todo_list.usecase.UpsertTaskUseCase
// ── Detection ─────────────────────────────────────────────────────────────────
import com.gpproject.adhera.detection.screens.ADHDDetectionWelcomeScreen
import com.gpproject.adhera.detection.screens.medical.ScanQuestionScreen
import com.gpproject.adhera.detection.screens.medical.EegScreen
import com.gpproject.adhera.detection.screens.medical.EegViewModel
import com.gpproject.adhera.detection.screens.medical.MriScreen
import com.gpproject.adhera.detection.screens.medical.MriViewModel
import com.gpproject.adhera.detection.screens.focustest.CameraPermissionScreen
import com.gpproject.adhera.detection.screens.focustest.FocusTestIntroScreen
import com.gpproject.adhera.detection.screens.focustest.SynapticFlowObservationScreen
import com.gpproject.adhera.detection.screens.assessment.AssessmentScreen
import com.gpproject.adhera.detection.screens.assessment.AssessmentViewModel
import com.gpproject.adhera.detection.reports.DetectionCompleteScreen
import com.gpproject.adhera.detection.reports.DetectionResultsScreen

// =============================================================================
//  Routes
// =============================================================================
object Routes {
    const val SPLASH             = "splash"
    const val ONBOARDING_1       = "onboarding_1"
    const val ONBOARDING_3       = "onboarding_3"
    const val ROLE_SELECTION     = "role_selection"
    const val SIGN_UP            = "signup/{role}"
    const val LOGIN              = "login"
    const val FORGOT_PASSWORD    = "forgot_password"
    const val EMAIL_VERIFY       = "email_verify/{role}"
    const val ADDITIONAL_INFO    = "additional_info/{role}"
    const val ACCOUNT_CREATED    = "account_created/{role}"
    const val HOME               = "home"
    const val DOCTOR_HOME        = "doctor_home"
    const val HABIT_TRACKER      = HabitTrackerRoutes.Graph
    const val DETECTION_WELCOME  = "detection_welcome"
    const val SCAN_QUESTION      = "scan_question"
    const val MEDICAL_EEG        = "medical_eeg"
    const val MEDICAL_MRI        = "medical_mri"
    const val CAMERA_PERMISSION  = "camera_permission"
    const val FOCUS_TEST_INTRO   = "focus_test_intro"
    const val FOCUS_TEST         = "focus_test"
    const val ASSESSMENT         = "assessment"
    const val DETECTION_DONE     = "detection_done"
    const val REPORTS            = "reports"
    const val TODO = "todo"
    const val CHATBOT = "chatbot"
    const val FOCUS_GAMES = "focus_games"
    const val EBB_INTRO = "ebb_and_flow_intro"
    const val EBB_HOW_TO = "ebb_and_flow_how_to"
    const val EBB_GAME = "ebb_and_flow_game"
    const val COLOR_MATCH = "color_match"
    const val MEMORY_MATRIX = "memory_matrix"

    // helpers
    fun signUp(role: String)         = "signup/$role"
    fun emailVerify(role: String)    = "email_verify/$role"
    fun additionalInfo(role: String) = "additional_info/$role"
    fun accountCreated(role: String) = "account_created/$role"
}

// =============================================================================
//  NavGraph
// =============================================================================
@Composable
fun AdheraNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    var detectionStartedByDoctor by remember { mutableStateOf(false) }
    val doctorViewModel: DoctorViewModel = viewModel(factory = DoctorViewModel.factory(context))

    NavHost(
        navController    = navController,
        startDestination = Routes.SPLASH
    ) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            AdheraAnimatedSplash(
                onAnimationFinished = {
                    navController.navigate(Routes.ONBOARDING_1) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding 1 ──────────────────────────────────────────────────────
        composable(Routes.ONBOARDING_1) {
            OnboardingScreen1(
                onContinue = { navController.navigate(Routes.ONBOARDING_3) },
                onSkip     = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(Routes.ONBOARDING_1) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding 3 ──────────────────────────────────────────────────────
        composable(Routes.ONBOARDING_3) {
            OnboardingScreen3(
                onStart = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(Routes.ONBOARDING_1) { inclusive = true }
                    }
                },
                onSkip  = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(Routes.ONBOARDING_1) { inclusive = true }
                    }
                }
            )
        }

        // ── Role Selection ────────────────────────────────────────────────────
        // الـ role بيتبعت كـ argument في الـ route عشان يوصل لـ SignUp ومنها لـ EmailVerify ومنها لـ AdditionalInfo
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    navController.navigate(Routes.signUp(role))
                }
            )
        }

        // ── Sign Up ───────────────────────────────────────────────────────────
        composable(
            route     = Routes.SIGN_UP,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = backStack.arguments?.getString("role") ?: "Adult/Child"
            SignupScreen(
                onSignupSuccess   = {
                    navController.navigate(Routes.emailVerify(role))
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = false }
                    }
                }
            )
        }

        // ── Login ─────────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess     = { role ->
                    val destination = if (role == UserRole.Doctor.value) Routes.DOCTOR_HOME else Routes.HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.popBackStack() },
                onForgotPassword   = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        // ── Forgot Password ───────────────────────────────────────────────────
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // ── Email Verification ────────────────────────────────────────────────
        // الـ email بييجي من الـ AuthViewModel مباشرة (state.email) مش من الـ route
        // عشان SignupScreen مش بتبعت email في الـ callback
        composable(
            route     = Routes.EMAIL_VERIFY,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = backStack.arguments?.getString("role") ?: "Adult/Child"

            // AuthViewModel مشترك بين SignupScreen و EmailVerificationScreen
            // لأنهم في نفس الـ NavBackStack فالـ ViewModel بيتشارك تلقائياً
            val authViewModel: com.gpproject.adhera.auth.AuthViewModel = viewModel()
            val authState by authViewModel.state.collectAsState()

            EmailVerificationScreen(
                email      = authState.email,   // بييجي من نفس الـ ViewModel اللي حفظ الـ email في SignupScreen
                onVerified = {
                    navController.navigate(Routes.additionalInfo(role)) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = false }
                    }
                },
                viewModel  = authViewModel
            )
        }

        // ── Additional Info ───────────────────────────────────────────────────
        composable(
            route     = Routes.ADDITIONAL_INFO,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val roleStr  = backStack.arguments?.getString("role") ?: "Adult/Child"
            val userRole = when (roleStr) {
                "Doctor"     -> UserRole.Doctor
                "Parent"     -> UserRole.Parent
                else         -> UserRole.AdultChild
            }
            AdditionalInfoScreen(
                userRole   = userRole,
                onContinue = { navController.navigate(Routes.accountCreated(roleStr)) }
            )
        }

        // ── Account Created ───────────────────────────────────────────────────
        composable(
            route = Routes.ACCOUNT_CREATED,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = backStack.arguments?.getString("role") ?: "Adult/Child"
            AccountCreatedScreen(
                onContinue = {
                    val destination = if (role == "Doctor") Routes.DOCTOR_HOME else Routes.HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeHubScreen(
                onNavigateToTodo = {
                    navController.navigate(Routes.TODO)
                },

                onNavigateToFocusGames = {
                    detectionStartedByDoctor = false
                    navController.navigate(Routes.DETECTION_WELCOME)
                },

                onNavigateToHabits = {
                    navController.navigate(Routes.HABIT_TRACKER)
                }
            )
        }

        composable(Routes.DOCTOR_HOME) {
            DoctorHomeScreen(
                doctorViewModel = doctorViewModel,
                onOpenFullDetection = {
                    detectionStartedByDoctor = true
                    navController.navigate(Routes.DETECTION_WELCOME)
                },
                onOpenEeg = {
                    detectionStartedByDoctor = true
                    navController.navigate(Routes.MEDICAL_EEG)
                },
                onOpenMri = {
                    detectionStartedByDoctor = true
                    navController.navigate(Routes.MEDICAL_MRI)
                },
                onOpenFocusTest = {
                    detectionStartedByDoctor = true
                    navController.navigate(Routes.CAMERA_PERMISSION)
                },
                onOpenAssessment = {
                    detectionStartedByDoctor = true
                    navController.navigate(Routes.ASSESSMENT)
                },
                onOpenTodo = { navController.navigate(Routes.TODO) },
                onOpenHabitTracker = { navController.navigate(Routes.HABIT_TRACKER) },
                onOpenChatbot = { navController.navigate(Routes.CHATBOT) },
                onOpenFocusGames = { navController.navigate(Routes.FOCUS_GAMES) }
            )
        }

        composable(Routes.TODO) {
            val taskDatabase = remember { AppDatabase.getDatabase(context) }
            val taskRepository = remember { TaskRepositoryImpl(taskDatabase.taskDao) }
            val taskUseCases = remember {
                TaskUseCases(
                    getAllTasks = GetAllTasksUseCase(taskRepository),
                    getTaskById = GetTaskByIdUseCase(taskRepository),
                    upsertTask = UpsertTaskUseCase(taskRepository),
                    deleteTask = DeleteTaskUseCase(taskRepository),
                    clearCompletedTasks = ClearCompletedTasksUseCase(taskRepository)
                )
            }
            val taskViewModel: TaskViewModel = viewModel(
                factory = TaskViewModelFactory(taskUseCases)
            )
            TaskNavGraph(
                taskViewModel = taskViewModel,
                onBackToHome = { navController.popBackStack() }
            )
        }

        composable(Routes.CHATBOT) {
            val factory = ChatBotViewModelFactory(
                repository = ChatBotRepositoryImpl(
                    dao = DatabaseProvider.getDatabase(context).chatDao()
                )
            )
            val chatViewModel: ChatBotViewModel = viewModel(factory = factory)
            ChatBotScreen(
                viewModel = chatViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.FOCUS_GAMES) {
            FocusGamesMenuScreen(
                onBack = { navController.popBackStack() },
                onPlayEbbAndFlow = { navController.navigate(Routes.EBB_INTRO) },
                onPlayMemoryMatrix = { navController.navigate(Routes.MEMORY_MATRIX) },
                onPlayColorMatch = { navController.navigate(Routes.COLOR_MATCH) }
            )
        }

        composable(Routes.EBB_INTRO) {
            EbbAndFlowIntroScreen(
                onBack = { navController.popBackStack() },
                onNewGame = { navController.navigate(Routes.EBB_GAME) },
                onHowToPlay = { navController.navigate(Routes.EBB_HOW_TO) }
            )
        }

        composable(Routes.EBB_HOW_TO) {
            EbbAndFlowHowToPlayScreen(
                onBack = { navController.popBackStack() },
                onStartGame = { navController.navigate(Routes.EBB_GAME) }
            )
        }

        composable(Routes.EBB_GAME) {
            EbbAndFlowScreen(onBack = { navController.popBackStack(Routes.FOCUS_GAMES, false) })
        }

        composable(Routes.COLOR_MATCH) {
            ColorMatchGameScreen(onExitGame = { navController.popBackStack(Routes.FOCUS_GAMES, false) })
        }

        composable(Routes.MEMORY_MATRIX) {
            val memoryNavController = rememberNavController()
            val memoryViewModel: GameViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return GameViewModel(GameStorage(context)) as T
                    }
                }
            )
            MemoryMatrixNavGraph(
                navController = memoryNavController,
                viewModel = memoryViewModel
            )
        }

        habitTrackerGraph(navController)
        // =====================================================================
        //  DETECTION FLOW
        // =====================================================================

        // ── ADHD Detection Welcome ────────────────────────────────────────────
        composable(Routes.DETECTION_WELCOME) {
            ADHDDetectionWelcomeScreen(
                onStartDetection = { navController.navigate(Routes.SCAN_QUESTION) }
            )
        }

        // ── Scan Question — "هل عندك MRI/EEG؟" ──────────────────────────────
        composable(Routes.SCAN_QUESTION) {
            ScanQuestionScreen(
                onNextClick = { hasScan ->
                    if (hasScan) {
                        navController.navigate(Routes.MEDICAL_EEG)
                    } else {
                        navController.navigate(Routes.CAMERA_PERMISSION)
                    }
                },
                onSkipClick = {
                    navController.navigate(Routes.CAMERA_PERMISSION)
                }
            )
        }

        // ── Medical: EEG ──────────────────────────────────────────────────────
        composable(Routes.MEDICAL_EEG) {
            val vm: EegViewModel = viewModel(factory = EegViewModel.factory(context))
            EegScreen(
                onNavigateBack = { navController.popBackStack() },
                onFinished     = { navController.navigate(Routes.MEDICAL_MRI) },
                viewModel      = vm
            )
        }

        // ── Medical: MRI ──────────────────────────────────────────────────────
        composable(Routes.MEDICAL_MRI) {
            val vm: MriViewModel = viewModel(factory = MriViewModel.factory(context))
            MriScreen(
                onNavigateBack = { navController.popBackStack() },
                onFinished     = { navController.navigate(Routes.CAMERA_PERMISSION) },
                viewModel      = vm
            )
        }

        // ── Camera Permission ─────────────────────────────────────────────────
        composable(Routes.CAMERA_PERMISSION) {
            CameraPermissionScreen(
                onPermissionGranted = { navController.navigate(Routes.FOCUS_TEST_INTRO) },
                onSecondaryAction   = {
                    // رفض الـ camera → skip الـ focus test وروح للـ assessment مباشرة
                    navController.navigate(Routes.ASSESSMENT) {
                        popUpTo(Routes.CAMERA_PERMISSION) { inclusive = true }
                    }
                }
            )
        }

        // ── Focus Test Intro ──────────────────────────────────────────────────
        composable(Routes.FOCUS_TEST_INTRO) {
            FocusTestIntroScreen(
                stageIndex = 3,
                onBack     = { navController.popBackStack() },
                onReady    = { navController.navigate(Routes.FOCUS_TEST) }
            )
        }

        // ── Focus Test ────────────────────────────────────────────────────────
        composable(Routes.FOCUS_TEST) {
            SynapticFlowObservationScreen(
                stageIndex          = 3,
                totalStages         = 3,
                onBack              = { navController.popBackStack() },
                onNavigateToResults = {
                    navController.navigate(Routes.ASSESSMENT) {
                        popUpTo(Routes.FOCUS_TEST_INTRO) { inclusive = true }
                    }
                }
            )
        }

        // ── Assessment ────────────────────────────────────────────────────────
        composable(Routes.ASSESSMENT) {
            val vm: AssessmentViewModel = viewModel(
                factory = AssessmentViewModel.factory(context)
            )
            AssessmentScreen(
                stageIndex     = 2,
                totalStages    = 3,
                onFinished     = { _ ->
                    navController.navigate(Routes.DETECTION_DONE) {
                        popUpTo(Routes.ASSESSMENT) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                viewModel      = vm
            )
        }

        // ── Detection Done (شاشة التحليل الانتقالية) ─────────────────────────
        composable(Routes.DETECTION_DONE) {
            DetectionCompleteScreen(
                onViewReport = {
                    navController.navigate(Routes.REPORTS) {
                        popUpTo(Routes.DETECTION_DONE) { inclusive = true }
                    }
                }
            )
        }

        // ── Reports ───────────────────────────────────────────────────────────
        composable(Routes.REPORTS) {
            DetectionResultsScreen(
                onDone = {
                    val destination = if (detectionStartedByDoctor) Routes.DOCTOR_HOME else Routes.HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.DETECTION_WELCOME) { inclusive = true }
                    }
                },
                doctorViewModel = if (detectionStartedByDoctor) doctorViewModel else null,
                onReturnHome = {
                    navController.navigate(Routes.DOCTOR_HOME) {
                        popUpTo(Routes.DETECTION_WELCOME) { inclusive = true }
                    }
                },
                onNewTest = {
                    navController.navigate(Routes.DETECTION_WELCOME) {
                        popUpTo(Routes.DETECTION_WELCOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
