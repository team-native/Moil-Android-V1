package com.example.moil.feature.profile.viewmodel

sealed interface ChangePasswordScreenEvent {
    data object BackClicked : ChangePasswordScreenEvent
    data class CurrentPasswordChanged(val password: String) : ChangePasswordScreenEvent
    data class NewPasswordChanged(val password: String) : ChangePasswordScreenEvent
    data class NewPasswordConfirmationChanged(val password: String) : ChangePasswordScreenEvent
    data object SaveClicked : ChangePasswordScreenEvent
}

sealed interface DeleteAccountScreenEvent {
    data object BackClicked : DeleteAccountScreenEvent
    data class EmailChanged(val email: String) : DeleteAccountScreenEvent
    data class PasswordChanged(val password: String) : DeleteAccountScreenEvent
    data class KeepSchedulesChanged(val shouldKeepSchedules: Boolean) : DeleteAccountScreenEvent
    data object SubmitClicked : DeleteAccountScreenEvent
}
