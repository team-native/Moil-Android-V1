package com.example.moil.feature.family.viewmodel

import com.example.moil.core.model.GroupMemberRole
import org.junit.Assert.assertEquals
import org.junit.Test

class FamilyUiStateTest {
    @Test
    fun `empty state has no selected group`() {
        val familyUiState = FamilyUiState()

        assertEquals(null, familyUiState.selectedGroup)
    }

    @Test
    fun `selected server group retains the current user role`() {
        val familyUiState = FamilyUiState(
            selectedGroupId = "24",
            currentUserRole = GroupMemberRole.Administrator,
        )

        assertEquals(GroupMemberRole.Administrator, familyUiState.selectedGroupCurrentUserRole)
    }
}
