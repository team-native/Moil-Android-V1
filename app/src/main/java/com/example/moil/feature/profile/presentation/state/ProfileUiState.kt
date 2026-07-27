package com.example.moil.feature.profile.presentation

data class ProfileUiState(
    val isDarkTheme: Boolean = false,
)

data class ProfileGroupUiModel(
    val id: String,
    val name: String,
    val indicator: ProfileGroupIndicator,
)

enum class ProfileGroupIndicator {
    Primary,
    Secondary,
}
