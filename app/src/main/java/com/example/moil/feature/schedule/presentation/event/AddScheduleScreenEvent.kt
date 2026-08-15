package com.example.moil.feature.schedule.presentation

import com.example.moil.core.component.MoilNavigationDestination

sealed interface AddScheduleScreenEvent {
    data class DestinationClicked(val destination: MoilNavigationDestination) : AddScheduleScreenEvent
    data class TitleChanged(val title: String) : AddScheduleScreenEvent
    data object CompleteClicked : AddScheduleScreenEvent
}
