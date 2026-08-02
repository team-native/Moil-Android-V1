package com.example.moil.core.component.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.moil.R
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.ui.theme.MoilCalendarEmptyGroupDimension
import com.example.moil.ui.theme.MoilRadius

/** 아직 참여한 그룹이 없을 때 참여 또는 생성을 안내하는 공용 Content입니다. */
@Composable
fun MoilEmptyJoinedGroupContent(
    onJoinGroupClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MoilCalendarEmptyGroupDimension.HorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.calendar_empty_group_mascot),
            contentDescription = null,
            modifier = Modifier.size(
                width = MoilCalendarEmptyGroupDimension.MascotWidth,
                height = MoilCalendarEmptyGroupDimension.MascotHeight,
            ),
        )

        Spacer(modifier = Modifier.height(MoilCalendarEmptyGroupDimension.MascotTitleSpacing))

        Text(
            text = stringResource(R.string.calendar_empty_group_title),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(MoilCalendarEmptyGroupDimension.TitleDescriptionSpacing))

        Text(
            text = stringResource(R.string.calendar_empty_group_description),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(MoilCalendarEmptyGroupDimension.DescriptionActionSpacing))

        MoilPrimaryButton(
            text = stringResource(R.string.calendar_empty_group_join),
            onClick = onJoinGroupClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilCalendarEmptyGroupDimension.ActionHeight),
        )

        Spacer(modifier = Modifier.height(MoilCalendarEmptyGroupDimension.ActionSpacing))

        OutlinedButton(
            onClick = onCreateGroupClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilCalendarEmptyGroupDimension.ActionHeight),
            shape = RoundedCornerShape(MoilRadius.Button),
        ) {
            Text(text = stringResource(R.string.calendar_empty_group_create))
        }
    }
}
