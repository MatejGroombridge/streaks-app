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

data class HabitIcon(val key: String, val label: String, val icon: ImageVector)

/**
 * One entry of the shared app-family palette. [light] and [dark] are card
 * backgrounds for each theme, [accent] is the stronger fill for icon tiles,
 * chips and failure marks, and [onColor] is legible over [light] and [accent].
 */
data class HabitColor(
    val key: String,
    val label: String,
    val light: Color,
    val dark: Color,
    val accent: Color,
    val onColor: Color,
)

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
 * The canonical palette shared by every app in the family, ordered around the
 * colour wheel so neighbouring chips look related. Persisted by key.
 */
object HabitColors {
    val all: List<HabitColor> = listOf(
        HabitColor("blush", "Blush", Color(0xFFFFE0E6), Color(0xFF5A3A42), Color(0xFFF7A6B5), Color(0xFF3A1F25)),
        HabitColor("peach", "Peach", Color(0xFFFFE3D1), Color(0xFF5A3F30), Color(0xFFFFB48A), Color(0xFF3A2418)),
        HabitColor("butter", "Butter", Color(0xFFFFF4C2), Color(0xFF55502B), Color(0xFFFFE066), Color(0xFF3A330A)),
        HabitColor("mint", "Mint", Color(0xFFD1F0DA), Color(0xFF2E4D3A), Color(0xFF8DD6A4), Color(0xFF143222)),
        HabitColor("teal", "Teal", Color(0xFFCFE8E4), Color(0xFF2F4D49), Color(0xFF8DCDC4), Color(0xFF143230)),
        HabitColor("sky", "Sky", Color(0xFFD3E8F5), Color(0xFF2F4756), Color(0xFF8FC4E0), Color(0xFF12303F)),
        HabitColor("lavender", "Lavender", Color(0xFFE3DAF5), Color(0xFF3F354F), Color(0xFFB7A5DD), Color(0xFF231A38)),
        HabitColor("fog", "Fog", Color(0xFFE2E5EA), Color(0xFF40454D), Color(0xFFB6BCC6), Color(0xFF22262D)),
    )

    private val defaultEntry = all.first { it.key == BadHabit.DEFAULT_COLOR_KEY }

    fun entry(key: String): HabitColor = all.firstOrNull { it.key == key } ?: defaultEntry

    /** The colour across the wheel from [key], so a new secondary habit contrasts with the primary. */
    fun contrasting(key: String): HabitColor {
        val index = all.indexOf(entry(key))
        return all[(index + all.size / 2) % all.size]
    }
}

val BadHabit.icon: ImageVector get() = HabitIcons.entry(iconKey).icon

val BadHabit.palette: HabitColor get() = HabitColors.entry(colorKey)
