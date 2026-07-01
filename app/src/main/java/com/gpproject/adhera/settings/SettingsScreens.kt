package com.gpproject.adhera.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.detection.reports.DiagnosticReportHistoryItem
import com.gpproject.adhera.ui.components.adheraScreenPadding
import com.gpproject.adhera.ui.theme.AppBackground
import com.gpproject.adhera.ui.theme.CardBackground
import com.gpproject.adhera.ui.theme.DividerColor
import com.gpproject.adhera.ui.theme.ErrorColor
import com.gpproject.adhera.ui.theme.NavyLight
import com.gpproject.adhera.ui.theme.NavyPrimary
import com.gpproject.adhera.ui.theme.TextPrimary
import com.gpproject.adhera.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsDashboardScreen(
    onBack: () -> Unit,
    onAccountData: () -> Unit
) {
    var remindersEnabled by remember { mutableStateOf(true) }

    SettingsScaffold(title = "Adhera", onBack = onBack) {
        SettingsSectionTitle("ACCOUNT & DATA")
        SettingsNavCard(
            title = "Account & Data",
            subtitle = "Manage your profile, email, and diagnostic history",
            icon = Icons.Default.ManageAccounts,
            onClick = onAccountData
        )

        Spacer(Modifier.height(28.dp))
        SettingsSectionTitle("REMINDERS")
        SettingsToggleCard(
            title = "Smart Reminders",
            subtitle = "Enable task-based alarms and diagnostic alerts",
            checked = remindersEnabled,
            onCheckedChange = { remindersEnabled = it }
        )
    }
}

@Composable
fun AccountDataScreen(
    onBack: () -> Unit,
    onProfileManagement: () -> Unit,
    onDiagnosticArchive: () -> Unit,
    onRestartDiagnostic: () -> Unit
) {
    SettingsScaffold(title = "Adhera", onBack = onBack) {
        ScreenHero(
            title = "Account & Data",
            subtitle = "Manage your diagnostic journey and personal information with clarity."
        )

        LargeSettingsCard(
            title = "Profile Management",
            subtitle = "Update contact details, secure your account, and manage preferences.",
            icon = Icons.Default.PersonAdd,
            onClick = onProfileManagement
        )

        Spacer(Modifier.height(18.dp))
        LargeSettingsCard(
            title = "Diagnostic Archive",
            subtitle = "Review historical reports, previous assessments, and data trends.",
            icon = Icons.Default.FolderCopy,
            onClick = onDiagnosticArchive
        )

        Spacer(Modifier.height(22.dp))
        InfoPanel()

        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onRestartDiagnostic,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text("Restart Diagnostic Process", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProfileManagementScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: AccountSettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileManagementContent(
        state = state,
        onBack = onBack,
        onNicknameChange = viewModel::onNicknameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSaveChanges = viewModel::saveChanges,
        onConfirmDelete = { viewModel.deleteAccount(onDeleted) }
    )
}

@Composable
fun ProfileManagementContent(
    state: AccountSettingsUiState,
    onBack: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Box(Modifier.fillMaxSize().background(AppBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .adheraScreenPadding()
        ) {
            TopBar(title = "Adhera", onBack = onBack)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    // Hero
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Account Settings",
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Manage your personal information and security preferences.",
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, DividerColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            SettingsTextField(
                                label = "Nickname",
                                value = state.nickname,
                                onValueChange = onNicknameChange,
                                icon = Icons.Default.Person
                            )
                            SettingsTextField(
                                label = "Email Address",
                                value = state.email,
                                onValueChange = onEmailChange,
                                icon = Icons.Default.Email
                            )
                            SettingsTextField(
                                label = "Password",
                                value = state.password,
                                onValueChange = onPasswordChange,
                                icon = Icons.Default.Lock,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                }
                            )
                            Text(
                                "Leave blank to keep current password.",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            Button(
                                onClick = onSaveChanges,
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    DangerZone(onDelete = { confirmDelete = true })
                    Spacer(Modifier.height(32.dp))
                }

                item {
                    Text(
                        text = "© 2026 Adhera Platform. All rights reserved.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete account?", style = MaterialTheme.typography.titleMedium) },
            text = { Text("This permanently removes your account data and cannot be undone.", style = MaterialTheme.typography.bodySmall) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        onConfirmDelete()
                    }
                ) {
                    Text("Delete", color = ErrorColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DiagnosticArchiveScreen(
    dataStore: AdheraDataStore,
    onBack: () -> Unit,
    viewModel: DiagnosticArchiveViewModel = viewModel(
        factory = DiagnosticArchiveViewModelFactory(dataStore)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()



    LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index, state.canLoadMore) {
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@LaunchedEffect
        if (lastVisible >= state.reports.lastIndex && state.canLoadMore) {
            viewModel.loadMore()
        }
    }

    Box(Modifier.fillMaxSize().background(AppBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .adheraScreenPadding()
        ) {
            TopBar(title = "Adhera", onBack = onBack, showBackText = true)

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 30.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    ScreenHero(
                        title = "Diagnostic Archive",
                        subtitle = "Access and manage your medical diagnostic history."
                    )
                    Spacer(Modifier.height(22.dp))
                    SettingsSectionTitle("RECENT REPORTS")
                }

                if (state.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NavyPrimary)
                        }
                    }
                } else if (state.reports.isEmpty()) {
                    item {
                        Text(
                            text = "No reports are available yet.",
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            textAlign = TextAlign.Center,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    items(
                        count = state.reports.size,
                        key = { index -> state.reports[index].id }
                    ) { index ->
                        ReportArchiveCard(
                            report = state.reports[index],
                            expandedByDefault = index == 0,
                            onDelete = { viewModel.deleteReport(state.reports[index].id) },
                            onDownload = {

                            }
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoadingMore) {
                            CircularProgressIndicator(color = NavyPrimary)
                        } else if (state.canLoadMore) {
                            Text(
                                "LOADING OLDER RECORDS...",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

       
    }
}

@Composable
private fun SettingsScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
    ) {
        TopBar(title = title, onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp),
            content = content
        )
    }
}

@Composable
private fun TopBar(
    title: String,
    onBack: () -> Unit,
    showBackText: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onBack() }
                .padding(start = 16.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NavyPrimary)
            if (showBackText) {
                Spacer(Modifier.width(6.dp))
                Text("Back", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Text(
            text = title,
            color = NavyPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
    HorizontalDivider(color = DividerColor)
}

@Composable
private fun ScreenHero(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 16.sp
        )
    }
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.5.sp
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SettingsNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
            }
            Icon(icon, contentDescription = null, tint = Color.Transparent, modifier = Modifier.size(1.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SettingsToggleCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.scale(0.85f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NavyPrimary
                )
            )
        }
    }
}
@Composable
private fun LargeSettingsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            NavyLight,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun InfoPanel() {
    Surface(
        color = Color(0xFFE1F0FF),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text("New Assessment?", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Restarting the diagnostic process will begin a new evaluation. Previous results will be archived automatically.",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(label, color = TextSecondary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DividerColor,
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = AppBackground,
                unfocusedContainerColor = AppBackground
            )
        )
    }
}

@Composable
private fun DangerZone(onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFFFBFB),
        border = BorderStroke(1.dp, ErrorColor.copy(alpha = 0.35f))
    ) {
        Column(Modifier.padding(22.dp)) {
            Text("Danger Zone", color = ErrorColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                "Deleting your account will remove all data permanently.",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(22.dp))
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.CenterHorizontally).width(160.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ErrorColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorColor)
            ) {
                Text("Delete\nAccount", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ReportArchiveCard(
    report: DiagnosticReportHistoryItem,
    expandedByDefault: Boolean,
    onDelete: () -> Unit,
    onDownload: () -> Unit
) {
    var expanded by remember(report.id) { mutableStateOf(expandedByDefault) }
    val isFirstReport = report.id == DiagnosticArchiveViewModel.FIRST_REPORT_ID

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, if (expanded) NavyPrimary else DividerColor)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).background(if (expanded) NavyPrimary else AppBackground, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = if (expanded) Color.White else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        reportTitle(report.reportNumber),
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                    Text(
                        "${formatReportDate(report.timestamp)} • Reference #ADH-${9000 + report.reportNumber}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand report",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "ADHD probability: ${report.finalProbability}%",
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(6.dp))
                report.modelResults.forEach { result ->
                    Text(
                        "${result.title}: ${result.percentage}%",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Download PDF", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    OutlinedButton(
                        onClick = onDelete,
                        enabled = !isFirstReport,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, ErrorColor.copy(alpha = if (isFirstReport) 0.25f else 1f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete report", tint = ErrorColor, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

private fun reportTitle(number: Int): String = when (number) {
    1 -> "First Report"
    2 -> "Second Report"
    3 -> "Third Report"
    else -> "Report $number"
}

private fun formatReportDate(timestamp: Long): String {
    if (timestamp <= 0L) return "Current"
    return SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun ProfileManagementScreenPreview() {
    com.gpproject.adhera.ui.theme.AdheraTheme {
        ProfileManagementContent(
            state = AccountSettingsUiState(
                nickname = "John Doe",
                email = "john.doe@example.com",
                password = ""
            ),
            onBack = {},
            onNicknameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onSaveChanges = {},
            onConfirmDelete = {}
        )
    }
}