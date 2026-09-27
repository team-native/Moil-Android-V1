package com.example.moil.navigation.route

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.example.moil.core.component.applyDialogBackdropBlur
import com.example.moil.feature.calendar.view.CalendarScreen
import com.example.moil.navigation.CalendarScreenEventHandler
import com.example.moil.navigation.MoilMainDestination
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState
import com.example.moil.ui.theme.MoilTimePickerDimension

/**
 * 달력 탭 목적지다. 시간 선택 다이얼로그가 떠 있는 동안 창 배경 블러가 막힌 기기를 대비해
 * 달력 자체도 흐리게 만드는데, 이 판단(다이얼로그가 back stack에 떠 있는지)은 네비게이션 상태에
 * 대한 지식이라 NavDisplay가 아니라 이 Route가 [navigator]로 직접 확인하고 UI(Modifier)에 반영한다.
 */
@Composable
internal fun CalendarRoute(
    mainUiState: MoilMainUiState,
    calendarScreenEventHandler: CalendarScreenEventHandler,
    navigator: MoilMainNavigator,
) {
    val isTimePickerOpen = navigator.isOnAnyBackStack(MoilMainDestination.ScheduleTimePicker)

    CalendarScreen(
        uiState = mainUiState.calendarUiState,
        onEvent = calendarScreenEventHandler::handle,
        modifier = Modifier.applyDialogBackdropBlur(
            shouldBlur = isTimePickerOpen,
            blurRadius = MoilTimePickerDimension.BackgroundBlur,
        ),
    )
}
