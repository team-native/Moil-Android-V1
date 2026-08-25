package com.example.moil.feature.calendar.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilScheduleSheet

@Composable
internal fun ScheduleSheetDragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MoilScheduleSheet.DragHandleWidth,
                    height = MoilScheduleSheet.DragHandleHeight,
                )
                .clip(RoundedCornerShape(percent = 50))
                .background(LocalMoilExtraColors.current.scheduleDivider),
        )
    }
}
