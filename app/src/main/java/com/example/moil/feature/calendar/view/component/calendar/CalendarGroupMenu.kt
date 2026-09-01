package com.example.moil.feature.calendar.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.clickable
import com.example.moil.core.component.ApplyDialogWindowBackgroundBlur
import com.example.moil.feature.calendar.viewmodel.CalendarGroupUiModel
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilCalendarDimension

@Composable
internal fun CalendarGroupMenu(
    groups: List<CalendarGroupUiModel>,
    selectedGroupId: Long?,
    onGroupClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 별도 Dialog Window로 승격시켜 ApplyDialogWindowBackgroundBlur가 실제로 동작하도록 한다.
    // (일반 Composable 트리 노드에서는 DialogWindowProvider를 찾지 못해 블러가 걸리지 않는다.)
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        ApplyDialogWindowBackgroundBlur(
            blurRadius = MoilCalendarDimension.GroupMenuBackgroundBlur,
            dimAmount = 0f,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .width(116.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                groups.forEachIndexed { groupIndex, calendarGroup ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clickable { onGroupClick(calendarGroup.id) }
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (calendarGroup.id == selectedGroupId) {
                                        LocalMoilExtraColors.current.calendarEventBlue
                                    } else {
                                        LocalMoilExtraColors.current.calendarEventGreen
                                    },
                                ),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = calendarGroup.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
