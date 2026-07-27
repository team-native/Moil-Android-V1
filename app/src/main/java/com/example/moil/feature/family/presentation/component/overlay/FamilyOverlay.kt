package com.example.moil.feature.family.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilOverlayDialog
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilOverlayDimension
import com.example.moil.ui.theme.MoilRadius

@Composable
fun FamilyGroupNameDialog(
    groupName: String,
    onDismissRequest: () -> Unit,
    onSaveClick: (String) -> Unit,
) {
    var editedGroupName by rememberSaveable(groupName) { mutableStateOf(groupName) }

    MoilOverlayDialog(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.padding(MoilOverlayDimension.DialogContentPadding)) {
            Text(
                text = stringResource(R.string.family_group_name_change),
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogTitleBottomPadding))

            BasicTextField(
                value = editedGroupName,
                onValueChange = { editedGroupName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MoilOverlayDimension.DialogFieldHeight)
                    .clip(RoundedCornerShape(MoilRadius.DialogField))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = MoilOverlayDimension.DialogContentPadding),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        innerTextField()
                    }
                },
            )

            Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogActionTopPadding))

            Row(horizontalArrangement = Arrangement.spacedBy(MoilOverlayDimension.DialogActionSpacing)) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .weight(1f)
                        .height(MoilOverlayDimension.DialogActionHeight),
                    shape = RoundedCornerShape(MoilRadius.DialogButton),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    ),
                ) {
                    Text(text = stringResource(R.string.family_dialog_cancel))
                }

                Button(
                    onClick = { onSaveClick(editedGroupName) },
                    modifier = Modifier
                        .weight(1f)
                        .height(MoilOverlayDimension.DialogActionHeight),
                    shape = RoundedCornerShape(MoilRadius.DialogButton),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(text = stringResource(R.string.family_dialog_save))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMemberPermissionsBottomSheet(
    members: List<FamilyMemberUiModel>,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Map<Int, Int>) -> Unit,
) {
    val memberRoles = remember(members) {
        mutableStateOf(members.associate { member -> member.nameRes to member.roleRes })
    }

    FamilyModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = stringResource(R.string.family_member_permissions_title),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(MoilOverlayDimension.SheetTitleBottomPadding))

        members.forEach { member ->
            val selectedRoleRes = memberRoles.value.getValue(member.nameRes)

            FamilyMemberRoleRow(
                member = member,
                selectedRoleRes = selectedRoleRes,
                onRoleClick = { roleRes ->
                    memberRoles.value = memberRoles.value + (member.nameRes to roleRes)
                },
            )
        }

        Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogActionTopPadding))

        Button(
            onClick = { onConfirmClick(memberRoles.value) },
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilOverlayDimension.DialogActionHeight),
            shape = RoundedCornerShape(MoilRadius.DialogButton),
        ) {
            Text(text = stringResource(R.string.family_member_permissions_confirm))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyInviteShareBottomSheet(
    inviteCode: String,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    FamilyModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = stringResource(R.string.family_invite_share_title),
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = stringResource(R.string.family_invite_share_link_format, inviteCode),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(MoilOverlayDimension.SheetTitleBottomPadding))

        Row(horizontalArrangement = Arrangement.spacedBy(MoilOverlayDimension.ShareOptionSpacing)) {
            FamilyShareOption(
                labelRes = R.string.family_share_kakao,
                color = MaterialTheme.colorScheme.tertiary,
            )
            FamilyShareOption(
                labelRes = R.string.family_share_message,
                color = LocalMoilExtraColors.current.memberCyan,
            )
            FamilyShareOption(
                labelRes = R.string.family_share_copy_link,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Composable
fun FamilyAdministratorTransferDialog(
    members: List<FamilyMemberUiModel>,
    onDismissRequest: () -> Unit,
    onConfirmClick: (FamilyMemberUiModel) -> Unit,
) {
    var selectedMemberNameRes by rememberSaveable { mutableStateOf<Int?>(null) }
    val transferCandidates = members.filter { member -> member.nameRes != R.string.family_member_me }
    val selectedMember = transferCandidates.firstOrNull { member ->
        member.nameRes == selectedMemberNameRes
    }

    MoilOverlayDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.padding(MoilOverlayDimension.DialogContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.family_admin_transfer_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogDescriptionTopSpacing))

            Text(
                text = stringResource(R.string.family_admin_transfer_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogTitleBottomPadding))

            transferCandidates.forEach { member ->
                FamilyTransferCandidateRow(
                    member = member,
                    isSelected = member.nameRes == selectedMemberNameRes,
                    onClick = { selectedMemberNameRes = member.nameRes },
                )
            }

            Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogActionSpacing))

            Button(
                onClick = { selectedMember?.let(onConfirmClick) },
                enabled = selectedMember != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MoilOverlayDimension.DialogActionHeight),
                shape = RoundedCornerShape(MoilRadius.DialogButton),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = LocalMoilExtraColors.current.scheduleDivider,
                    disabledContentColor = LocalMoilExtraColors.current.scheduleMutedText,
                ),
            ) {
                Text(text = stringResource(R.string.family_admin_transfer_confirm))
            }

            Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogActionSpacing))

            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MoilOverlayDimension.DialogActionHeight),
                shape = RoundedCornerShape(MoilRadius.DialogButton),
                border = BorderStroke(
                    width = 1.dp,
                    color = LocalMoilExtraColors.current.scheduleDivider,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(text = stringResource(R.string.family_dialog_cancel))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FamilyModalBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
        shape = RoundedCornerShape(
            topStart = MoilRadius.OverlaySheet,
            topEnd = MoilRadius.OverlaySheet,
        ),
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MoilOverlayDimension.SheetHorizontalPadding,
                    top = MoilOverlayDimension.SheetTopPadding,
                    end = MoilOverlayDimension.SheetHorizontalPadding,
                    bottom = MoilOverlayDimension.SheetBottomPadding,
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(
                        width = MoilOverlayDimension.SheetDragHandleWidth,
                        height = MoilOverlayDimension.SheetDragHandleHeight,
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )

            Spacer(modifier = Modifier.height(MoilOverlayDimension.SheetTitleTopPadding))

            content()
        }
    }
}

@Composable
private fun FamilyMemberRoleRow(
    member: FamilyMemberUiModel,
    @StringRes selectedRoleRes: Int,
    onRoleClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilOverlayDimension.RoleRowHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(LocalMoilExtraColors.current.memberCyan),
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(member.nameRes),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )

        FamilyRoleButton(
            labelRes = R.string.family_member_administrator,
            isSelected = selectedRoleRes == R.string.family_member_administrator,
            onClick = { onRoleClick(R.string.family_member_administrator) },
        )

        Spacer(modifier = Modifier.width(6.dp))

        FamilyRoleButton(
            labelRes = R.string.family_member_role,
            isSelected = selectedRoleRes == R.string.family_member_role,
            onClick = { onRoleClick(R.string.family_member_role) },
        )
    }
}

@Composable
private fun FamilyRoleButton(
    @StringRes labelRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(MoilOverlayDimension.RoleButtonWidth)
            .height(MoilOverlayDimension.RoleButtonHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(MoilRadius.DialogField),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(labelRes),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun FamilyShareOption(
    @StringRes labelRes: Int,
    color: androidx.compose.ui.graphics.Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(MoilOverlayDimension.ShareOptionSize),
    ) {
        Box(
            modifier = Modifier
                .size(MoilOverlayDimension.ShareOptionSize)
                .clip(CircleShape)
                .background(color),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(labelRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun FamilyTransferCandidateRow(
    member: FamilyMemberUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilOverlayDimension.TransferCandidateHeight)
            .clip(RoundedCornerShape(MoilRadius.DialogField))
            .border(
                width = 1.dp,
                color = LocalMoilExtraColors.current.scheduleDivider,
                shape = RoundedCornerShape(MoilRadius.DialogField),
            )
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(MoilOverlayDimension.TransferCandidateAvatarSize)
                .clip(CircleShape)
                .background(transferCandidateColor(member.nameRes)),
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(member.nameRes),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        )

        Spacer(modifier = Modifier.width(10.dp))

        FamilyTransferRadioButton(isSelected = isSelected)
    }

    Spacer(modifier = Modifier.height(MoilOverlayDimension.TransferCandidateSpacing))
}

@Composable
private fun FamilyTransferRadioButton(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(MoilOverlayDimension.TransferCandidateRadioSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outline),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(MoilOverlayDimension.TransferCandidateRadioSize - 2.dp)
                .clip(CircleShape)
                .background(LocalMoilExtraColors.current.overlaySurface),
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun transferCandidateColor(@StringRes memberNameRes: Int) = when (memberNameRes) {
    R.string.family_member_jimin -> LocalMoilExtraColors.current.memberViolet
    R.string.family_member_seoyeon -> LocalMoilExtraColors.current.memberCyan
    else -> MaterialTheme.colorScheme.secondary
}
