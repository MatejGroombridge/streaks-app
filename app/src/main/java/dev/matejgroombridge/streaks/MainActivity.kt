package dev.matejgroombridge.streaks

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowUp
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle as ComposeTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.matejgroombridge.streaks.data.AppSettings
import dev.matejgroombridge.streaks.data.BadHabit
import dev.matejgroombridge.streaks.data.HabitSlot
import dev.matejgroombridge.streaks.data.StreakState
import dev.matejgroombridge.streaks.data.ThemeMode
import dev.matejgroombridge.streaks.data.WeekStart
import dev.matejgroombridge.streaks.ui.SettingsViewModel
import dev.matejgroombridge.streaks.ui.StreakViewModel
import dev.matejgroombridge.streaks.ui.components.HabitColor
import dev.matejgroombridge.streaks.ui.components.HabitColors
import dev.matejgroombridge.streaks.ui.components.HabitIcon
import dev.matejgroombridge.streaks.ui.components.HabitIcons
import dev.matejgroombridge.streaks.ui.components.icon
import dev.matejgroombridge.streaks.ui.components.palette
import dev.matejgroombridge.streaks.ui.theme.AppTheme
import dev.matejgroombridge.streaks.ui.theme.StreakOrangeDeep
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime
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

private enum class DetailScreen { Settings }

@Composable
private fun StreaksApp(settingsViewModel: SettingsViewModel) {
    val app = LocalContext.current.applicationContext as Application
    val streakViewModel: StreakViewModel = viewModel(factory = StreakViewModel.factory(app))
    val streakState by streakViewModel.state.collectAsStateWithLifecycle()
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    var detailScreen by remember { mutableStateOf<DetailScreen?>(null) }

    when (detailScreen) {
        DetailScreen.Settings -> SettingsScreen(
            settings = settings,
            viewModel = settingsViewModel,
            streakState = streakState,
            streakViewModel = streakViewModel,
            padding = PaddingValues(),
            onBack = { detailScreen = null },
        )
        null -> HomeScreen(
            state = streakState,
            showHabitNames = settings.showHabitNames,
            viewModel = streakViewModel,
            padding = PaddingValues(),
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
                    failedDays = state.primary.failureEpochDays,
                    days = days,
                    today = today,
                    onSetFailed = { day, failed -> viewModel.setFailure(HabitSlot.Primary, day, failed) },
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
    showHabitNames: Boolean,
    viewModel: StreakViewModel,
    padding: PaddingValues,
    onSettingsClick: () -> Unit,
) {
    var resetConfirmSlot by remember { mutableStateOf<HabitSlot?>(null) }
    var showPastWeekManually by remember { mutableStateOf(false) }
    val today = rememberToday()
    val pastWeekDays = remember(today) { previousSevenDays(today) }
    val primary = state.primary
    val secondary = state.secondary
    val hasRecentFailure = remember(primary.failureEpochDays, secondary?.failureEpochDays, pastWeekDays) {
        pastWeekDays.any { it in primary.failureEpochDays || (secondary != null && it in secondary.failureEpochDays) }
    }
    val showPastWeek = hasRecentFailure || showPastWeekManually
    val todayDate = remember(today) { LocalDate.ofEpochDay(today) }
    val endOfCurrentWeek = remember(todayDate) {
        val daysAfterMon = ((todayDate.dayOfWeek.value - DayOfWeek.MONDAY.value) + 7) % 7
        val mondayOfThisWeek = todayDate.minusDays(daysAfterMon.toLong())
        mondayOfThisWeek.plusDays(6)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            HomeHeader(onOpenSettings = onSettingsClick)
        }
        item {
            CurrentStreakCard(
                primary = primary,
                today = today,
                showNames = showHabitNames,
                onResetClick = { slot -> resetConfirmSlot = slot },
            )
        }
        if (secondary != null) {
            item {
                SecondaryStreakCard(
                    secondary = secondary,
                    today = today,
                    showNames = showHabitNames,
                    onResetClick = { resetConfirmSlot = HabitSlot.Secondary },
                )
            }
        }
        if (showPastWeek) {
            item {
                PastWeekCard(
                    primary = primary,
                    secondary = secondary,
                    days = pastWeekDays,
                    today = today,
                    editable = true,
                    collapsible = !hasRecentFailure,
                    onSetDay = viewModel::setDayFailures,
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

    val confirmSlot = resetConfirmSlot
    val confirmHabit = confirmSlot?.let(state::habit)
    if (confirmSlot != null && confirmHabit != null) {
        AlertDialog(
            onDismissRequest = { resetConfirmSlot = null },
            // With two habits on screen, the icon tells the user which streak they're about to reset.
            icon = { Icon(if (secondary == null) Icons.Outlined.WarningAmber else confirmHabit.icon, contentDescription = null) },
            title = { Text(if (showHabitNames) "Reset your ${confirmHabit.name} streak?" else "Reset your streak?") },
            text = { Text("This marks today as a failure. No shame, no spiral — just an honest reset and the next right action.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordFailureToday(confirmSlot)
                        resetConfirmSlot = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmHabit.palette.accent,
                        contentColor = confirmHabit.palette.onColor,
                    ),
                ) { Text("I slipped today") }
            },
            dismissButton = { TextButton(onClick = { resetConfirmSlot = null }) { Text("Cancel") } },
        )
    }
}

/**
 * Today's epoch day, refreshed when the app resumes and at midnight while it's open.
 * Streaks count whole days, so a stale "today" would leave every stat a day behind.
 */
@Composable
private fun rememberToday(): Long {
    var today by remember { mutableLongStateOf(LocalDate.now().toEpochDay()) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { today = LocalDate.now().toEpochDay() }
    LaunchedEffect(Unit) {
        while (true) {
            val now = ZonedDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
            delay(Duration.between(now, nextMidnight).toMillis() + 1_000)
            today = LocalDate.now().toEpochDay()
        }
    }
    return today
}

@Composable
private fun HomeHeader(
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
private fun CurrentStreakCard(
    primary: BadHabit,
    today: Long,
    showNames: Boolean,
    onResetClick: (HabitSlot) -> Unit,
) {
    HomeCard {
        StreakCountRow(habit = primary, days = primary.currentStreakDays(today), showName = showNames)
        Spacer(Modifier.height(18.dp))
        ResetStreakButton(
            label = "Reset streak",
            icon = Icons.Outlined.WarningAmber,
            colors = primary.palette,
            onClick = { onResetClick(HabitSlot.Primary) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** The secondary habit gets its own card under the primary's, with a reset sized down to match it. */
@Composable
private fun SecondaryStreakCard(
    secondary: BadHabit,
    today: Long,
    showNames: Boolean,
    onResetClick: () -> Unit,
) {
    HomeCard {
        StreakCountRow(habit = secondary, days = secondary.currentStreakDays(today), showName = showNames, compact = true) {
            ResetStreakButton(
                label = "Reset",
                icon = Icons.Outlined.WarningAmber,
                colors = secondary.palette,
                onClick = onResetClick,
                compact = true,
            )
        }
    }
}

/** A habit's streak. The secondary habit uses the [compact] size so the primary stays the focus. */
@Composable
private fun StreakCountRow(
    habit: BadHabit,
    days: Long,
    showName: Boolean,
    compact: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (compact) {
            HabitBadge(habit = habit, size = 44.dp, iconSize = 26.dp, cornerRadius = 14.dp)
        } else {
            HabitBadge(habit = habit, size = 64.dp, iconSize = 38.dp, cornerRadius = 20.dp)
        }
        Column(modifier = Modifier.weight(1f)) {
            if (showName) {
                Text(
                    text = habit.name,
                    style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            StreakDayCount(days = days, compact = compact)
        }
        trailing?.invoke()
    }
}

private class CountSize(val count: ComposeTextStyle, val unit: ComposeTextStyle, val unitBottomPadding: Dp)

/**
 * The day count and its unit. A reset pill can share the row, so a long count steps
 * down a size rather than wrapping, and as a last resort the unit moves underneath.
 */
@Composable
private fun StreakDayCount(days: Long, compact: Boolean) {
    val type = MaterialTheme.typography
    // Largest first; the first entry is the card's normal look.
    val sizes = if (compact) {
        listOf(CountSize(type.headlineLarge, type.titleMedium, 4.dp), CountSize(type.headlineMedium, type.bodyMedium, 3.dp))
    } else {
        listOf(
            CountSize(type.displayMedium, type.headlineSmall, 8.dp),
            CountSize(type.displaySmall, type.titleLarge, 6.dp),
            CountSize(type.headlineLarge, type.titleMedium, 4.dp),
        )
    }
    val count = days.toString()
    val unit = if (days == 1L) "day" else "days"
    val gap = if (compact) 6.dp else 8.dp
    val measurer = rememberTextMeasurer()
    BoxWithConstraints {
        val gapPx = with(LocalDensity.current) { gap.roundToPx() }
        fun countWidth(size: CountSize) = measurer.measure(count, size.count.copy(fontWeight = FontWeight.SemiBold)).size.width
        val fitting = sizes.firstOrNull { size ->
            countWidth(size) + gapPx + measurer.measure(unit, size.unit).size.width <= constraints.maxWidth
        }
        // Stacked, the count has the whole width to itself, so it can stay larger.
        val size = fitting ?: sizes.firstOrNull { countWidth(it) <= constraints.maxWidth } ?: sizes.last()
        val countText = @Composable {
            Text(
                text = count,
                style = size.count,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
            )
        }
        val unitText = @Composable { modifier: Modifier ->
            Text(
                text = unit,
                modifier = modifier,
                style = size.unit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
            )
        }
        if (fitting != null) {
            Row(verticalAlignment = Alignment.Bottom) {
                countText()
                Spacer(Modifier.width(gap))
                unitText(Modifier.padding(bottom = size.unitBottomPadding))
            }
        } else {
            Column {
                countText()
                unitText(Modifier)
            }
        }
    }
}

/** A habit's identity tile: its icon on the palette accent, as in the rest of the app family. */
@Composable
private fun HabitBadge(habit: BadHabit, size: Dp, iconSize: Dp, cornerRadius: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(habit.palette.accent),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            habit.icon,
            contentDescription = null,
            tint = habit.palette.onColor,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Composable
private fun ResetStreakButton(
    label: String,
    icon: ImageVector,
    colors: HabitColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.accent,
            contentColor = colors.onColor,
        ),
        contentPadding = if (compact) PaddingValues(horizontal = 14.dp, vertical = 6.dp) else ButtonDefaults.ContentPadding,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(if (compact) 16.dp else 18.dp))
        Spacer(Modifier.width(if (compact) 6.dp else 8.dp))
        Text(label)
    }
}

@Composable
private fun PastWeekCard(
    primary: BadHabit,
    secondary: BadHabit?,
    days: List<Long>,
    today: Long,
    editable: Boolean,
    collapsible: Boolean,
    onSetDay: (day: Long, primaryFailed: Boolean, secondaryFailed: Boolean) -> Unit,
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
                InlineWeekDay(
                    date = LocalDate.ofEpochDay(epochDay),
                    primary = primary,
                    secondary = secondary,
                    isToday = epochDay == today,
                    editable = editable,
                    onSet = { primaryFailed, secondaryFailed -> onSetDay(epochDay, primaryFailed, secondaryFailed) },
                )
            }
        }
    }
}

/**
 * Tap order for a past-week day: clean → primary broken → secondary broken → both broken → clean.
 * Without a secondary habit a tap simply toggles the primary one.
 */
private fun nextDayState(primaryFailed: Boolean, secondaryFailed: Boolean, hasSecondary: Boolean): Pair<Boolean, Boolean> = when {
    !hasSecondary -> !primaryFailed to false
    !primaryFailed && !secondaryFailed -> true to false
    primaryFailed && !secondaryFailed -> false to true
    !primaryFailed && secondaryFailed -> true to true
    else -> false to false
}

@Composable
private fun InlineWeekDay(
    date: LocalDate,
    primary: BadHabit,
    secondary: BadHabit?,
    isToday: Boolean,
    editable: Boolean,
    onSet: (primaryFailed: Boolean, secondaryFailed: Boolean) -> Unit,
) {
    val epochDay = date.toEpochDay()
    val failed = epochDay in primary.failureEpochDays
    val secondaryFailed = secondary != null && epochDay in secondary.failureEpochDays
    val primaryColors = primary.palette
    val secondaryColors = secondary?.palette
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant
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
                .drawBehind {
                    when {
                        failed && secondaryColors != null && secondaryFailed -> {
                            // Both broken: split diagonally, primary top-left and secondary bottom-right.
                            drawRect(primaryColors.accent)
                            val lowerRight = Path().apply {
                                moveTo(size.width, 0f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(lowerRight, secondaryColors.accent)
                        }
                        failed -> drawRect(primaryColors.accent)
                        secondaryColors != null && secondaryFailed -> drawRect(secondaryColors.accent)
                        else -> drawRect(emptyColor)
                    }
                }
                .semantics {
                    stateDescription = when {
                        failed && secondaryFailed -> "Both habits broken"
                        failed -> "Primary habit broken"
                        secondaryFailed -> "Secondary habit broken"
                        else -> "Clean"
                    }
                }
                .then(
                    if (editable) {
                        Modifier.clickable {
                            val (nextPrimary, nextSecondary) = nextDayState(failed, secondaryFailed, hasSecondary = secondary != null)
                            onSet(nextPrimary, nextSecondary)
                        }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    failed -> primaryColors.onColor
                    secondaryColors != null && secondaryFailed -> secondaryColors.onColor
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = if (failed || secondaryFailed) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

/** Secondary habit failures render as a dot so they never hide the primary habit's filled square. */
@Composable
private fun SecondaryFailureDot(
    colors: HabitColor,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.accent),
    )
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
    // Stats describe the primary habit; the secondary habit only appears in the grid.
    val primary = state.primary
    val secondary = state.secondary
    val cleanDays = remember(primary.startEpochDay, primary.failureEpochDays, today) {
        (primary.startEpochDay..today).count { it !in primary.failureEpochDays }
    }
    val streak = remember(primary.startEpochDay, primary.failureEpochDays, today) { primary.currentStreakDays(today) }
    val topStreak = remember(primary.startEpochDay, primary.failureEpochDays, today) {
        longestCleanStreak(primary.startEpochDay, today, primary.failureEpochDays)
    }
    val resets = remember(primary.startEpochDay, primary.failureEpochDays, today) {
        primary.failureEpochDays.count { it in primary.startEpochDay..today }
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
            createdAtEpochDay = primary.startEpochDay,
            failedDays = primary.failureEpochDays,
            secondaryFailedDays = secondary?.failureEpochDays.orEmpty(),
            today = today,
            endOfCurrentWeek = endOfCurrentWeek,
            accent = primary.palette.accent,
            secondaryColors = secondary?.palette,
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

private val CELL_SIZE = 12.dp
private val CELL_GAP = 3.dp
private val SECONDARY_DOT_SIZE = 6.dp
private val GRID_SIDE_PADDING = 0.dp

@Composable
private fun ContributionGrid(
    createdAtEpochDay: Long,
    failedDays: Set<Long>,
    secondaryFailedDays: Set<Long>,
    today: Long,
    endOfCurrentWeek: LocalDate,
    accent: Color,
    secondaryColors: HabitColor?,
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
                            secondaryFailed = cellEpoch in secondaryFailedDays,
                            inactive = inFuture || beforeStart,
                            accent = accent,
                            secondaryColors = secondaryColors,
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
    secondaryFailed: Boolean,
    inactive: Boolean,
    accent: Color,
    secondaryColors: HabitColor?,
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
        contentAlignment = Alignment.Center,
    ) {
        if (secondaryFailed && secondaryColors != null) {
            SecondaryFailureDot(colors = secondaryColors, size = SECONDARY_DOT_SIZE)
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    streakState: StreakState,
    streakViewModel: StreakViewModel,
    padding: PaddingValues,
    onBack: () -> Unit,
) {
    var editingSlot by remember { mutableStateOf<HabitSlot?>(null) }
    var confirmRemoveSecondary by remember { mutableStateOf(false) }

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
            item { SettingsSectionTitle("Habits") }
            item {
                SettingsCard {
                    HabitSettingsRow(
                        title = "Primary habit",
                        habit = streakState.primary,
                        onClick = { editingSlot = HabitSlot.Primary },
                    )
                    DividerLine()
                    HabitSettingsRow(
                        title = "Secondary habit",
                        habit = streakState.secondary,
                        onClick = { editingSlot = HabitSlot.Secondary },
                    )
                    DividerLine()
                    SwitchRow("Show habit names", "Display each habit's name on the home screen.", settings.showHabitNames, viewModel::setShowHabitNames)
                }
            }
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

    val slot = editingSlot
    if (slot != null) {
        val existing = streakState.habit(slot)
        // Keyed by slot so switching between the two editors never carries over unsaved input.
        key(slot) {
            HabitEditorDialog(
                title = when {
                    slot == HabitSlot.Primary -> "Edit Primary Habit"
                    existing == null -> "Add Secondary Habit"
                    else -> "Edit Secondary Habit"
                },
                initial = existing ?: BadHabit(
                    name = "",
                    iconKey = if (streakState.primary.iconKey == HabitIcons.GENERIC_KEY) BadHabit.DEFAULT_ICON_KEY else HabitIcons.GENERIC_KEY,
                    colorKey = HabitColors.contrasting(streakState.primary.colorKey).key,
                ),
                onDismiss = { editingSlot = null },
                onSave = { name, iconKey, colorKey ->
                    streakViewModel.saveHabit(slot, name, iconKey, colorKey)
                    editingSlot = null
                },
                onRemove = if (slot == HabitSlot.Secondary && existing != null) {
                    { confirmRemoveSecondary = true }
                } else {
                    null
                },
            )
        }
    }

    val secondary = streakState.secondary
    if (confirmRemoveSecondary && secondary != null) {
        AlertDialog(
            onDismissRequest = { confirmRemoveSecondary = false },
            icon = { Icon(Icons.Outlined.WarningAmber, contentDescription = null) },
            title = { Text("Remove ${secondary.name}?") },
            text = { Text("This deletes the secondary habit's streak and failure history. Your primary habit is not affected.") },
            confirmButton = {
                Button(
                    onClick = {
                        streakViewModel.removeSecondaryHabit()
                        confirmRemoveSecondary = false
                        editingSlot = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { confirmRemoveSecondary = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun HabitSettingsRow(
    title: String,
    habit: BadHabit?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (habit != null) {
            HabitBadge(habit = habit, size = 32.dp, iconSize = 20.dp, cornerRadius = 10.dp)
        } else {
            Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                text = habit?.name ?: "Not set. Tap to add one.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private const val ICON_PICKER_COLUMNS = 6

@Composable
private fun HabitEditorDialog(
    title: String,
    initial: BadHabit,
    onDismiss: () -> Unit,
    onSave: (name: String, iconKey: String, colorKey: String) -> Unit,
    onRemove: (() -> Unit)?,
) {
    var name by remember { mutableStateOf(initial.name) }
    var iconKey by remember { mutableStateOf(initial.iconKey) }
    var colorKey by remember { mutableStateOf(initial.colorKey) }
    val selectedColors = HabitColors.entry(colorKey)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(BadHabit.MAX_NAME_LENGTH) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("What do you want to stop?") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                )
                EditorSection("Icon") {
                    HabitIcons.all.chunked(ICON_PICKER_COLUMNS).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            row.forEach { entry ->
                                IconChoice(
                                    entry = entry,
                                    selected = entry.key == iconKey,
                                    colors = selectedColors,
                                    onClick = { iconKey = entry.key },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            repeat(ICON_PICKER_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
                EditorSection("Colour") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        HabitColors.all.forEach { entry ->
                            ColorChoice(
                                entry = entry,
                                selected = entry.key == colorKey,
                                onClick = { colorKey = entry.key },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, iconKey, colorKey) }, enabled = name.isNotBlank()) { Text("Save") }
        },
        dismissButton = {
            Row {
                if (onRemove != null) {
                    TextButton(
                        onClick = onRemove,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) { Text("Remove") }
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },
    )
}

@Composable
private fun EditorSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        content()
    }
}

@Composable
private fun IconChoice(
    entry: HabitIcon,
    selected: Boolean,
    colors: HabitColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (selected) colors.accent else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            entry.icon,
            contentDescription = entry.label,
            tint = if (selected) colors.onColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ColorChoice(
    entry: HabitColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(entry.accent)
            .clickable(onClickLabel = entry.label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = "${entry.label} selected", tint = entry.onColor, modifier = Modifier.size(18.dp))
        }
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
