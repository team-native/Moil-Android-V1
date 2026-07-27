package com.example.moil.feature.schedule.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.core.component.MoilTabScaffold
import com.example.moil.ui.theme.MoilSpacing

@Composable
internal fun AddScheduleScreenContent(
    uiState: AddScheduleUiState,
    onEvent: (AddScheduleScreenEvent) -> Unit,
) {
    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.JoinGroup,
        onDestinationClick = { destination ->
            onEvent(AddScheduleScreenEvent.DestinationClicked(destination))
        },
    ) { contentModifier ->
        Column(modifier = contentModifier) {
            Text(
                text = stringResource(R.string.add_schedule_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            Image(
                painter = painterResource(R.drawable.common_mascot),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(MoilSpacing.ContentTop))

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { title ->
                    onEvent(AddScheduleScreenEvent.TitleChanged(title))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.add_schedule_placeholder)) },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(24.dp))

            ScheduleFormValueRow(
                label = stringResource(R.string.family_tab),
                value = stringResource(R.string.add_schedule_group),
            )
            ScheduleFormValueRow(
                label = stringResource(R.string.calendar_tab),
                value = stringResource(R.string.add_schedule_date),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onEvent(AddScheduleScreenEvent.CompleteClicked) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(text = stringResource(R.string.add_schedule_complete))
            }
        }
    }
}
