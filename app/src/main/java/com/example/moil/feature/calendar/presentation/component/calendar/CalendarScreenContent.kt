package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.core.component.MoilBottomNavigation
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.feature.calendar.presentation.CalendarScreenEvent
import com.example.moil.feature.calendar.presentation.CalendarUiState
import com.example.moil.ui.theme.MoilSpacing

@Composable
internal fun CalendarScreenContent(
    uiState: CalendarUiState,
    onEvent: (CalendarScreenEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            MoilBottomNavigation(
                selectedDestination = MoilNavigationDestination.Calendar,
                onDestinationClick = { destination ->
                    onEvent(CalendarScreenEvent.DestinationClicked(destination))
                },
            )
        },
    ) { innerPadding ->
        CalendarContent(
            uiState = uiState,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MoilSpacing.ScreenHorizontal),
        )
    }
}
