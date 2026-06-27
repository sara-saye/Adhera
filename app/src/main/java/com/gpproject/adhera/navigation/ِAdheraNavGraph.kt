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
import com.gpproject.adhera.treatment.chatbot.ChatBotRepositoryImpl
import com.gpproject.adhera.treatment.chatbot.ChatBotScreen
import com.gpproject.adhera.treatment.chatbot.ChatBotViewModel
import com.gpproject.adhera.treatment.chatbot.ChatBotViewModelFactory
import com.gpproject.adhera.treatment.chatbot.chatbotdb.DatabaseProvider
import com.gpproject.adhera.treatment.games.colormatchgame.ColorMatchGameScreen
import com.gpproject.adhera.treatment.games.ebbandflow.EbbAndFlowHowToPlayScreen
import com.gpproject.adhera.treatment.games.ebbandflow.EbbAndFlowIntroScreen
import com.gpproject.adhera.treatment.games.ebbandflow.EbbAndFlowScreen
import com.gpproject.adhera.treatment.todo_list.data.TaskRepositoryImpl
import com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel
import com.gpproject.adhera.treatment.todo_list.screens.TaskViewModelFactory
import com.gpproject.adhera.treatment.todo_list.tododb.TodoDatabase
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
import com.gpproject.adhera.detection.screens.assessment.AssessmentIntroScreen
import com.gpproject.adhera.detection.screens.assessment.AssessmentScreen
import com.gpproject.adhera.detection.screens.assessment.AssessmentViewModel
import com.gpproject.adhera.detection.reports.DetectionCompleteScreen
import com.gpproject.adhera.detection.reports.DetectionResultsScreen
import com.gpproject.adhera.doctor.ui.SavePatientResultScreen
import com.gpproject.adhera.treatment.games.ebbandflow.FocusGamesMenuScreen

// =============================================================================
//  Routes
// =============================================================================
object Routes {
    // ── Splash / Onboarding ───────────────────────────────────────────────────
    const val SPLASH             = "splash"
    const val ONBOARDING_1       = "onboarding_1"
    const val ONBOARDING_3       = "onboarding_3"
    const val ROLE_SELECTION     = "role_selection"

    // ── Auth ──────────────────────────────────────────────────────────────────
    const val SIGN_UP            = "signup/{role}"
    const val LOGIN              = "login"
    const val FORGOT_PASSWORD    = "forgot_password"
    const val EMAIL_VERIFY       = "email_verify/{role}"
    const val ADDITIONAL_INFO    = "additional_info/{role}"
    const val ACCOUNT_CREATED    = "account_created/{role}"
    const val SAVE_PATIENT_RESULT="save_patient_result"
    // ── Home ──────────────────────────────────────────────────────────────────
    /** Adult/Child/Parent home — contains bottom nav (Home | Tools | Focus Games) */
    const val HOME               = "home"
    const val DOCTOR_HOME        = DoctorRoutes.GRAPH
    const val HABIT_TRACKER      = HabitTrackerRoutes.Graph

    // ── Treatment tools (launched from HomeHubScreen) ─────────────────────────
    const val TODO               = "todo"
    const val CHATBOT            = "chatbot"

    // ── Focus Games (launched from HomeHubScreen → Games tab) ─────────────────
    const val EBB_INTRO          = "ebb_and_flow_intro"
    const val EBB_HOW_TO         = "ebb_and_flow_how_to"
    const val EBB_GAME           = "ebb_and_flow_game"
    const val COLOR_MATCH        = "color_match"
    const val MEMORY_MATRIX      = "memory_matrix"
    const val FOCUS_GAMES_MENU = "focus_games_menu"


    // ── Detection flow ────────────────────────────────────────────────────────
    /**
     * Flow for Adult/Child/Parent (after login) AND for Doctor (on demand):
     *
     *  DETECTION_WELCOME
     *    → CAMERA_PERMISSION
     *        → MEDICAL_WELCOME
     *    → MEDICAL_WELCOME
     *        → MEDICAL_MRI → MEDICAL_EEG  (if user has scans)
     *        → skip to FOCUS_TEST_INTRO    (if user has no scans)
     *    → FOCUS_TEST_INTRO
     *    → FOCUS_TEST
     *    → ASSESSMENT_WELCOME
     *    → ASSESSMENT
     *    → DETECTION_DONE
     *    → REPORTS
     *    → HOME / DOCTOR_HOME
     */
    const val DETECTION_WELCOME  = "detection_welcome"
    const val CAMERA_PERMISSION  = "camera_permission"
    const val FOCUS_TEST_INTRO   = "focus_test_intro"
    const val FOCUS_TEST         = "focus_test"
    const val MEDICAL_WELCOME    = "medical_welcome"   // ScanQuestionScreen (new name for clarity)
    const val MEDICAL_EEG        = "medical_eeg"
    const val MEDICAL_MRI        = "medical_mri"
    const val ASSESSMENT_WELCOME = "assessment_welcome"
    const val ASSESSMENT         = "assessment"
    const val DETECTION_DONE     = "detection_done"
    const val REPORTS            = "reports"

    // ── Helpers ───────────────────────────────────────────────────────────────
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
    val context = LocalContext.current.applicationContext

    var doctorTestResult by remember {
        mutableStateOf("")
    }
    /**
     * Tracks whether the current detection session was started by a Doctor.
     * Used at the Reports screen to know where to navigate "Done".
     */
    var detectionStartedByDoctor by remember { mutableStateOf(false) }
    var doctorSingleTestMode by remember { mutableStateOf<String?>(null) }
    val doctorViewModel: DoctorViewModel = viewModel(factory = DoctorViewModel.factory(context))

    NavHost(
        navController    = navController,
        startDestination = Routes.SPLASH,
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
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role -> navController.navigate(Routes.signUp(role)) }
            )
        }

        // ── Sign Up ───────────────────────────────────────────────────────────
        composable(
            route     = Routes.SIGN_UP,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = backStack.arguments?.getString("role") ?: "AdultChild"
            SignupScreen(
                onSignupSuccess   = { navController.navigate(Routes.emailVerify(role)) },
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
                    if (role == UserRole.Doctor.value) {
                        // Doctor → goes straight to DoctorHome
                        navController.navigate(Routes.DOCTOR_HOME) {
                            popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                        }
                    } else {
                        // Adult/Child/Parent → goes straight to Detection flow
                        detectionStartedByDoctor = false
                        navController.navigate(Routes.DETECTION_WELCOME) {
                            popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                        }
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
        composable(
            route     = Routes.EMAIL_VERIFY,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = backStack.arguments?.getString("role") ?: "AdultChild"
            val authViewModel: com.gpproject.adhera.auth.AuthViewModel = viewModel()
            val authState by authViewModel.state.collectAsState()
            EmailVerificationScreen(
                email      = authState.email,
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
            val roleStr  = backStack.arguments?.getString("role") ?: "AdultChild"
            val userRole = when (roleStr) {
                "Doctor" -> UserRole.Doctor
                "Parent" -> UserRole.Parent
                else     -> UserRole.AdultChild
            }
            AdditionalInfoScreen(
                userRole   = userRole,
                onContinue = { navController.navigate(Routes.accountCreated(roleStr)) }
            )
        }

        // ── Account Created ───────────────────────────────────────────────────
        composable(
            route     = Routes.ACCOUNT_CREATED,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = backStack.arguments?.getString("role") ?: "AdultChild"
            AccountCreatedScreen(
                onContinue = {
                    if (role == "Doctor") {
                        // Doctor → DoctorHome
                        navController.navigate(Routes.DOCTOR_HOME) {
                            popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                        }
                    } else {
                        // Adult/Child/Parent → Detection flow (first time after signup)
                        detectionStartedByDoctor = false
                        navController.navigate(Routes.DETECTION_WELCOME) {
                            popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                        }
                    }
                }
            )
        }

        // =====================================================================
        //  HOME  (Adult / Child / Parent)
        //  Three-tab screen: Home | Management Tools | Focus Games
        // =====================================================================
        composable(Routes.HOME) {
            HomeHubScreen(
                // Management Tools tab callbacks
                onNavigateToTodo    = { navController.navigate(Routes.TODO) },
                onNavigateToHabits  = { navController.navigate(Routes.HABIT_TRACKER) },
                onNavigateToChatbot = { navController.navigate(Routes.CHATBOT) },
                // Focus Games tab callbacks (go directly to each game's intro/screen)
                onNavigateToEbbAndFlow    = { navController.navigate(Routes.EBB_INTRO) },
                onNavigateToMemoryMatrix  = { navController.navigate(Routes.MEMORY_MATRIX) },
                onNavigateToColorMatch    = { navController.navigate(Routes.COLOR_MATCH) },
                // Legacy param — not used in new design but kept for API compatibility
                onNavigateToFocusGames = {
                    navController.navigate(
                        Routes.FOCUS_GAMES_MENU
                    )
                },
            )
        }

        doctorNavGraph(
            navController = navController,
            doctorViewModel = doctorViewModel,

            onOpenFullDetection = {
                detectionStartedByDoctor = true
                doctorSingleTestMode = null
                navController.navigate(Routes.DETECTION_WELCOME)
            },

            onOpenEeg = {
                detectionStartedByDoctor = true
                doctorSingleTestMode = "EEG"
                navController.navigate(Routes.MEDICAL_EEG)
            },

            onOpenMri = {
                detectionStartedByDoctor = true
                doctorSingleTestMode = "MRI"
                navController.navigate(Routes.MEDICAL_MRI)
            },

            onOpenFocusTest = {
                detectionStartedByDoctor = true
                doctorSingleTestMode = "Focus"
                navController.navigate(Routes.CAMERA_PERMISSION)
            },

            onOpenAssessment = {
                detectionStartedByDoctor = true
                doctorSingleTestMode = "Assessment"
                navController.navigate(Routes.ASSESSMENT_WELCOME)
            }
        )

        // =====================================================================
        //  TREATMENT TOOLS
        // =====================================================================

        // ── To-Do ─────────────────────────────────────────────────────────────
        composable(Routes.TODO) {
            val taskDatabase   = remember { TodoDatabase.getDatabase(context) }
            val taskRepository = remember { TaskRepositoryImpl(taskDatabase.taskDao()) }
            val taskUseCases   = remember {
                TaskUseCases(
                    getAllTasks          = GetAllTasksUseCase(taskRepository),
                    getTaskById         = GetTaskByIdUseCase(taskRepository),
                    upsertTask          = UpsertTaskUseCase(taskRepository),
                    deleteTask          = DeleteTaskUseCase(taskRepository),
                    clearCompletedTasks = ClearCompletedTasksUseCase(taskRepository),
                )
            }
            val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(taskUseCases))
            TaskNavGraph(
                taskViewModel = taskViewModel,
                onBackToHome  = { navController.popBackStack() },
            )
        }

        // ── Chatbot ───────────────────────────────────────────────────────────
        composable(Routes.CHATBOT) {
            val factory = ChatBotViewModelFactory(
                repository = ChatBotRepositoryImpl(
                    dao = DatabaseProvider.getDatabase(context).chatDao()
                )
            )
            val chatViewModel: ChatBotViewModel = viewModel(factory = factory)
            ChatBotScreen(
                viewModel   = chatViewModel,
                onBackClick = { navController.popBackStack() },
            )
        }

        // ── Habit Tracker ─────────────────────────────────────────────────────
        habitTrackerGraph(navController)

        // =====================================================================
        //  FOCUS GAMES  (launched from HomeHubScreen → Games tab)
        // =====================================================================
// ── Focus Games Main Menu ───────────────────────────
        composable(Routes.FOCUS_GAMES_MENU) {

            FocusGamesMenuScreen(

                onBack = {
                    navController.popBackStack()
                },

                onSettingsClick = {
                    // حطي settings لو عندك شاشة ليها
                },

                onPlayEbbAndFlow = {
                    navController.navigate(
                        Routes.EBB_INTRO
                    )
                },

                onPlayMemoryMatrix = {
                    navController.navigate(
                        Routes.MEMORY_MATRIX
                    )
                },

                onPlayColorMatch = {
                    navController.navigate(
                        Routes.COLOR_MATCH
                    )
                }
            )
        }
        composable(Routes.EBB_INTRO) {
            EbbAndFlowIntroScreen(
                onBack      = { navController.popBackStack() },
                onNewGame   = { navController.navigate(Routes.EBB_GAME) },
                onHowToPlay = { navController.navigate(Routes.EBB_HOW_TO) },
            )
        }

        composable(Routes.EBB_HOW_TO) {
            EbbAndFlowHowToPlayScreen(
                onBack      = { navController.popBackStack() },
                onStartGame = { navController.navigate(Routes.EBB_GAME) },
            )
        }

        composable(Routes.EBB_GAME) {
            EbbAndFlowScreen(
                onBack = { navController.popBackStack(Routes.HOME, false) }
            )
        }

        composable(Routes.COLOR_MATCH) {
            ColorMatchGameScreen(
                onExitGame = { navController.popBackStack(Routes.HOME, false) }
            )
        }

        composable(Routes.MEMORY_MATRIX) {
            val memoryNavController = rememberNavController()
            val memoryViewModel: GameViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        GameViewModel(GameStorage(context)) as T
                }
            )
            MemoryMatrixNavGraph(
                navController = memoryNavController,
                viewModel     = memoryViewModel,
            )
        }

        // =====================================================================
        //  DETECTION FLOW
        //
        //  Triggered for:
        //    • Adult/Child/Parent  → right after login / signup (detectionStartedByDoctor=false)
        //    • Doctor              → on-demand from DoctorHome  (detectionStartedByDoctor=true)
        //
        //  Full flow:
        //    DETECTION_WELCOME
        //      → CAMERA_PERMISSION
        //      → MEDICAL_WELCOME (ScanQuestion)
        //          → (has scan)  MEDICAL_MRI → MEDICAL_EEG
        //          → (no scan)   FOCUS_TEST_INTRO
        //      → FOCUS_TEST_INTRO
        //      → FOCUS_TEST
        //      → ASSESSMENT_WELCOME
        //      → ASSESSMENT
        //      → DETECTION_DONE
        //      → REPORTS
        //      → HOME / DOCTOR_HOME
        // =====================================================================

        // ── Detection Welcome ─────────────────────────────────────────────────
        composable(Routes.DETECTION_WELCOME) {
            ADHDDetectionWelcomeScreen(
                onStartDetection = { navController.navigate(Routes.CAMERA_PERMISSION) }
            )
        }

        // ── Camera Permission ─────────────────────────────────────────────────
        composable(Routes.CAMERA_PERMISSION) {
            CameraPermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Routes.MEDICAL_WELCOME)
                },
                onSecondaryAction   = {
                    navController.navigate(Routes.MEDICAL_WELCOME) {
                        popUpTo(Routes.CAMERA_PERMISSION) { inclusive = true }
                    }
                },
            )
        }

        // ── Focus Test Intro ──────────────────────────────────────────────────
        composable(Routes.FOCUS_TEST_INTRO) {
            FocusTestIntroScreen(
                stageIndex = 1,
                onBack     = { navController.popBackStack() },
                onReady    = { navController.navigate(Routes.FOCUS_TEST) },
            )
        }

        // ── Focus Test ────────────────────────────────────────────────────────
        // ── Focus Test ────────────────────────────────────────────────────────
        composable(Routes.FOCUS_TEST) {
            SynapticFlowObservationScreen(
                stageIndex = 1,
                totalStages = 3,

                onBack = {
                    navController.popBackStack()
                },

                onNavigateToResults = {

                    if (doctorSingleTestMode != null) {

                        navController.navigate(
                            Routes.SAVE_PATIENT_RESULT
                        )

                    } else {

                        navController.navigate(
                            Routes.ASSESSMENT_WELCOME
                        ) {

                            popUpTo(
                                Routes.FOCUS_TEST_INTRO
                            ) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        // ── Medical Welcome (Scan Question) ───────────────────────────────────
        //    "Do you have an EEG/MRI scan?"
        composable(Routes.MEDICAL_WELCOME) {
            ScanQuestionScreen(
                onNextClick = { hasScan ->
                    if (hasScan) {
                        navController.navigate(Routes.MEDICAL_MRI)
                    } else {
                        navController.navigate(Routes.FOCUS_TEST_INTRO)
                    }
                },
                onSkipClick = {
                    navController.navigate(Routes.FOCUS_TEST_INTRO)
                },
            )
        }

        // ── Medical: EEG ──────────────────────────────────────────────────────
        composable(Routes.MEDICAL_EEG) {
            val vm: EegViewModel = viewModel(factory = EegViewModel.factory(context))
            EegScreen(
                onNavigateBack = { navController.popBackStack() },
                onFinished = {
                    if (doctorSingleTestMode != null) {
                        navController.navigate(Routes.SAVE_PATIENT_RESULT)
                    } else {
                        navController.navigate(Routes.FOCUS_TEST_INTRO)
                    }
                },
                viewModel      = vm,
            )
        }

        // ── Medical: MRI ──────────────────────────────────────────────────────
        composable(Routes.MEDICAL_MRI) {
            val vm: MriViewModel = viewModel(factory = MriViewModel.factory(context))
            MriScreen(
                onNavigateBack = { navController.popBackStack() },
                onFinished = {
                    if (doctorSingleTestMode != null) {
                        navController.navigate(Routes.SAVE_PATIENT_RESULT)
                    } else {
                        navController.navigate(Routes.MEDICAL_EEG)
                    }
                },
                viewModel      = vm,
            )
        }

        // ── Assessment Welcome ───────────────────────────────────────────────
        composable(Routes.ASSESSMENT_WELCOME) {
            AssessmentIntroScreen(
                stageIndex = 3,
                onBack = { navController.popBackStack() },
                onReady = { navController.navigate(Routes.ASSESSMENT) },
            )
        }

        // ── Assessment ────────────────────────────────────────────────────────
        composable(Routes.ASSESSMENT) {

            val vm: AssessmentViewModel =
                viewModel(
                    factory = AssessmentViewModel.factory(context)
                )

            AssessmentScreen(

                stageIndex = 3,
                totalStages = 3,

                onFinished = { result ->

                    if (doctorSingleTestMode != null) {

                        navController.navigate(
                            Routes.SAVE_PATIENT_RESULT
                        )

                    } else {

                        navController.navigate(
                            Routes.DETECTION_DONE
                        ) {

                            popUpTo(
                                Routes.ASSESSMENT
                            ) {
                                inclusive = true
                            }

                        }
                    }

                },

                onNavigateBack = {
                    navController.popBackStack()
                },

                viewModel = vm
            )
        }

        // ── Detection Done (Analysis transition screen) ───────────────────────
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
                onDone          = {
                    // After viewing report → go to the appropriate home
                    val destination = if (detectionStartedByDoctor) Routes.DOCTOR_HOME else Routes.HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.DETECTION_WELCOME) { inclusive = true }
                    }
                },
                doctorViewModel = if (detectionStartedByDoctor) doctorViewModel else null,
                onReturnHome    = {
                    navController.navigate(Routes.DOCTOR_HOME) {
                        popUpTo(Routes.DETECTION_WELCOME) { inclusive = true }
                    }
                },
                onNewTest       = {
                    navController.navigate(Routes.DETECTION_WELCOME) {
                        popUpTo(Routes.DETECTION_WELCOME) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.SAVE_PATIENT_RESULT){

            SavePatientResultScreen(
                doctorViewModel=doctorViewModel,
                testType=doctorSingleTestMode ?: "",
                testResult="Positive", // أو النتيجة الفعلية من MRI/EEG

                onDone={

                    navController.navigate(
                        Routes.DOCTOR_HOME
                    ){

                        popUpTo(
                            Routes.DOCTOR_HOME
                        ){
                            inclusive=false
                        }

                    }

                }

            )
        }
    }
}