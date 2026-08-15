package com.example.moil.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilScheduleSheet

@Composable
fun CommonSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val switchDescription = stringResource(R.string.common_switch)
    val thumbAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = modifier
            .size(
                width = MoilScheduleSheet.SwitchWidth,
                height = MoilScheduleSheet.SwitchHeight,
            )
            .clip(RoundedCornerShape(percent = 50))
            .background(
                if (checked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    LocalMoilExtraColors.current.scheduleDivider
                },
            )
            .semantics {
                contentDescription = switchDescription
            }
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = thumbAlignment,
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 1.dp,
                    shape = CircleShape,
                )
                .padding(horizontal = MoilScheduleSheet.SwitchThumbPadding)
                .size(MoilScheduleSheet.SwitchThumbSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
        )
    }
}
