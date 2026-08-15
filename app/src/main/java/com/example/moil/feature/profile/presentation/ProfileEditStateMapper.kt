package com.example.moil.feature.profile.presentation

fun ProfileUiState.toProfileEditUiState(): ProfileEditUiState = ProfileEditUiState(
    profileName = profileName,
    selectedProfileAvatarRes = profileAvatarRes,
)

fun ProfileEditUiState.toUpdatedProfileUiState(currentProfile: ProfileUiState): ProfileUiState =
    currentProfile.copy(
        profileName = profileName.trim(),
        profileAvatarRes = selectedProfileAvatarRes,
    )
