package com.example.moil.navigation.route

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.R
import com.example.moil.feature.calendar.view.ScheduleSheetContent
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.calendar.viewmodel.toCalendarScheduleUiModel
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainUiState
import kotlinx.coroutines.launch

/** 일정 목록·생성·수정·상세 바텀시트 목적지다. 시트 컨테이너는 overlay scene이 제공한다. */
@Composable
internal fun ScheduleSheetRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    calendarViewModel: CalendarViewModel,
    onEvent: (CalendarScreenEvent) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val mapUnavailableMessage = stringResource(R.string.schedule_map_unavailable)
    val groupUiState by groupViewModel.uiState.collectAsStateWithLifecycle()
    val calendarRemoteUiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val selectedSchedule = calendarRemoteUiState.selectedEvent?.let { selectedEvent ->
        runCatching {
            selectedEvent.toCalendarScheduleUiModel(
                fallbackProfileColor = groupUiState.selectedGroup?.myColor
                    ?: GroupColor.Unknown,
                groupMembers = groupUiState.members,
            )
        }.getOrNull()
    }

    ScheduleSheetContent(
        uiState = mainUiState.calendarUiState,
        selectedSchedule = selectedSchedule,
        isSelectedEventLoading = calendarRemoteUiState.isSelectedEventLoading,
        isMutationLoading = calendarRemoteUiState.isMutationLoading,
        hasSelectedEventError = calendarRemoteUiState.selectedEventError != null,
        hasMutationError = calendarRemoteUiState.mutationError != null,
        onEvent = { event ->
            if (event is CalendarScreenEvent.ScheduleMapClicked) {
                val isMapOpened = context.openLocationInMap(event.location)

                if (!isMapOpened) {
                    coroutineScope.launch {
                        mainUiState.snackbarHostState.showSnackbar(mapUnavailableMessage)
                    }
                }
            } else {
                onEvent(event)
            }
        },
    )
}

// 일정 위치 문자열로 외부 지도 앱 검색을 연다. 좌표가 없으므로 geo 검색 URI를 쓰고,
// 이를 처리할 앱이 없으면 웹 지도 검색으로 대신한다. 둘 다 없으면 false를 돌려준다.
private fun Context.openLocationInMap(location: String): Boolean {
    val encodedLocation = Uri.encode(location)
    val mapIntents = listOf(
        Intent(Intent.ACTION_VIEW, "geo:0,0?q=$encodedLocation".toUri()),
        Intent(Intent.ACTION_VIEW, "https://www.google.com/maps/search/?api=1&query=$encodedLocation".toUri()),
    )

    return mapIntents.any { mapIntent ->
        try {
            startActivity(mapIntent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
