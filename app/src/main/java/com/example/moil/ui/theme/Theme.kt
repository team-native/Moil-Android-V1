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
    val bookCoverPlaceholder: Color,
)

val LocalMoilExtraColors = staticCompositionLocalOf<MoilExtraColors> {
    error("MoilExtraColors is not provided.")
}

val LocalMoilExtraTypography = staticCompositionLocalOf<MoilExtraTypography> {
    error("MoilExtraTypography is not provided.")
}

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
)

private val LightExtraColors = MoilExtraColors(
    bookCoverPlaceholder = LightBookCoverPlaceholder,
)

private val DarkExtraColors = MoilExtraColors(
    bookCoverPlaceholder = DarkBookCoverPlaceholder,
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
        DarkExtraColors
    } else {
        LightExtraColors
    }

    CompositionLocalProvider(
        LocalMoilExtraColors provides extraColors,
        LocalMoilExtraTypography provides DefaultMoilExtraTypography,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
