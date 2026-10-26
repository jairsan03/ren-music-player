package com.dn0ne.player.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
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
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

val renLightColorScheme = lightColorScheme(
    primary = renPrimary,
    onPrimary = Color(0xFF2C171B),
    primaryContainer = Color(0xFFF0D4D9),
    onPrimaryContainer = Color(0xFF3A2025),
    secondary = renSecondary,
    onSecondary = Color(0xFF272331),
    secondaryContainer = Color(0xFFE3DDEC),
    onSecondaryContainer = Color(0xFF302B39),
    tertiary = renTertiary,
    onTertiary = Color(0xFF351A13),
    tertiaryContainer = Color(0xFFF2D8D0),
    onTertiaryContainer = Color(0xFF3E2119),
    background = renLightBackground,
    onBackground = Color(0xFF211B1C),
    surface = renLightBackground,
    onSurface = Color(0xFF211B1C),
    surfaceVariant = Color(0xFFEAE3DE),
    onSurfaceVariant = Color(0xFF514849),
    outline = Color(0xFF817578),
    outlineVariant = Color(0xFFD3C7C8),
    surfaceContainerLowest = Color(0xFFFFFBF8),
    surfaceContainerLow = Color(0xFFFAF4F0),
    surfaceContainer = Color(0xFFF2EAE6),
    surfaceContainerHigh = Color(0xFFECE3E0),
    surfaceContainerHighest = Color(0xFFE6DDDA)
)

val renDarkColorScheme = darkColorScheme(
    primary = renPrimary,
    onPrimary = Color(0xFF382127),
    primaryContainer = Color(0xFF60414A),
    onPrimaryContainer = Color(0xFFF0D4D9),
    secondary = renSecondary,
    onSecondary = Color(0xFF302B39),
    secondaryContainer = Color(0xFF494354),
    onSecondaryContainer = Color(0xFFE3DDEC),
    tertiary = renTertiary,
    onTertiary = Color(0xFF3E2119),
    tertiaryContainer = Color(0xFF65463D),
    onTertiaryContainer = Color(0xFFF2D8D0),
    background = renDarkBackground,
    onBackground = Color(0xFFF1E9E8),
    surface = renDarkBackground,
    onSurface = Color(0xFFF1E9E8),
    surfaceVariant = Color(0xFF494147),
    onSurfaceVariant = Color(0xFFD2C4C6),
    outline = Color(0xFF9B8E91),
    outlineVariant = Color(0xFF494147),
    surfaceContainerLowest = Color(0xFF211B24),
    surfaceContainerLow = Color(0xFF2D2730),
    surfaceContainer = Color(0xFF332D36),
    surfaceContainerHigh = Color(0xFF3E3741),
    surfaceContainerHighest = Color(0xFF49424C)
)

@Composable
fun RenMaterialTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) renDarkColorScheme else renLightColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    useRenPalette: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useRenPalette -> if (darkTheme) renDarkColorScheme else renLightColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
