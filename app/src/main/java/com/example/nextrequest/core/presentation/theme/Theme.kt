package com.example.nextrequest.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

    MaterialTheme(
        colorScheme = colors, typography = Typography, content = content
    )
}

val ColorScheme.textMuted
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF585958) else Color(0xFF888888)


val ColorScheme.iconTint
    @Composable
    get() = if (isSystemInDarkTheme())
        Color(0xFFFFFFFF) else
        Color(0xFF2F409D)

val ColorScheme.iconOnBackground
    @Composable
    get() = if (isSystemInDarkTheme())
        Color(0xFF585958) else
        Color(0xFFBEBBBB)

val ColorScheme.focusedBorderColor
    get() =primary.copy(alpha = 0.5f)

val ColorScheme.unfocusedBorderColor
    get() =primary.copy(alpha = 0.4f)
