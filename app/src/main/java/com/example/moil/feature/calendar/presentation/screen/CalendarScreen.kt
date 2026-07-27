package com.example.moil.feature.calendar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.feature.calendar.presentation.component.CalendarScreenContent
import com.example.moil.ui.theme.MoilTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onEvent: (CalendarScreenEvent) -> Unit,
) {
    CalendarScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun CalendarScreenPreview() {
    MoilTheme(darkTheme = false) {
        CalendarScreen(
            uiState = CalendarUiState(
                displayedMonth = YearMonth.of(2026, 7),
                selectedDate = LocalDate.of(2026, 7, 22),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun CalendarGroupMenuPreview() {
    MoilTheme(darkTheme = false) {
        CalendarScreen(
            uiState = CalendarUiState(
                displayedMonth = YearMonth.of(2026, 7),
                selectedDate = LocalDate.of(2026, 7, 22),
                isGroupMenuVisible = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun CalendarScheduleSheetVisiblePreview() {
    MoilTheme(darkTheme = false) {
        CalendarScreen(
            uiState = CalendarUiState(
                displayedMonth = YearMonth.of(2026, 7),
                selectedDate = LocalDate.of(2026, 7, 22),
                isScheduleSheetVisible = true,
            ),
            onEvent = {},
        )
    }
}
