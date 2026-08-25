package com.example.moil.feature.calendar.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilScheduleSheet

@Composable
internal fun ScheduleTitleField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val extraColors = LocalMoilExtraColors.current
    val extraTypography = LocalMoilExtraTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilScheduleSheet.TitleHeight)
            .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = extraTypography.scheduleTitle.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.schedule_title_placeholder),
                            color = extraColors.scheduleMutedText,
                            style = extraTypography.scheduleTitle,
                        )
                    }

                    innerTextField()
                },
            )
        }

        HorizontalDivider(color = extraColors.scheduleDivider)
    }
}
