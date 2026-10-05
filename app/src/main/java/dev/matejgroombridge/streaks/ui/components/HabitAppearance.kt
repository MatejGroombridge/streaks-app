package dev.matejgroombridge.streaks.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Icecream
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalPizza
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Nightlife
import androidx.compose.material.icons.outlined.NoAdultContent
import androidx.compose.material.icons.outlined.OndemandVideo
import androidx.compose.material.icons.outlined.PanTool
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.SmokingRooms
import androidx.compose.material.icons.outlined.Snooze
import androidx.compose.material.icons.outlined.SportsBar
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.matejgroombridge.streaks.data.BadHabit
import dev.matejgroombridge.streaks.ui.theme.StreakOrangeDeep

data class HabitIcon(val key: String, val label: String, val icon: ImageVector)

data class HabitColor(val key: String, val label: String, val color: Color)

/** Curated identity icons for habits. Persisted by key, so append rather than rename. */
object HabitIcons {
    /** Neutral icon suggested for a newly added habit. */
    const val GENERIC_KEY = "block"

    val all: List<HabitIcon> = listOf(
        HabitIcon(BadHabit.DEFAULT_ICON_KEY, "Streak", Icons.Outlined.LocalFireDepartment),
        HabitIcon("adult", "Adult content", Icons.Outlined.NoAdultContent),
        HabitIcon("smoking", "Smoking", Icons.Outlined.SmokingRooms),
        HabitIcon("cocktail", "Alcohol", Icons.Outlined.LocalBar),
        HabitIcon("beer", "Beer", Icons.Outlined.SportsBar),
        HabitIcon("wine", "Wine", Icons.Outlined.WineBar),
        HabitIcon("nightlife", "Partying", Icons.Outlined.Nightlife),
        HabitIcon("coffee", "Caffeine", Icons.Outlined.LocalCafe),
        HabitIcon("energy", "Energy drinks", Icons.Outlined.Bolt),
        HabitIcon("soda", "Soda", Icons.Outlined.LocalDrink),
        HabitIcon("fast_food", "Fast food", Icons.Outlined.Fastfood),
        HabitIcon("pizza", "Takeaway", Icons.Outlined.LocalPizza),
        HabitIcon("sweets", "Sweets", Icons.Outlined.Cake),
        HabitIcon("ice_cream", "Ice cream", Icons.Outlined.Icecream),
        HabitIcon("phone", "Phone", Icons.Outlined.Smartphone),
        HabitIcon("social", "Social media", Icons.Outlined.Forum),
        HabitIcon("video", "Videos", Icons.Outlined.OndemandVideo),
        HabitIcon("tv", "TV", Icons.Outlined.Tv),
        HabitIcon("gaming", "Gaming", Icons.Outlined.SportsEsports),
        HabitIcon("web", "Browsing", Icons.Outlined.Public),
        HabitIcon("gambling", "Gambling", Icons.Outlined.Casino),
        HabitIcon("shopping", "Shopping", Icons.Outlined.ShoppingCart),
        HabitIcon("spending", "Spending", Icons.Outlined.AttachMoney),
        HabitIcon("pills", "Pills", Icons.Outlined.Medication),
        HabitIcon("sleep", "Staying up late", Icons.Outlined.Bedtime),
        HabitIcon("snooze", "Snoozing", Icons.Outlined.Snooze),
        HabitIcon("nail_biting", "Nail biting", Icons.Outlined.PanTool),
        HabitIcon("anger", "Anger", Icons.Outlined.SentimentVeryDissatisfied),
        HabitIcon("swearing", "Swearing", Icons.Outlined.RecordVoiceOver),
        HabitIcon("lust", "Lust", Icons.Outlined.FavoriteBorder),
        HabitIcon("eyes", "Watching", Icons.Outlined.Visibility),
        HabitIcon(GENERIC_KEY, "Other", Icons.Outlined.Block),
    )

    private val default = all.first()

    fun entry(key: String): HabitIcon = all.firstOrNull { it.key == key } ?: default
}

/**
 * Saturated accents that keep white glyphs legible, ordered around the colour
 * wheel so neighbouring chips look related. Persisted by key.
 */
object HabitColors {
    val all: List<HabitColor> = listOf(
        HabitColor("red", "Red", Color(0xFFD93F3F)),
        HabitColor(BadHabit.DEFAULT_COLOR_KEY, "Orange", StreakOrangeDeep),
        HabitColor("amber", "Amber", Color(0xFFC98A00)),
        HabitColor("green", "Green", Color(0xFF2E9D57)),
        HabitColor("teal", "Teal", Color(0xFF00968A)),
        HabitColor("blue", "Blue", Color(0xFF2F7CE0)),
        HabitColor("purple", "Purple", Color(0xFF8358D6)),
        HabitColor("pink", "Pink", Color(0xFFD6458C)),
    )

    private val default = all.first { it.key == BadHabit.DEFAULT_COLOR_KEY }

    fun entry(key: String): HabitColor = all.firstOrNull { it.key == key } ?: default

    /** The colour across the wheel from [key], so a new secondary habit contrasts with the primary. */
    fun contrasting(key: String): HabitColor {
        val index = all.indexOf(entry(key))
        return all[(index + all.size / 2) % all.size]
    }
}

val BadHabit.icon: ImageVector get() = HabitIcons.entry(iconKey).icon

val BadHabit.color: Color get() = HabitColors.entry(colorKey).color
