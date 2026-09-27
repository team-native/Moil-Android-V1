package com.example.moil.feature.profile.viewmodel

import com.example.moil.core.domain.MoilError
import com.example.moil.feature.auth.viewmodel.emailRegex
import com.example.moil.feature.auth.viewmodel.passwordRegex

/** 마이페이지 비밀번호 변경 화면의 입력·요청 상태다. */
data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val newPasswordConfirmation: String = "",
    val isSaving: Boolean = false,
    val error: MoilError? = null,
) {
    val isNewPasswordTooShort: Boolean
        get() = newPassword.isNotEmpty() && !passwordRegex.matches(newPassword)

    val isNewPasswordSameAsCurrent: Boolean
        get() = newPassword.isNotEmpty() && newPassword == currentPassword

    val isConfirmationMismatched: Boolean
        get() = newPasswordConfirmation.isNotEmpty() && newPassword != newPasswordConfirmation

    val canSave: Boolean
        get() = currentPassword.isNotEmpty() &&
            passwordRegex.matches(newPassword) &&
            !isNewPasswordSameAsCurrent &&
            newPassword == newPasswordConfirmation &&
            !isSaving
}

/**
 * 마이페이지 회원 탈퇴 화면의 입력·요청 상태다.
 *
 * [shouldKeepSchedules]는 API의 `leftData`로, 탈퇴 후에도 내가 캘린더에 적은 정보를 남길지 여부다.
 * [isSocialAccount]이면 서버 탈퇴 API가 이메일·비밀번호를 요구해 앱에서 탈퇴할 수 없으므로 안내만 보여준다.
 */
data class DeleteAccountUiState(
    val email: String = "",
    val password: String = "",
    val shouldKeepSchedules: Boolean = true,
    val isSocialAccount: Boolean = false,
    val isDeleting: Boolean = false,
    val error: MoilError? = null,
) {
    val canSubmit: Boolean
        get() = !isSocialAccount &&
            emailRegex.matches(email.trim()) &&
            password.isNotEmpty() &&
            !isDeleting
}
