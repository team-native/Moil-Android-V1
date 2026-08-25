package com.example.moil.feature.profile.viewmodel

fun ProfileUiState.toProfileEditUiState(): ProfileEditUiState = ProfileEditUiState(
    profileName = profileName,
    selectedProfileAvatarRes = profileAvatarRes,
)

fun ProfileEditUiState.toUpdatedProfileUiState(currentProfile: ProfileUiState): ProfileUiState =
    currentProfile.copy(
        profileName = profileName.trim(),
        profileAvatarRes = selectedProfileAvatarRes,
    )
