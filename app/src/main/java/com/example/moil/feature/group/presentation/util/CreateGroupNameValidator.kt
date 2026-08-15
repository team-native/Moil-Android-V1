package com.example.moil.feature.group.presentation

internal object CreateGroupNameValidator {
    fun isDuplicate(
        groupName: String,
        existingGroupNames: List<String>,
    ): Boolean {
        val normalizedGroupName = groupName.trim()

        return existingGroupNames.any { existingGroupName ->
            existingGroupName.equals(normalizedGroupName, ignoreCase = true)
        }
    }
}
