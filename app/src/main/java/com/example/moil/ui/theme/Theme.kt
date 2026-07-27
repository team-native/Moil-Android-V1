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
    val calendarEventBlue: Color,
    val calendarEventGreen: Color,
    val calendarEventYellow: Color,
    val calendarMutedText: Color,
    val scheduleDivider: Color,
    val scheduleMutedText: Color,
    val switchTrack: Color,
    val overlaySurface: Color,
    val profileGroupPrimaryIndicator: Color,
    val profileGroupSecondaryIndicator: Color,
    val profileLogout: Color,
)

val LocalMoilExtraColors = staticCompositionLocalOf<MoilExtraColors> {
    error("MoilExtraColors is not provided.")
}

private val lightPrimaryColor = Color(0xFFBF6B60)
private val lightOnPrimaryColor = Color(0xFFFFFFFF)
private val lightPrimaryContainerColor = Color(0xFFF2F2F7)
private val lightOnPrimaryContainerColor = Color(0xFF15110D)
private val lightSecondaryColor = Color(0xFF009690)
private val lightOnSecondaryColor = Color(0xFFFFFFFF)
private val lightSecondaryContainerColor = Color(0xFFF2F2F7)
private val lightOnSecondaryContainerColor = Color(0xFF15110D)
private val lightTertiaryColor = Color(0xFF8C6EBD)
private val lightOnTertiaryColor = Color(0xFFFFFFFF)
private val lightTertiaryContainerColor = Color(0xFFF2F2F7)
private val lightOnTertiaryContainerColor = Color(0xFF15110D)
private val lightBackgroundColor = Color(0xFFF6F5F2)
private val lightOnBackgroundColor = Color(0xFF15110D)
private val lightSurfaceColor = lightBackgroundColor
private val lightOnSurfaceColor = Color(0xFF15110D)
private val lightSurfaceContainerColor = Color(0xFFFFFFFF)
private val lightSurfaceVariantColor = Color(0xFFF2F2F7)
private val lightOnSurfaceVariantColor = Color(0xFF5A5450)
private val lightOutlineColor = Color(0xFF5D5751)
private val lightOutlineVariantColor = Color(0xFFF2F2F7)
private val lightErrorColor = Color(0xFFBF6B60)
private val lightOnErrorColor = Color(0xFFFFFFFF)
private val lightErrorContainerColor = Color(0xFFF2F2F7)
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
private val darkSurfaceContainerColor = darkSurfaceColor
private val darkSurfaceVariantColor = Color(0xFF24211D)
private val darkOnSurfaceVariantColor = Color(0xFF9B9891)
private val darkOutlineColor = Color(0xFF615D57)
private val darkOutlineVariantColor = Color(0xFF9B9891)
private val darkErrorColor = Color(0xFFBF6B60)
private val darkOnErrorColor = Color(0xFFFFFFFF)
private val darkErrorContainerColor = Color(0xFF24211D)
private val darkOnErrorContainerColor = Color(0xFFBF6B60)

private val memberCyanColor = Color(0xFF00908A)
private val memberVioletColor = Color(0xFF8668B6)
private val memberRoseColor = Color(0xFFAF6297)
private val calendarEventBlueColor = Color(0xFF3E9BE8)
private val calendarEventGreenColor = Color(0xFF39A87B)
private val calendarEventYellowColor = Color(0xFFE9AC18)
private val calendarMutedTextColor = Color(0xFFB7B4B0)
private val lightScheduleDividerColor = Color(0xFFE0DEDA)
private val lightScheduleMutedTextColor = Color(0xFF97918C)
private val profileGroupPrimaryIndicatorColor = Color(0xFF347EC4)
private val profileGroupSecondaryIndicatorColor = Color(0xFF298954)
private val profileLogoutColor = Color(0xFFCF4040)
private val darkScheduleDividerColor = Color(0xFF615D57)
private val darkScheduleMutedTextColor = Color(0xFF9B9891)

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
    surfaceContainerLowest = lightSurfaceContainerColor,
    surfaceContainerLow = lightSurfaceContainerColor,
    surfaceContainer = lightSurfaceContainerColor,
    surfaceContainerHigh = lightSurfaceContainerColor,
    surfaceContainerHighest = lightSurfaceContainerColor,
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
    surfaceContainerLowest = darkSurfaceContainerColor,
    surfaceContainerLow = darkSurfaceContainerColor,
    surfaceContainer = darkSurfaceContainerColor,
    surfaceContainerHigh = darkSurfaceContainerColor,
    surfaceContainerHighest = darkSurfaceContainerColor,
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
    calendarEventBlue = calendarEventBlueColor,
    calendarEventGreen = calendarEventGreenColor,
    calendarEventYellow = calendarEventYellowColor,
    calendarMutedText = calendarMutedTextColor,
    scheduleDivider = lightScheduleDividerColor,
    scheduleMutedText = lightScheduleMutedTextColor,
    switchTrack = lightScheduleDividerColor,
    overlaySurface = Color.White,
    profileGroupPrimaryIndicator = profileGroupPrimaryIndicatorColor,
    profileGroupSecondaryIndicator = profileGroupSecondaryIndicatorColor,
    profileLogout = profileLogoutColor,
)

private val darkMoilExtraColors = MoilExtraColors(
    memberCyan = memberCyanColor,
    memberViolet = memberVioletColor,
    memberRose = memberRoseColor,
    calendarEventBlue = calendarEventBlueColor,
    calendarEventGreen = calendarEventGreenColor,
    calendarEventYellow = calendarEventYellowColor,
    calendarMutedText = calendarMutedTextColor,
    scheduleDivider = darkScheduleDividerColor,
    scheduleMutedText = darkScheduleMutedTextColor,
    switchTrack = darkScheduleDividerColor,
    overlaySurface = darkSurfaceColor,
    profileGroupPrimaryIndicator = profileGroupPrimaryIndicatorColor,
    profileGroupSecondaryIndicator = profileGroupSecondaryIndicatorColor,
    profileLogout = profileLogoutColor,
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
    val extraColors = if (darkTheme) {
        darkMoilExtraColors
    } else {
        moilExtraColors
    }

    CompositionLocalProvider(
        LocalMoilExtraColors provides extraColors,
        LocalMoilExtraTypography provides MoilDefaultExtraTypography,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
