package com.example.moil.feature.calendar.viewmodel

import java.time.LocalTime

sealed interface EventAvailabilityScreenEvent {
    data object BackClicked : EventAvailabilityScreenEvent
    data class TabSelected(val tab: EventAvailabilityTab) : EventAvailabilityScreenEvent
    data class SlotClicked(val slotStart: LocalTime) : EventAvailabilityScreenEvent
    data object SaveClicked : EventAvailabilityScreenEvent
    data object ClearClicked : EventAvailabilityScreenEvent
    data object RetryClicked : EventAvailabilityScreenEvent
}
