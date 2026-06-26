package com.gpproject.adhera.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.gpproject.adhera.auth.UserRole
import com.gpproject.adhera.auth.screens.AccountCreatedScreen
import com.gpproject.adhera.auth.screens.AdditionalInfoScreen
import com.gpproject.adhera.auth.screens.EmailVerificationScreen
import com.gpproject.adhera.auth.screens.ForgotPasswordScreen
import com.gpproject.adhera.auth.screens.LoginScreen
import com.gpproject.adhera.auth.screens.SignupScreen

@Composable
fun AuthNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.LOGIN
    ) {

        // =========================
        // LOGIN
        // =========================
        composable(AuthRoutes.LOGIN) {

            LoginScreen(

                onLoginSuccess = {
                    navController.navigate(AuthRoutes.HOME)
                },

                onNavigateToSignup = {
                    navController.navigate(AuthRoutes.SIGNUP)
                },

                onForgotPassword = {
                    navController.navigate(AuthRoutes.FORGOT_PASSWORD)
                }
            )
        }

        // =========================
        // SIGNUP
        // =========================
        composable(AuthRoutes.SIGNUP) {

            SignupScreen(

                onSignupSuccess = {

                    // بعد ما نعمل create account
                    navController.navigate(AuthRoutes.EMAIL_VERIFICATION) {
                        popUpTo(AuthRoutes.SIGNUP) { inclusive = true }
                    }
                },

                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // =========================
        // FORGOT PASSWORD
        // =========================
        composable(AuthRoutes.FORGOT_PASSWORD) {

            ForgotPasswordScreen(

                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // =========================
        // EMAIL VERIFICATION
        // =========================
        composable(AuthRoutes.EMAIL_VERIFICATION) {

            EmailVerificationScreen(

                email = "",

                onVerified = {

                    navController.navigate(AuthRoutes.ADDITIONAL_INFO) {
                        popUpTo(AuthRoutes.EMAIL_VERIFICATION) { inclusive = true }
                    }
                }
            )
        }

        // =========================
        // ADDITIONAL INFO
        // =========================
        composable(AuthRoutes.ADDITIONAL_INFO) {

            AdditionalInfoScreen(

                userRole = UserRole.AdultChild, // مؤقت لحد ما نربطه من Signup state

                onContinue = {

                    navController.navigate(AuthRoutes.ACCOUNT_CREATED) {
                        popUpTo(AuthRoutes.ADDITIONAL_INFO) { inclusive = true }
                    }
                }
            )
        }

        // =========================
        // ACCOUNT CREATED (NEW)
        // =========================
        composable(AuthRoutes.ACCOUNT_CREATED) {

            AccountCreatedScreen(

                onContinue = {

                    navController.navigate(AuthRoutes.HOME) {
                        popUpTo(0) // يمسح الستاك كله
                    }
                }
            )
        }
    }
}