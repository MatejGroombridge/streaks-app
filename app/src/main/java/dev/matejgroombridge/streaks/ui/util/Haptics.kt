package dev.matejgroombridge.streaks.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/** Intent-first wrapper copied from Habit Tracker so interactions keep parity. */
class Haptics(private val raw: HapticFeedback) {
    fun completion() = raw.performHapticFeedback(HapticFeedbackType.LongPress)
    fun longPress() = raw.performHapticFeedback(HapticFeedbackType.LongPress)
    fun light() = raw.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

@Composable
fun rememberHaptics(): Haptics {
    val raw = LocalHapticFeedback.current
    return remember(raw) { Haptics(raw) }
}
