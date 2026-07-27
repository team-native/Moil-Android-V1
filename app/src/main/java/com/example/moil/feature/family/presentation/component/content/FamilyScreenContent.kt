package com.example.moil.feature.family.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilGroupDetailDimension
import com.example.moil.ui.theme.MoilMemberDimension

@Composable
internal fun FamilyScreenContent(
    uiState: FamilyUiState,
    onEvent: (FamilyScreenEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = MoilGroupDetailDimension.ScreenHorizontalPadding,
                    vertical = MoilGroupDetailDimension.ContentTopPadding,
                )
                .verticalScroll(rememberScrollState()),
        ) {
            FamilyDetailHeader(
                groupName = groupDisplayName(uiState.selectedGroup),
                onBackClick = { onEvent(FamilyScreenEvent.BackClicked) },
            )

            Spacer(modifier = Modifier.height(MoilGroupDetailDimension.SectionLabelTopSpacing))

            FamilySectionTitle(text = stringResource(R.string.family_member_section))

            Spacer(modifier = Modifier.height(MoilGroupDetailDimension.SectionLabelBottomSpacing))

            FamilyMemberCard(
                members = uiState.selectedGroup.members,
                memberRoleOverrides = uiState.memberRoleOverrides,
            )

            Spacer(modifier = Modifier.height(MoilGroupDetailDimension.ScheduleSectionTopSpacing))

            FamilySectionTitle(text = stringResource(R.string.family_month_schedule_section))

            Spacer(modifier = Modifier.height(MoilGroupDetailDimension.SectionLabelBottomSpacing))

            FamilyScheduleCard()

            Spacer(modifier = Modifier.height(MoilGroupDetailDimension.LeaveActionTopSpacing))

            Text(
                text = stringResource(R.string.family_group_leave),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(FamilyScreenEvent.BackClicked) }
                    .padding(vertical = MoilGroupDetailDimension.LeaveActionVerticalPadding),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FamilyDetailHeader(
    groupName: String,
    onBackClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.common_chevron_back),
            contentDescription = stringResource(R.string.common_back),
            modifier = Modifier
                .size(MoilGroupDetailDimension.HeaderIconTouchTarget)
                .clickable(onClick = onBackClick)
                .padding(
                    horizontal = (MoilGroupDetailDimension.HeaderIconTouchTarget - MoilGroupDetailDimension.HeaderIconWidth) / 2,
                    vertical = (MoilGroupDetailDimension.HeaderIconTouchTarget - MoilGroupDetailDimension.HeaderIconHeight) / 2,
                ),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.width(MoilGroupDetailDimension.HeaderContentSpacing))

        Text(
            text = groupName,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
private fun FamilyMemberCard(
    members: List<FamilyMemberUiModel>,
    memberRoleOverrides: Map<Int, Int>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilGroupDetailDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Column {
            members.forEachIndexed { index, member ->
                FamilyMemberRow(
                    nameRes = member.nameRes,
                    avatarRes = member.avatarRes,
                    roleRes = memberRoleOverrides[member.nameRes] ?: member.roleRes,
                    customName = member.customName,
                    rowHeight = MoilGroupDetailDimension.MemberRowHeight,
                    avatarSize = MoilGroupDetailDimension.MemberAvatarSize,
                    horizontalPadding = MoilGroupDetailDimension.MemberRowHorizontalPadding,
                    contentSpacing = MoilGroupDetailDimension.MemberContentSpacing,
                )

                if (index < members.lastIndex) {
                    HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
                }
            }
        }
    }
}

@Composable
private fun FamilyScheduleCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilGroupDetailDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Text(
            text = stringResource(R.string.family_month_schedule_count),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MoilGroupDetailDimension.ScheduleCardVerticalPadding),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FamilySectionTitle(text: String) {
    Text(
        text = text,
        color = LocalMoilExtraColors.current.scheduleMutedText,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun groupDisplayName(group: GroupUiModel): String = group.customName
    ?: stringResource(requireNotNull(group.nameRes))
