package com.example.moil.feature.calendar.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.core.component.content.MoilEmptyJoinedGroupContent

@Composable
internal fun CalendarEmptyGroupContent(
    onJoinGroupClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MoilEmptyJoinedGroupContent(
        onJoinGroupClick = onJoinGroupClick,
        onCreateGroupClick = onCreateGroupClick,
        modifier = modifier,
    )
}

@Composable
internal fun CalendarGroupLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun CalendarGroupErrorContent(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.calendar_load_error),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoilPrimaryButton(
            text = stringResource(R.string.calendar_retry),
            onClick = onRetryClick,
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(0.45f),
        )
    }
}
