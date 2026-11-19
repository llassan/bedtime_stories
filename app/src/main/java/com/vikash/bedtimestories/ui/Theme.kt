@file:OptIn(ExperimentalTextApi::class)

package com.vikash.bedtimestories.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vikash.bedtimestories.R

val Night = Color(0xFF0B1033)
val NightSoft = Color(0xFF1A1F55)
val CardBlue = Color(0xFF232A6B)
val Gold = Color(0xFFFFD98E)
val Cream = Color(0xFFFFF6E0)
val Mist = Color(0xFFB9C0F0)
val Rose = Color(0xFFFF9DB5)

val NightBrush = Brush.verticalGradient(listOf(Night, Color(0xFF1B1F5C), Color(0xFF2A2468)))

private fun variable(res: Int, vararg weights: Int) = FontFamily(
    weights.map { w -> Font(res, FontWeight(w), variationSettings = FontVariation.Settings(FontVariation.weight(w))) }
)

/** Rounded and friendly, for titles. */
val Fredoka = variable(R.font.fredoka, 500, 600)
/** Soft and very readable, for story text and everything else. */
val Nunito = variable(R.font.nunito, 500, 600, 700, 800)

private val Colors = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF3A2A00),
    secondary = Mist,
    onSecondary = Night,
    background = Night,
    onBackground = Cream,
    surface = NightSoft,
    onSurface = Cream,
    surfaceVariant = CardBlue,
    onSurfaceVariant = Mist,
    surfaceContainerLow = NightSoft,
    surfaceContainer = NightSoft,
    surfaceContainerHigh = CardBlue,
    surfaceContainerHighest = CardBlue,
    secondaryContainer = Color(0xFF4B459C),
    onSecondaryContainer = Cream,
    outline = Color(0xFF5A62A8),
    error = Rose,
)

private val Type = Typography().run {
    fun TextStyle.body(weight: Int = 600) = copy(fontFamily = Nunito, fontWeight = FontWeight(weight))
    fun TextStyle.title() = copy(fontFamily = Fredoka, fontWeight = FontWeight(600))
    copy(
        headlineLarge = headlineLarge.title(),
        headlineMedium = headlineMedium.title(),
        headlineSmall = headlineSmall.title(),
        titleLarge = titleLarge.title(),
        titleMedium = titleMedium.body(800),
        titleSmall = titleSmall.body(800),
        bodyLarge = bodyLarge.body(),
        bodyMedium = bodyMedium.body(),
        bodySmall = bodySmall.body(),
        labelLarge = labelLarge.body(800),
        labelMedium = labelMedium.body(800),
        labelSmall = labelSmall.body(700),
    )
}

/** The large type used for story text. */
val StoryText = TextStyle(fontFamily = Nunito, fontWeight = FontWeight(600), fontSize = 22.sp, lineHeight = 35.sp, color = Cream)

@Composable
fun BedtimeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = Type) {
        // There is no Surface at the root, so set the default text and icon color here.
        CompositionLocalProvider(
            LocalContentColor provides Cream,
            androidx.compose.material3.LocalTextStyle provides Type.bodyLarge,
            content = content,
        )
    }
}
