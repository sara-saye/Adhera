package com.gpproject.adhera.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.data.model.UserRole
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.AuthViewModel

@Composable
fun AdditionalInfoScreen(

    userRole: UserRole,

    onContinue: () -> Unit,

    viewModel: AuthViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.height(48.dp))

        AuthTopSection(
            emoji = "🧩",
            title = "Additional Information",
            subtitle = "Help us personalize your experience"
        )

        Spacer(modifier = Modifier.height(40.dp))

        when (userRole) {

            is UserRole.AdultChild -> {

                AuthTextField(
                    value = state.nickname,
                    onValueChange = viewModel::onNicknameChanged,
                    label = "Nickname"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = state.age,
                    onValueChange = viewModel::onAgeChanged,
                    label = "Age"
                )

                Spacer(modifier = Modifier.height(20.dp))

                GenderSelector(
                    selectedGender = state.selectedGender,
                    onGenderSelected = viewModel::onGenderSelected
                )
            }

            is UserRole.Parent -> {

                AuthTextField(
                    value = state.nickname,
                    onValueChange = viewModel::onNicknameChanged,
                    label = "Child Nickname"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = state.age,
                    onValueChange = viewModel::onAgeChanged,
                    label = "Child Age"
                )

                Spacer(modifier = Modifier.height(20.dp))

                GenderSelector(
                    selectedGender = state.selectedGender,
                    onGenderSelected = viewModel::onGenderSelected
                )
            }

            is UserRole.Doctor -> {

                Text(
                    text = "No additional information required.",
                    color = TextSecondary
                )
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = "Continue",
            onClick = {

                viewModel.saveAdditionalInfo(

                    role = userRole.value,

                    onSuccess = onContinue
                )
            }
        )
    }
}