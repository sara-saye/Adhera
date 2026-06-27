package com.gpproject.adhera.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Color Palette (consistent with FocusGamesMenuScreen) ────────────────────
private object HomeColors {
    val Background     = Color(0xFFF0F4F8)
    val CardWhite      = Color(0xFFFFFFFF)
    val NavyDark       = Color(0xFF1B3A5C)
    val NavyMedium     = Color(0xFF1E4976)
    val AccentBlue     = Color(0xFF3A6EA5)
    val AccentTeal     = Color(0xFF2A7F7F)
    val AccentPurple   = Color(0xFF6B4FA0)
    val AccentAmber    = Color(0xFFB07800)
    val TextPrimary    = Color(0xFF1B3A5C)
    val TextSecondary  = Color(0xFF6B7A8D)
    val IconBg         = Color(0xFFDDE8F5)
    val TagBg          = Color(0xFFEDF2FB)
    val NavBarBg       = Color(0xFFFFFFFF)
    val NavSelected    = Color(0xFF1B3A5C)
    val NavUnselected  = Color(0xFF9AAABB)
    val DividerColor   = Color(0xFFE8EEF4)
    val GreenAccent    = Color(0xFF2E7D32)
    val OrangeAccent   = Color(0xFFE65100)
}

// ─── Bottom Nav Items ─────────────────────────────────────────────────────────
private enum class HomeTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        "Home",
        Icons.Filled.Home,
        Icons.Outlined.Home,
    ),
    TOOLS(
        "Tools",
        Icons.Filled.Build,
        Icons.Outlined.Build,
    ),
    GAMES(
        "Focus Games",
        Icons.Filled.SportsEsports,
        Icons.Outlined.SportsEsports,
    ),
}

// ─── Data Models ──────────────────────────────────────────────────────────────
private data class FeatureCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val actionLabel: String,
    val accentColor: Color,
    val onClick: () -> Unit,
)

private data class ToolCard(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val onClick: () -> Unit,
)

private data class GameCard(
    val title: String,
    val tag: String,
    val description: String,
    val icon: ImageVector,
    val tagColor: Color,
    val onClick: () -> Unit,
)

// ─── Main Screen ──────────────────────────────────────────────────────────────
@Composable
fun HomeHubScreen(
    onNavigateToTodo: () -> Unit,
    onNavigateToFocusGames: () -> Unit,  // kept for legacy; not used in new flow
    onNavigateToHabits: () -> Unit,
    onNavigateToChatbot: () -> Unit = {},
    onNavigateToEbbAndFlow: () -> Unit = {},
    onNavigateToMemoryMatrix: () -> Unit = {},
    onNavigateToColorMatch: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }

    Scaffold(
        containerColor = HomeColors.Background,
        bottomBar = {
            HomeBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AnimatedVisibility(
                visible = selectedTab == HomeTab.HOME,
                enter = fadeIn(tween(220)),
                exit = fadeOut(tween(150)),
            ) {
                HomeTab(
                    onNavigateToTodo = onNavigateToTodo,
                    onNavigateToHabits = onNavigateToHabits,
                    onNavigateToChatbot = onNavigateToChatbot,
                    onNavigateToGames = { selectedTab = HomeTab.GAMES },
                )
            }
            AnimatedVisibility(
                visible = selectedTab == HomeTab.TOOLS,
                enter = fadeIn(tween(220)),
                exit = fadeOut(tween(150)),
            ) {
                ManagementToolsTab(
                    onNavigateToTodo = onNavigateToTodo,
                    onNavigateToHabits = onNavigateToHabits,
                    onNavigateToChatbot = onNavigateToChatbot,
                )
            }
            AnimatedVisibility(
                visible = selectedTab == HomeTab.GAMES,
                enter = fadeIn(tween(220)),
                exit = fadeOut(tween(150)),
            ) {
                FocusGamesTab(
                    onPlayEbbAndFlow = onNavigateToEbbAndFlow,
                    onPlayMemoryMatrix = onNavigateToMemoryMatrix,
                    onPlayColorMatch = onNavigateToColorMatch,
                )
            }
        }
    }
}

// ─── Bottom Nav Bar ───────────────────────────────────────────────────────────
@Composable
private fun HomeBottomNavBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = HomeColors.NavBarBg,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                NavBarItem(
                    tab = tab,
                    isSelected = isSelected,
                    onClick = { onTabSelected(tab) },
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    tab: HomeTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 48.dp else 0.dp,
        animationSpec = tween(250),
        label = "indicator",
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
            contentDescription = tab.label,
            tint = if (isSelected) HomeColors.NavSelected else HomeColors.NavUnselected,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) HomeColors.NavSelected else HomeColors.NavUnselected,
                fontSize = 10.sp,
            ),
        )
        Spacer(Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(indicatorWidth)
                .height(3.dp)
                .clip(CircleShape)
                .background(HomeColors.NavSelected),
        )
    }
}

// ─── Tab 1: Home ──────────────────────────────────────────────────────────────
@Composable
private fun HomeTab(
    onNavigateToTodo: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToGames: () -> Unit,
) {
    val features = listOf(
        FeatureCard(
            title = "Focus Games",
            subtitle = "Sharpen cognitive agility",
            icon = Icons.Filled.SportsEsports,
            actionLabel = "Play",
            accentColor = HomeColors.AccentBlue,
            onClick = onNavigateToGames,
        ),
        FeatureCard(
            title = "To-Do List",
            subtitle = "Manage priority tasks",
            icon = Icons.AutoMirrored.Filled.PlaylistAddCheck,
            actionLabel = "View",
            accentColor = HomeColors.GreenAccent,
            onClick = onNavigateToTodo,
        ),
        FeatureCard(
            title = "Habit Tracker",
            subtitle = "Maintain daily rhythms",
            icon = Icons.Filled.CalendarMonth,
            actionLabel = "Check In",
            accentColor = HomeColors.AccentTeal,
            onClick = onNavigateToHabits,
        ),
        FeatureCard(
            title = "AI Chatbot",
            subtitle = "Talk through your day",
            icon = Icons.Filled.Chat,
            actionLabel = "Chat",
            accentColor = HomeColors.AccentPurple,
            onClick = onNavigateToChatbot,
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
    ) {
        item { HomeHeader() }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            Text(
                text = "Your Tools",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                ),
            )
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            // 2-column grid, manual since we're inside LazyColumn
            val rows = features.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        row.forEach { feature ->
                            HomeFeatureCardItem(
                                card = feature,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        // fill empty slot if row has only 1 item
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
        item { QuickTipBanner() }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun HomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Adhera",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = HomeColors.NavyDark,
                    letterSpacing = (-0.5).sp,
                ),
            )
            IconButton(onClick = {}) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = HomeColors.NavyDark)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Good morning 👋",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = HomeColors.TextPrimary,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Ready for a calm, structured day?",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = HomeColors.TextSecondary,
            ),
        )
    }
}

@Composable
private fun HomeFeatureCardItem(
    card: FeatureCard,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(168.dp)
            .clickable { card.onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(card.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = null,
                    tint = card.accentColor,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = HomeColors.TextPrimary,
                    ),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = card.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = HomeColors.TextSecondary,
                        fontSize = 11.sp,
                    ),
                    maxLines = 2,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = card.actionLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = card.accentColor,
                    ),
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = card.accentColor,
                    modifier = Modifier.size(9.dp),
                )
            }
        }
    }
}

@Composable
private fun QuickTipBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HomeColors.NavyDark,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Daily Tip",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 1.sp,
                    ),
                )
                Text(
                    text = "Break tasks into 10-minute blocks for better focus.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                    ),
                )
            }
        }
    }
}

// ─── Tab 2: Management Tools ──────────────────────────────────────────────────
@Composable
private fun ManagementToolsTab(
    onNavigateToTodo: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToChatbot: () -> Unit,
) {
    val tools = listOf(
        ToolCard(
            title = "Habit Tracker",
            description = "Build and maintain your daily routines. Track streaks and stay consistent.",
            icon = Icons.Filled.CalendarMonth,
            accentColor = HomeColors.AccentTeal,
            onClick = onNavigateToHabits,
        ),
        ToolCard(
            title = "To-Do List",
            description = "Capture and prioritize tasks. Break down big goals into manageable steps.",
            icon = Icons.AutoMirrored.Filled.PlaylistAddCheck,
            accentColor = HomeColors.GreenAccent,
            onClick = onNavigateToTodo,
        ),
        ToolCard(
            title = "AI Chatbot",
            description = "Your personal ADHD support companion. Ask questions and get guidance anytime.",
            icon = Icons.Filled.Chat,
            accentColor = HomeColors.AccentPurple,
            onClick = onNavigateToChatbot,
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
    ) {
        item {
            Text(
                text = "Management Tools",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Everything you need to stay organized and on track.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HomeColors.TextSecondary),
            )
            Spacer(Modifier.height(24.dp))
        }
        items(tools) { tool ->
            ToolCardItem(tool = tool)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ToolCardItem(tool: ToolCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { tool.onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tool.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.accentColor,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HomeColors.TextPrimary,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = HomeColors.TextSecondary,
                        lineHeight = 17.sp,
                    ),
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = HomeColors.NavUnselected,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

// ─── Tab 3: Focus Games ───────────────────────────────────────────────────────
@Composable
private fun FocusGamesTab(
    onPlayEbbAndFlow: () -> Unit,
    onPlayMemoryMatrix: () -> Unit,
    onPlayColorMatch: () -> Unit,
) {
    val games = listOf(
        GameCard(
            title = "Ebb & Flow",
            tag = "COGNITIVE SHIFT",
            description = "Train mental agility by navigating shifting patterns and rhythms.",
            icon = Icons.Filled.Waves,
            tagColor = HomeColors.AccentBlue,
            onClick = onPlayEbbAndFlow,
        ),
        GameCard(
            title = "Memory Matrix",
            tag = "RECALL",
            description = "Strengthen working memory through spatial recognition and sequence tracking.",
            icon = Icons.Filled.GridOn,
            tagColor = HomeColors.TextSecondary,
            onClick = onPlayMemoryMatrix,
        ),
        GameCard(
            title = "Color Match",
            tag = "REACTION",
            description = "Enhance inhibitory control and processing speed with rapid visual filtering.",
            icon = Icons.Filled.Palette,
            tagColor = HomeColors.OrangeAccent,
            onClick = onPlayColorMatch,
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
    ) {
        item {
            Text(
                text = "Focus Games",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Sharpen your cognitive clarity with structured play.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HomeColors.TextSecondary),
            )
            Spacer(Modifier.height(24.dp))
        }
        items(games) { game ->
            GameCardItem(game = game)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun GameCardItem(game: GameCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { game.onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(game.tagColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = null,
                        tint = game.tagColor,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = HomeColors.TagBg,
                ) {
                    Text(
                        text = game.tag,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = game.tagColor,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = game.description,
                style = MaterialTheme.typography.bodySmall.copy(color = HomeColors.TextSecondary),
            )
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = game.onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomeColors.NavyDark),
            ) {
                Text(
                    text = "Play  ▶",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    ),
                )
            }
        }
    }
}