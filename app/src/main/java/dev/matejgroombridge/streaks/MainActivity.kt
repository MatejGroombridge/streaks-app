package dev.matejgroombridge.streaks

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.matejgroombridge.streaks.blocker.AdultDomains
import dev.matejgroombridge.streaks.blocker.BlockerVpnService
import dev.matejgroombridge.streaks.data.AppSettings
import dev.matejgroombridge.streaks.data.BlockerState
import dev.matejgroombridge.streaks.data.StreakState
import dev.matejgroombridge.streaks.data.ThemeMode
import dev.matejgroombridge.streaks.data.WeekStart
import dev.matejgroombridge.streaks.ui.SettingsViewModel
import dev.matejgroombridge.streaks.ui.StreakViewModel
import dev.matejgroombridge.streaks.ui.theme.AppTheme
import dev.matejgroombridge.streaks.ui.theme.StreakOrangeDeep
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = LocalContext.current.applicationContext as Application
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(app))
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            AppTheme(themeMode = settings.themeMode, amoled = settings.amoled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    StreaksApp(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}

private enum class DetailScreen { Blocker, Settings }

@Composable
private fun StreaksApp(settingsViewModel: SettingsViewModel) {
    val app = LocalContext.current.applicationContext as Application
    val streakViewModel: StreakViewModel = viewModel(factory = StreakViewModel.factory(app))
    val streakState by streakViewModel.state.collectAsStateWithLifecycle()
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    var detailScreen by remember { mutableStateOf<DetailScreen?>(null) }

    when (detailScreen) {
        DetailScreen.Blocker -> BlockerScreen(
            blocker = streakState.blocker,
            viewModel = streakViewModel,
            padding = PaddingValues(),
            onBack = { detailScreen = null },
        )
        DetailScreen.Settings -> SettingsScreen(
            settings = settings,
            viewModel = settingsViewModel,
            padding = PaddingValues(),
            onBack = { detailScreen = null },
        )
        null -> HomeScreen(
            state = streakState,
            viewModel = streakViewModel,
            padding = PaddingValues(),
            onBlockerClick = { detailScreen = DetailScreen.Blocker },
            onSettingsClick = { detailScreen = DetailScreen.Settings },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PastWeekScreen(
    state: StreakState,
    weekStart: WeekStart,
    viewModel: StreakViewModel,
    contentPadding: PaddingValues,
) {
    val today = LocalDate.now().toEpochDay()
    val days = remember(today) { previousSevenDays(today) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Past Week") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                PastWeekRow(
                    failedDays = state.failureEpochDays,
                    days = days,
                    today = today,
                    onSetFailed = viewModel::setFailure,
                )
            }
        }
    }
}

@Composable
private fun PastWeekRow(
    failedDays: Set<Long>,
    days: List<Long>,
    today: Long,
    onSetFailed: (Long, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        days.forEach { epochDay ->
            val failed = epochDay in failedDays
            PastWeekDayChip(
                date = LocalDate.ofEpochDay(epochDay),
                failed = failed,
                isToday = epochDay == today,
                onClick = { onSetFailed(epochDay, !failed) },
                onPick = { markFailed -> onSetFailed(epochDay, markFailed) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PastWeekDayChip(
    date: LocalDate,
    failed: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    onPick: (Boolean) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
        )
        Box {
            DayCellShape(
                failed = failed,
                dayOfMonth = date.dayOfMonth,
                modifier = Modifier
                    .size(34.dp)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = { menuOpen = true },
                    ),
            )
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Mark Clean") },
                    leadingIcon = { Icon(Icons.Outlined.Check, contentDescription = null) },
                    onClick = { menuOpen = false; onPick(false) },
                )
                DropdownMenuItem(
                    text = { Text("Mark Failure") },
                    leadingIcon = { Icon(Icons.Outlined.Close, contentDescription = null) },
                    onClick = { menuOpen = false; onPick(true) },
                )
            }
        }
    }
}

@Composable
private fun DayCellShape(
    failed: Boolean,
    dayOfMonth: Int,
    modifier: Modifier = Modifier,
) {
    if (failed) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(StreakOrangeDeep),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Failure",
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.06f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = dayOfMonth.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeScreen(
    state: StreakState,
    viewModel: StreakViewModel,
    padding: PaddingValues,
    onBlockerClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var showResetConfirm by remember { mutableStateOf(false) }
    var showPastWeekManually by remember { mutableStateOf(false) }
    val today = LocalDate.now().toEpochDay()
    val pastWeekDays = remember(today) { previousSevenDays(today) }
    val hasRecentFailure = remember(state.failureEpochDays, pastWeekDays) {
        pastWeekDays.any { it in state.failureEpochDays }
    }
    val showPastWeek = hasRecentFailure || showPastWeekManually
    val todayDate = remember(today) { LocalDate.ofEpochDay(today) }
    val endOfCurrentWeek = remember(todayDate) {
        val daysAfterMon = ((todayDate.dayOfWeek.value - DayOfWeek.MONDAY.value) + 7) % 7
        val mondayOfThisWeek = todayDate.minusDays(daysAfterMon.toLong())
        mondayOfThisWeek.plusDays(6)
    }
    val streakDays = state.currentStreakDays(today)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            HomeHeader(
                onOpenBlocker = onBlockerClick,
                onOpenSettings = onSettingsClick,
            )
        }
        item {
            CurrentStreakCard(
                days = streakDays,
                onResetClick = { showResetConfirm = true },
            )
        }
        if (showPastWeek) {
            item {
                PastWeekCard(
                    failedDays = state.failureEpochDays,
                    days = pastWeekDays,
                    today = today,
                    editable = true,
                    collapsible = !hasRecentFailure,
                    onSetFailed = viewModel::setFailure,
                    onToggleWeeklyView = { showPastWeekManually = false },
                )
            }
        }
        item {
            AllTimeCard(
                state = state,
                today = today,
                endOfCurrentWeek = endOfCurrentWeek,
                onShowWeeklyView = if (showPastWeek) null else { { showPastWeekManually = true } },
            )
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            icon = { Icon(Icons.Outlined.WarningAmber, contentDescription = null) },
            title = { Text("Reset your streak?") },
            text = { Text("This marks today as a failure. No shame, no spiral — just an honest reset and the next right action.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordFailureToday()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreakOrangeDeep,
                        contentColor = Color.White,
                    ),
                ) { Text("I slipped today") }
            },
            dismissButton = { TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun HomeHeader(
    onOpenBlocker: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onOpenBlocker) {
                Icon(Icons.Outlined.Block, contentDescription = "Blocker")
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
        }
        Text(
            text = "Streaks",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 14.dp),
        )
    }
}

@Composable
private fun HomeCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun CurrentStreakCard(days: Long, onResetClick: () -> Unit) {
    HomeCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(38.dp),
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = days.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (days == 1L) "day" else "days",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onResetClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = StreakOrangeDeep,
                contentColor = Color.White,
            ),
        ) {
            Icon(Icons.Outlined.WarningAmber, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Reset streak")
        }
    }
}

@Composable
private fun PastWeekCard(
    failedDays: Set<Long>,
    days: List<Long>,
    today: Long,
    editable: Boolean,
    collapsible: Boolean,
    onSetFailed: (Long, Boolean) -> Unit,
    onToggleWeeklyView: () -> Unit,
) {
    HomeCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Past week", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (collapsible) {
                IconButton(onClick = onToggleWeeklyView) {
                    Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Hide weekly view")
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            days.forEach { epochDay ->
                val date = LocalDate.ofEpochDay(epochDay)
                val failed = epochDay in failedDays
                InlineWeekDay(
                    date = date,
                    failed = failed,
                    isToday = epochDay == today,
                    editable = editable,
                    onClick = { onSetFailed(epochDay, !failed) },
                )
            }
        }
    }
}

@Composable
private fun InlineWeekDay(
    date: LocalDate,
    failed: Boolean,
    isToday: Boolean,
    editable: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
        )
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (failed) StreakOrangeDeep else MaterialTheme.colorScheme.surfaceVariant)
                .then(if (editable) Modifier.clickable(onClick = onClick) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (failed) {
                Icon(Icons.Outlined.Close, contentDescription = "Reset day", tint = Color.White, modifier = Modifier.size(17.dp))
            } else {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllTimeScreen(
    state: StreakState,
    contentPadding: PaddingValues,
) {
    val today = LocalDate.now().toEpochDay()
    val todayDate = remember(today) { LocalDate.ofEpochDay(today) }
    val endOfCurrentWeek = remember(todayDate) {
        val daysAfterMon = ((todayDate.dayOfWeek.value - DayOfWeek.MONDAY.value) + 7) % 7
        val mondayOfThisWeek = todayDate.minusDays(daysAfterMon.toLong())
        mondayOfThisWeek.plusDays(6)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("All Time") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                AllTimeCard(
                    state = state,
                    today = today,
                    endOfCurrentWeek = endOfCurrentWeek,
                )
            }
        }
    }
}

@Composable
private fun AllTimeCard(
    state: StreakState,
    today: Long,
    endOfCurrentWeek: LocalDate,
    onShowWeeklyView: (() -> Unit)? = null,
) {
    val cleanDays = remember(state.startEpochDay, state.failureEpochDays, today) {
        (state.startEpochDay..today).count { it !in state.failureEpochDays }
    }
    val streak = remember(state.failureEpochDays, today) { state.currentStreakDays(today) }
    val topStreak = remember(state.startEpochDay, state.failureEpochDays, today) {
        longestCleanStreak(state.startEpochDay, today, state.failureEpochDays)
    }
    val resets = remember(state.startEpochDay, state.failureEpochDays, today) {
        state.failureEpochDays.count { it in state.startEpochDay..today }
    }

    HomeCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Stats", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (onShowWeeklyView != null) {
                IconButton(onClick = onShowWeeklyView) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = "Show weekly view")
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatBlock("Clean days", cleanDays.toString(), Modifier.weight(1f))
            StatBlock("Best streak", topStreak.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatBlock("Current", streak.toString(), Modifier.weight(1f))
            StatBlock("Resets", resets.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        ContributionGrid(
            createdAtEpochDay = state.startEpochDay,
            failedDays = state.failureEpochDays,
            today = today,
            endOfCurrentWeek = endOfCurrentWeek,
            accent = StreakOrangeDeep,
            emptyTint = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun StatBlock(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProtectionStatusCard(
    blocker: BlockerState,
    onManageClick: () -> Unit,
) {
    val hasRules = blocker.blockAllPornSites || blocker.customSites.isNotEmpty()
    val active = blocker.blockerEnabled && hasRules
    HomeCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Block, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Protection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = when {
                        active -> "Blocker active"
                        hasRules -> "Rules ready, blocker off"
                        else -> "No blocker rules enabled"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onManageClick) { Text("Manage") }
        }
    }
}

private val CELL_SIZE = 12.dp
private val CELL_GAP = 3.dp
private val GRID_SIDE_PADDING = 0.dp

@Composable
private fun ContributionGrid(
    createdAtEpochDay: Long,
    failedDays: Set<Long>,
    today: Long,
    endOfCurrentWeek: LocalDate,
    accent: Color,
    emptyTint: Color,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columnsThatFit = run {
            val available = maxWidth - (GRID_SIDE_PADDING * 2)
            val columnPitch = CELL_SIZE + CELL_GAP
            ((available + CELL_GAP) / columnPitch).toInt().coerceAtLeast(1)
        }
        val weeksAvailable = remember(createdAtEpochDay, endOfCurrentWeek, columnsThatFit) {
            val createdDate = LocalDate.ofEpochDay(createdAtEpochDay)
            val daysBetween = ChronoUnit.DAYS.between(createdDate, endOfCurrentWeek).toInt()
            val historyWeeks = (daysBetween + 6) / 7 + 1
            maxOf(columnsThatFit, historyWeeks)
        }
        val scrollState = rememberScrollState()
        LaunchedEffect(weeksAvailable) { scrollState.scrollTo(scrollState.maxValue) }
        val needsScroll = weeksAvailable > columnsThatFit
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (needsScroll) Modifier.horizontalScroll(scrollState) else Modifier)
                .padding(horizontal = GRID_SIDE_PADDING),
            horizontalArrangement = Arrangement.End,
        ) {
            for (weeksBack in (weeksAvailable - 1) downTo 0) {
                val weekMonday = endOfCurrentWeek.minusDays(6).minusWeeks(weeksBack.toLong())
                Column(
                    verticalArrangement = Arrangement.spacedBy(CELL_GAP),
                    modifier = Modifier.padding(end = if (weeksBack == 0) 0.dp else CELL_GAP),
                ) {
                    for (offset in 0..6) {
                        val cellDate = weekMonday.plusDays(offset.toLong())
                        val cellEpoch = cellDate.toEpochDay()
                        val inFuture = cellEpoch > today
                        val beforeStart = cellEpoch < createdAtEpochDay
                        val failed = cellEpoch in failedDays
                        GridCell(
                            failed = failed,
                            inactive = inFuture || beforeStart,
                            accent = accent,
                            emptyTint = emptyTint,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    failed: Boolean,
    inactive: Boolean,
    accent: Color,
    emptyTint: Color,
) {
    Box(
        modifier = Modifier
            .size(CELL_SIZE)
            .clip(RoundedCornerShape(3.dp))
            .background(
                when {
                    failed -> accent
                    inactive -> emptyTint.copy(alpha = 0.35f)
                    else -> emptyTint
                },
            ),
    )
}

private fun longestCleanStreak(start: Long, today: Long, failedDays: Set<Long>): Long {
    var best = 0L
    var current = 0L
    for (day in start..today) {
        if (day in failedDays) current = 0 else {
            current += 1
            if (current > best) best = current
        }
    }
    return best
}

@Composable
private fun SobrietyCard(streakDays: Long) {
    SettingsCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = StreakOrangeDeep)
            Column {
                Text("One clean decision at a time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "You have protected ${streakDays.coerceAtLeast(0)} day${if (streakDays == 1L) "" else "s"} of attention, energy, and self-respect.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun previousSevenDays(today: Long): List<Long> = (6 downTo 0).map { today - it }

@Composable
private fun BlockerScreen(
    blocker: BlockerState,
    viewModel: StreakViewModel,
    padding: PaddingValues,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var newSite by remember { mutableStateOf("") }
    var challengeFact by remember { mutableStateOf(PornFacts.random()) }
    var showChallenge by remember { mutableStateOf(false) }
    var vpnDenied by remember { mutableStateOf(false) }
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ContextCompat.startForegroundService(context, Intent(context, BlockerVpnService::class.java))
            viewModel.setBlockerEnabled(true)
            vpnDenied = false
        } else {
            vpnDenied = true
            viewModel.setBlockerEnabled(false)
        }
    }

    LaunchedEffect(blocker.blockerEnabled) {
        if (blocker.blockerEnabled && VpnService.prepare(context) == null) {
            ContextCompat.startForegroundService(context, Intent(context, BlockerVpnService::class.java))
        }
    }

    fun requestEnableBlocker() {
        val prepareIntent = VpnService.prepare(context)
        if (prepareIntent != null) {
            vpnPermissionLauncher.launch(prepareIntent)
        } else {
            ContextCompat.startForegroundService(context, Intent(context, BlockerVpnService::class.java))
            viewModel.setBlockerEnabled(true)
            vpnDenied = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .statusBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            PageHeader("Blocker", "Build friction before the craving becomes automatic.")
        }
        item {
            BlockerStatusCard(blocker = blocker, vpnDenied = vpnDenied)
        }
        item {
            SettingsCard {
                SwitchRow(
                    title = "Block all porn sites",
                    subtitle = "Main switch for the curated adult-site block list.",
                    checked = blocker.blockAllPornSites,
                    onCheckedChange = viewModel::setBlockAllPornSites,
                )
            }
        }
        item {
            SettingsCard {
                SwitchRow(
                    title = "Blocker enabled",
                    subtitle = if (blocker.blockerEnabled) "Protection is on through a local DNS-filtering VPN." else "Turning off requires a writing challenge.",
                    checked = blocker.blockerEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) requestEnableBlocker() else {
                            challengeFact = PornFacts.random()
                            showChallenge = true
                        }
                    },
                )
            }
        }
        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Individual sites", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = newSite,
                        onValueChange = { newSite = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("example.com") },
                        leadingIcon = { Icon(Icons.Outlined.Public, contentDescription = null) },
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            viewModel.addSite(newSite)
                            newSite = ""
                        },
                        enabled = newSite.isNotBlank(),
                    ) { Text("Add site") }
                }
            }
        }
        if (blocker.customSites.isEmpty()) {
            item { Text("No custom sites yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(blocker.customSites) { site ->
                SettingsCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(site, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { viewModel.removeSite(site) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Remove $site")
                        }
                    }
                }
            }
        }
        if (vpnDenied) {
            item {
                Text(
                    "VPN permission was not granted, so blocking could not be enabled.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        item {
            Text(
                "Blocking uses Android's local VPN API to inspect DNS lookups on-device. Matching adult/custom domains return NXDOMAIN; allowed DNS requests are forwarded upstream.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (showChallenge) {
        UnblockChallengeDialog(
            fact = challengeFact,
            onCancel = { showChallenge = false },
            onComplete = {
                context.startService(BlockerVpnService.stopIntent(context))
                viewModel.setBlockerEnabled(false)
                showChallenge = false
            },
        )
    }
}

@Composable
private fun BlockerStatusCard(blocker: BlockerState, vpnDenied: Boolean) {
    val builtInRules = if (blocker.blockAllPornSites) AdultDomains.suffixes.size else 0
    val customRules = blocker.customSites.size
    val totalRules = builtInRules + customRules
    val statusColor = when {
        vpnDenied -> MaterialTheme.colorScheme.error
        blocker.blockerEnabled -> StreakOrangeDeep
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val statusText = when {
        vpnDenied -> "Permission needed"
        blocker.blockerEnabled -> "Active"
        else -> "Paused"
    }

    SettingsCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(statusColor),
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("DNS protection: $statusText", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "$totalRules blocked domain rule${if (totalRules == 1) "" else "s"} loaded • ${customRules} custom",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            AssistChip(
                onClick = {},
                label = { Text(if (blocker.blockAllPornSites) "Adult list on" else "Adult list off") },
                leadingIcon = { Icon(Icons.Outlined.Block, null, Modifier.size(18.dp)) },
            )
            AssistChip(
                onClick = {},
                label = { Text(if (blocker.blockerEnabled) "VPN running" else "VPN stopped") },
                leadingIcon = { Icon(Icons.Outlined.Public, null, Modifier.size(18.dp)) },
            )
        }
    }
}

@Composable
private fun UnblockChallengeDialog(
    fact: String,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
) {
    var typed by remember { mutableStateOf("") }
    val wordCount = typed.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    val canDisable = wordCount >= 50

    AlertDialog(
        onDismissRequest = onCancel,
        icon = { Icon(Icons.Outlined.LockOpen, contentDescription = null) },
        title = { Text("Slow down first") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(fact, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    label = { Text("Type at least 50 words about why you are turning this off") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                )
                Text("$wordCount / 50 words", color = if (canDisable) StreakOrangeDeep else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { Button(onClick = onComplete, enabled = canDisable) { Text("Disable blocker") } },
        dismissButton = { TextButton(onClick = onCancel) { Text("Keep blocker on") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    padding: PaddingValues,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { DetailTopBar(title = "Settings", onBack = onBack) },
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { SettingsSectionTitle("Appearance") }
            item {
                SettingsCard {
                    SettingsChoiceRow(
                        title = "Theme",
                        subtitle = when (settings.themeMode) {
                            ThemeMode.System -> "Follow system"
                            ThemeMode.Light -> "Light"
                            ThemeMode.Dark -> "Dark"
                        },
                        icon = when (settings.themeMode) {
                            ThemeMode.System -> Icons.Outlined.SettingsBrightness
                            ThemeMode.Light -> Icons.Outlined.LightMode
                            ThemeMode.Dark -> Icons.Outlined.DarkMode
                        },
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            AssistChip(
                                onClick = { viewModel.setThemeMode(mode) },
                                label = { Text(mode.name) },
                                leadingIcon = if (settings.themeMode == mode) ({ Icon(Icons.Outlined.CheckCircle, null, Modifier.size(18.dp)) }) else null,
                            )
                        }
                    }
                    DividerLine()
                    SwitchRow("AMOLED dark", "Use black backgrounds in dark mode.", settings.amoled, viewModel::setAmoled)
                }
            }
            item { SettingsSectionTitle("Calendar") }
            item {
                SettingsCard {
                    SettingsChoiceRow(
                        title = "Week starts on",
                        subtitle = settings.weekStart.label,
                        icon = Icons.Outlined.CalendarMonth,
                    ) {
                        WeekStart.entries.forEach { start ->
                            AssistChip(
                                onClick = { viewModel.setWeekStart(start) },
                                label = { Text(start.label) },
                                leadingIcon = if (settings.weekStart == start) ({ Icon(Icons.Outlined.CheckCircle, null, Modifier.size(18.dp)) }) else null,
                            )
                        }
                    }
                }
            }
            item { SettingsSectionTitle("Focus") }
            item {
                SettingsCard {
                    SwitchRow("Daily check reminder", "Prepare for a future local reminder without habit pauses/skips.", settings.dailyCheckReminder, viewModel::setDailyCheckReminder)
                    DividerLine()
                    SwitchRow("Zen mode", "Keep the app focused on the home streak screen.", settings.zenMode, viewModel::setZenMode)
                }
            }
        }
    }
}

@Composable
private fun PageHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    )
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
    )
}

@Composable
private fun SettingsChoiceRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    choices: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { choices() }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DividerLine() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

private object PornFacts {
    private val facts = listOf(
        "Porn can train the brain to seek novelty, intensity, and instant reward, which may make real intimacy feel less stimulating when the habit becomes compulsive.",
        "Many people report that compulsive porn use is not mainly about sex, but about escaping stress, loneliness, boredom, shame, or emotional discomfort.",
        "Repeatedly using porn during difficult emotions can strengthen an avoidance loop: discomfort appears, porn numbs it briefly, and the original problem remains unsolved.",
        "Porn often presents bodies, consent, pleasure, and relationships in unrealistic ways, which can quietly reshape expectations about sex and connection.",
        "A craving usually peaks and falls like a wave. Delaying for ten minutes often gives the rational brain enough time to regain control.",
        "Turning off protection while aroused is rarely a neutral decision; it is often the habit protecting itself from friction and accountability.",
        "Recovery is easier when barriers are strongest before a craving begins, because willpower is weakest when the cue and opportunity are already present.",
        "Compulsive porn use can steal sleep, focus, motivation, and confidence by converting short urges into long sessions followed by regret.",
        "Escalating content can happen gradually: the brain adapts to familiar stimulation and starts searching for more novelty to get the same effect.",
        "A lapse does not erase progress, but hiding a lapse often feeds shame. Honest tracking turns failure into information instead of identity.",
        "Porn is designed to be frictionless, private, and endless. Recovery often requires making access slower, more visible, and more deliberate.",
        "Healthy intimacy depends on attention, patience, empathy, and presence — the opposite of rapidly switching between endless clips.",
        "The urge to disable a blocker is a signal to pause, breathe, move rooms, contact someone, or do a task that reconnects you to your values.",
        "Every clean day is not just the absence of porn; it is practice in choosing long-term self-respect over short-term escape.",
        "If a trigger keeps recurring at the same time, place, or mood, the solution is often changing the environment rather than promising stronger willpower.",
    )
    fun random(): String = facts.random()
}
