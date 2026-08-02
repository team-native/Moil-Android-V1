package com.example.moil.feature.calendar.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.moil.ui.theme.LocalMoilIsDarkTheme

@Composable
internal fun CalendarMonthNavigationButton(
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val isDarkTheme = LocalMoilIsDarkTheme.current
    val navigationImageModifier = if (isDarkTheme) {
        Modifier.fillMaxSize()
    } else {
        Modifier.size(12.dp)
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            modifier = navigationImageModifier,
            contentScale = ContentScale.Fit,
        )
    }
}
