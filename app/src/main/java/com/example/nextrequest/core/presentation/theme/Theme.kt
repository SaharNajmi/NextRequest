package com.example.nextrequest.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    error = errorLight,
    outline = outlineLight,
    errorContainer = errorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    error = errorDark,
    outline = outlineDark,
    errorContainer = errorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) darkScheme else lightScheme
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}

val ColorScheme.isDark: Boolean
    get() = background.luminance() < 0.5f

val ColorScheme.textMuted: Color
    get() = if (isDark) Color(0xFF585958) else Color(0xFF888888)

val ColorScheme.iconMuted: Color
    get() = if (isDark) Color(0xFF9E9E9E) else Color(0xFF555555)

val ColorScheme.iconTint: Color
    get() = if (isDark) Color(0xFFFFFFFF) else Color(0xFF2F409D)

val ColorScheme.iconOnBackground: Color
    get() = if (isDark) Color(0xFF585958) else Color(0xFFBEBBBB)

val ColorScheme.focusedBorderColor: Color
    get() = primary.copy(alpha = 0.5f)

val ColorScheme.unfocusedBorderColor: Color
    get() = primary.copy(alpha = 0.4f)

val ColorScheme.inputBackground: Color
    get() = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF0F0F0)

val ColorScheme.cardBackground: Color
    get() = if (isDark) Color(0xFF1C1C1E) else Color(0xFFFAFAFA)

val ColorScheme.cardBorder: Color
    get() = if (isDark) Color(0xFF2E2E2E) else Color(0xFFE8E8E8)

val ColorScheme.dropdownBorder: Color
    get() = if (isDark) Color(0xFF3A3A3A) else Color(0xFFE0E0E0)

val ColorScheme.chipTintAlpha: Float
    get() = if (isDark) 0.12f else 0.1f

val ColorScheme.tagChipBackground: Color
    get() = if (isDark) Color(0xFF0D2010) else Color(0xFFE8F5EE)

val ColorScheme.tagChipBorder: Color
    get() = if (isDark) Color(0xFF1D4428) else Color(0xFF9DCC80)

val ColorScheme.tagChipText: Color
    get() = if (isDark) Color(0xFF6DC88A) else Color(0xFF1F7922)

val ColorScheme.tagChipRemove: Color
    get() = if (isDark) Color(0xFF3D7A50) else Color(0xFF7DAA60)

val ColorScheme.jsonChipBackground: Color
    get() = if (isDark) Color(0xFF2A2A2A) else Color(0xFFEFEFEF)

val ColorScheme.jsonChipBorder: Color
    get() = if (isDark) Color(0xFF2A2A2A) else Color(0xFFE7E4E4)

@Composable
fun inputFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.inputBackground,
    unfocusedContainerColor = MaterialTheme.colorScheme.inputBackground,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
)
