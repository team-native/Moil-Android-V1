package com.example.moil.feature.family.presentation

import com.example.moil.R
import com.example.moil.core.model.GroupMemberRole
import org.junit.Assert.assertEquals
import org.junit.Test

class FamilyUiStateTest {
    @Test
    fun `family group shows member role for current user`() {
        val familyUiState = FamilyUiState()

        assertEquals(GroupMemberRole.Member, familyUiState.selectedGroupCurrentUserRole)
    }

    @Test
    fun `college group shows administrator role and its invite code`() {
        val familyUiState = FamilyUiState(
            selectedGroupId = "college",
        )

        assertEquals(GroupMemberRole.Administrator, familyUiState.selectedGroupCurrentUserRole)
        assertEquals(
            R.string.family_group_college_invite_code,
            familyUiState.selectedGroup.inviteCodeRes,
        )
    }
}
