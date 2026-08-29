package com.example.moil.feature.family.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.feature.family.viewmodel.FamilyMemberUiModel
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilMemberDimension

@Composable
internal fun FamilyMemberCard(
    members: List<FamilyMemberUiModel>,
    memberRoleOverrides: Map<Long, Int>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilMemberDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Column {
            members.forEachIndexed { index, member ->
                FamilyMemberRow(
                    name = member.name,
                    roleRes = memberRoleOverrides[member.id] ?: member.roleRes,
                    profileColor = member.profileColor,
                    profileImagePath = member.profileImagePath,
                )

                if (index < members.lastIndex) {
                    HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
                }
            }
        }
    }
}
