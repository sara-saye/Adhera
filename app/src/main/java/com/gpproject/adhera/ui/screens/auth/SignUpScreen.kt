package com.gpproject.adhera.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(
    role: String,
    viewModel: AuthViewModel = AuthViewModel(),
    onSignUpComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Create Account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Registering as a $role", color = NavyPrimary)

        Spacer(modifier = Modifier.height(24.dp))

        when (role) {
            "Doctor" -> DoctorFields(viewModel)
            "Adult/Child" -> AdultFields(viewModel)
            "Parent" -> ParentFields(viewModel)
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Create My Account",
            onClick = { viewModel.performSignUp(role, onSignUpComplete) }
        )
    }
}

@Composable
fun DoctorFields(vm: AuthViewModel) {
    AdheraTextField(
        value = vm.name,
        onValueChange = { vm.name = it },
        label = "Full Name",
        icon = Icons.Default.Person
    )
    Spacer(modifier = Modifier.height(14.dp))
    AdheraTextField(
        value = vm.email,
        onValueChange = { vm.email = it },
        label = "Email",
        icon = Icons.Default.Email
    )
    Spacer(modifier = Modifier.height(14.dp))
    AdheraTextField(
        value = vm.password,
        onValueChange = { vm.password = it },
        label = "Password",
        icon = Icons.Default.Lock,
        isPassword = true
    )
}

@Composable
fun AdultFields(vm: AuthViewModel) {
    AdheraTextField(
        value = vm.nickname,
        onValueChange = { vm.nickname = it },
        label = "Nickname",
        icon = Icons.Default.Face
    )
    Spacer(modifier = Modifier.height(14.dp))
    AdheraTextField(
        value = vm.email,
        onValueChange = { vm.email = it },
        label = "Email",
        icon = Icons.Default.Email
    )
    Spacer(modifier = Modifier.height(14.dp))
    AdheraTextField(
        value = vm.password,
        onValueChange = { vm.password = it },
        label = "Password",
        icon = Icons.Default.Lock,
        isPassword = true
    )
    Spacer(modifier = Modifier.height(14.dp))
    AdheraTextField(
        value = vm.age,
        onValueChange = { vm.age = it },
        label = "Age",
        icon = Icons.Default.Cake
    )
    Spacer(modifier = Modifier.height(14.dp))
    GenderSelector(selected = vm.selectedGender, onSelect = { vm.selectedGender = it })
}

@Composable
fun ParentFields(vm: AuthViewModel) {
    Text("Parent's Info", fontWeight = FontWeight.Bold, color = NavyPrimary)
    AdheraTextField(
        value = vm.email,
        onValueChange = { vm.email = it },
        label = "Your Email",
        icon = Icons.Default.Email
    )
    Spacer(modifier = Modifier.height(12.dp))
    AdheraTextField(
        value = vm.password,
        onValueChange = { vm.password = it },
        label = "Your Password",
        icon = Icons.Default.Lock,
        isPassword = true
    )

    HorizontalDivider(Modifier.padding(vertical = 20.dp), color = DividerColor)

    Text("Child's Info", fontWeight = FontWeight.Bold, color = NavyPrimary)
    AdheraTextField(
        value = vm.nickname,
        onValueChange = { vm.nickname = it },
        label = "Child's Nickname",
        icon = Icons.Default.ChildCare
    )
    Spacer(modifier = Modifier.height(12.dp))
    AdheraTextField(
        value = vm.age,
        onValueChange = { vm.age = it },
        label = "Child's Age",
        icon = Icons.Default.Cake
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = vm.isChildUsingThisPhone,
            onCheckedChange = { vm.isChildUsingThisPhone = it })
        Text("Child will use this phone", style = MaterialTheme.typography.bodyMedium)
    }

    if (!vm.isChildUsingThisPhone) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = vm.shouldLinkPhones, onCheckedChange = { vm.shouldLinkPhones = it })
            Text("Link my phone with my child's", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ui/components/AdheraTextFields.kt
@Composable
fun AdheraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = NavyPrimary) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NavyPrimary,
            unfocusedBorderColor = DividerColor,
            focusedLabelColor = NavyPrimary,
            cursorColor = NavyPrimary
        )
    )
}

@Composable
fun GenderSelector(
    selected: String?,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            "I identify as",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GenderBtn("Male", "👨", selected == "Male", { onSelect("Male") }, Modifier.weight(1f))
            GenderBtn(
                "Female",
                "👩",
                selected == "Female",
                { onSelect("Female") },
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GenderBtn(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) NavyPrimary else CardBackground,
        border = BorderStroke(1.dp, if (isSelected) NavyPrimary else DividerColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = if (isSelected) Color.White else TextPrimary)
        }
    }
}