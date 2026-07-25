package com.example.moil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MoilExtraColors(
    val memberCyan: Color,
    val memberViolet: Color,
    val memberRose: Color,
)

val LocalMoilExtraColors = staticCompositionLocalOf<MoilExtraColors> {
    error("MoilExtraColors is not provided.")
}

private val lightPrimaryColor = Color(0xFFBF6B60)
private val lightOnPrimaryColor = Color(0xFFFFFFFF)
private val lightPrimaryContainerColor = Color(0xFFF3F1EF)
private val lightOnPrimaryContainerColor = Color(0xFF171613)
private val lightSecondaryColor = Color(0xFF009690)
private val lightOnSecondaryColor = Color(0xFFFFFFFF)
private val lightSecondaryContainerColor = Color(0xFFF3F1EF)
private val lightOnSecondaryContainerColor = Color(0xFF171613)
private val lightTertiaryColor = Color(0xFF8C6EBD)
private val lightOnTertiaryColor = Color(0xFFFFFFFF)
private val lightTertiaryContainerColor = Color(0xFFF3F1EF)
private val lightOnTertiaryContainerColor = Color(0xFF171613)
private val lightBackgroundColor = Color(0xFFF3F1EF)
private val lightOnBackgroundColor = Color(0xFF171613)
private val lightSurfaceColor = Color(0xFFFCFCFC)
private val lightOnSurfaceColor = Color(0xFF171613)
private val lightSurfaceVariantColor = Color(0xFFF3F1EF)
private val lightOnSurfaceVariantColor = Color(0xFF615D57)
private val lightOutlineColor = Color(0xFF9B9891)
private val lightOutlineVariantColor = Color(0xFF615D57)
private val lightErrorColor = Color(0xFFBF6B60)
private val lightOnErrorColor = Color(0xFFFFFFFF)
private val lightErrorContainerColor = Color(0xFFF3F1EF)
private val lightOnErrorContainerColor = Color(0xFFBF6B60)

private val darkPrimaryColor = Color(0xFFBF6B60)
private val darkOnPrimaryColor = Color(0xFFFFFFFF)
private val darkPrimaryContainerColor = Color(0xFF24211D)
private val darkOnPrimaryContainerColor = Color(0xFFF3F1EF)
private val darkSecondaryColor = Color(0xFF009690)
private val darkOnSecondaryColor = Color(0xFFFFFFFF)
private val darkSecondaryContainerColor = Color(0xFF24211D)
private val darkOnSecondaryContainerColor = Color(0xFFF3F1EF)
private val darkTertiaryColor = Color(0xFF8C6EBD)
private val darkOnTertiaryColor = Color(0xFFFFFFFF)
private val darkTertiaryContainerColor = Color(0xFF24211D)
private val darkOnTertiaryContainerColor = Color(0xFFF3F1EF)
private val darkBackgroundColor = Color(0xFF171613)
private val darkOnBackgroundColor = Color(0xFFF3F1EF)
private val darkSurfaceColor = Color(0xFF24211D)
private val darkOnSurfaceColor = Color(0xFFF3F1EF)
private val darkSurfaceVariantColor = Color(0xFF24211D)
private val darkOnSurfaceVariantColor = Color(0xFF9B9891)
private val darkOutlineColor = Color(0xFF615D57)
private val darkOutlineVariantColor = Color(0xFF9B9891)
private val darkErrorColor = Color(0xFFBF6B60)
private val darkOnErrorColor = Color(0xFFFFFFFF)
private val darkErrorContainerColor = Color(0xFF24211D)
private val darkOnErrorContainerColor = Color(0xFFBF6B60)

private val memberCyanColor = Color(0xFF009690)
private val memberVioletColor = Color(0xFF8C6EBD)
private val memberRoseColor = Color(0xFFAF6297)

private val LightColorScheme = lightColorScheme(
    primary = lightPrimaryColor,
    onPrimary = lightOnPrimaryColor,
    primaryContainer = lightPrimaryContainerColor,
    onPrimaryContainer = lightOnPrimaryContainerColor,
    secondary = lightSecondaryColor,
    onSecondary = lightOnSecondaryColor,
    secondaryContainer = lightSecondaryContainerColor,
    onSecondaryContainer = lightOnSecondaryContainerColor,
    tertiary = lightTertiaryColor,
    onTertiary = lightOnTertiaryColor,
    tertiaryContainer = lightTertiaryContainerColor,
    onTertiaryContainer = lightOnTertiaryContainerColor,
    background = lightBackgroundColor,
    onBackground = lightOnBackgroundColor,
    surface = lightSurfaceColor,
    onSurface = lightOnSurfaceColor,
    surfaceVariant = lightSurfaceVariantColor,
    onSurfaceVariant = lightOnSurfaceVariantColor,
    outline = lightOutlineColor,
    outlineVariant = lightOutlineVariantColor,
    error = lightErrorColor,
    onError = lightOnErrorColor,
    errorContainer = lightErrorContainerColor,
    onErrorContainer = lightOnErrorContainerColor,
)

private val DarkColorScheme = darkColorScheme(
    primary = darkPrimaryColor,
    onPrimary = darkOnPrimaryColor,
    primaryContainer = darkPrimaryContainerColor,
    onPrimaryContainer = darkOnPrimaryContainerColor,
    secondary = darkSecondaryColor,
    onSecondary = darkOnSecondaryColor,
    secondaryContainer = darkSecondaryContainerColor,
    onSecondaryContainer = darkOnSecondaryContainerColor,
    tertiary = darkTertiaryColor,
    onTertiary = darkOnTertiaryColor,
    tertiaryContainer = darkTertiaryContainerColor,
    onTertiaryContainer = darkOnTertiaryContainerColor,
    background = darkBackgroundColor,
    onBackground = darkOnBackgroundColor,
    surface = darkSurfaceColor,
    onSurface = darkOnSurfaceColor,
    surfaceVariant = darkSurfaceVariantColor,
    onSurfaceVariant = darkOnSurfaceVariantColor,
    outline = darkOutlineColor,
    outlineVariant = darkOutlineVariantColor,
    error = darkErrorColor,
    onError = darkOnErrorColor,
    errorContainer = darkErrorContainerColor,
    onErrorContainer = darkOnErrorContainerColor,
)

private val moilExtraColors = MoilExtraColors(
    memberCyan = memberCyanColor,
    memberViolet = memberVioletColor,
    memberRose = memberRoseColor,
)

@Composable
fun MoilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    CompositionLocalProvider(
        LocalMoilExtraColors provides moilExtraColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
