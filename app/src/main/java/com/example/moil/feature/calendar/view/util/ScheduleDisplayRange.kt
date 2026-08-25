package com.example.moil.feature.calendar.view

import androidx.compose.runtime.Composable
import java.time.LocalTime

@Composable
internal fun LocalTime.toScheduleDisplayTime(): String = toKoreanTimeDisplay()
