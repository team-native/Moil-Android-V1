package com.example.moil.core.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.moil.ui.theme.MoilSpacing

@Composable
fun MoilTabScaffold(
    selectedDestination: MoilNavigationDestination,
    onDestinationClick: (MoilNavigationDestination) -> Unit,
    contentHorizontalPadding: Dp = MoilSpacing.ScreenHorizontal,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            MoilBottomNavigation(
                selectedDestination = selectedDestination,
                onDestinationClick = onDestinationClick,
            )
        },
    ) { innerPadding ->
        content(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = contentHorizontalPadding,
                    vertical = MoilSpacing.HeaderTop,
                ),
        )
    }
}
