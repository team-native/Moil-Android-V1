package com.example.moil.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.SignInMethod
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.auth.module.domain.usecase.ChangePasswordUseCase
import com.example.moil.feature.auth.module.domain.usecase.DeleteAccountUseCase
import com.example.moil.feature.auth.module.domain.usecase.LogoutUseCase
import com.example.moil.feature.auth.module.domain.usecase.UpdateProfileNameUseCase
import com.example.moil.feature.profile.viewmodel.ProfileEditSaveError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUpdateUiState(
    val isSaving: Boolean = false,
    val saveError: ProfileEditSaveError? = null,
)

sealed interface ProfileUpdateEffect {
    data class Saved(val profile: UserProfile) : ProfileUpdateEffect
}

/** 비밀번호 변경·회원 탈퇴 요청의 진행·실패 상태다. 입력값은 `MoilMainUiState`의 화면 상태가 보관한다. */
data class AccountActionUiState(
    val isChangingPassword: Boolean = false,
    val changePasswordError: MoilError? = null,
    val isDeletingAccount: Boolean = false,
    val deleteAccountError: MoilError? = null,
)

sealed interface AccountEffect {
    /** 비밀번호 변경이 끝나 변경 화면을 닫고 완료를 안내한다. */
    data object PasswordChanged : AccountEffect
}

@HiltViewModel
class MainTabViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val updateProfileNameUseCase: UpdateProfileNameUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val currentUserProfileStore: CurrentUserProfileStore,
) : ViewModel() {
    private val mutableProfileUpdateUiState = MutableStateFlow(ProfileUpdateUiState())
    private val mutableProfileUpdateEffects = MutableSharedFlow<ProfileUpdateEffect>()

    val profileUpdateUiState: StateFlow<ProfileUpdateUiState> = mutableProfileUpdateUiState.asStateFlow()
    val profileUpdateEffects: SharedFlow<ProfileUpdateEffect> = mutableProfileUpdateEffects.asSharedFlow()
    val currentUserProfile: StateFlow<UserProfile?> = currentUserProfileStore.profile
    val signInMethod: StateFlow<SignInMethod?> = currentUserProfileStore.signInMethod

    private val mutableAccountActionUiState = MutableStateFlow(AccountActionUiState())
    private val mutableAccountEffects = MutableSharedFlow<AccountEffect>()

    val accountActionUiState: StateFlow<AccountActionUiState> = mutableAccountActionUiState.asStateFlow()
    val accountEffects: SharedFlow<AccountEffect> = mutableAccountEffects.asSharedFlow()

    /** 프로필 로그아웃 이벤트에서 서버 로그아웃 UseCase를 호출합니다. */
    fun logout() = viewModelScope.launch {
        when (logoutUseCase()) {
            is MoilResult.Success -> Unit
            is MoilResult.Failure -> Unit
        }
    }

    // 프로필 저장 버튼에서 호출되어 서버 이름 변경 결과를 화면 상태와 일회성 효과로 전달합니다.
    // 실패하면 입력값을 유지하고 오류 상태만 갱신해 사용자가 다시 저장할 수 있게 합니다.
    fun updateProfileName(name: String) = viewModelScope.launch {
        if (mutableProfileUpdateUiState.value.isSaving) {
            return@launch
        }

        mutableProfileUpdateUiState.value = ProfileUpdateUiState(isSaving = true)

        when (val result = updateProfileNameUseCase(name)) {
            is MoilResult.Success -> {
                mutableProfileUpdateUiState.value = ProfileUpdateUiState()
                mutableProfileUpdateEffects.emit(ProfileUpdateEffect.Saved(result.value))
            }

            is MoilResult.Failure -> {
                mutableProfileUpdateUiState.value = ProfileUpdateUiState(
                    saveError = ProfileEditSaveError.SaveFailed,
                )
            }
        }
    }

    /**
     * 비밀번호 변경 화면의 저장 버튼에서 호출됩니다.
     * 성공하면 [AccountEffect.PasswordChanged]로 화면을 닫고, 실패하면 서버 사유를 화면 오류로 남깁니다.
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        newPasswordConfirmation: String,
    ) = viewModelScope.launch {
        if (mutableAccountActionUiState.value.isChangingPassword) {
            return@launch
        }

        mutableAccountActionUiState.value = mutableAccountActionUiState.value.copy(
            isChangingPassword = true,
            changePasswordError = null,
        )

        when (val result = changePasswordUseCase(currentPassword, newPassword, newPasswordConfirmation)) {
            is MoilResult.Success -> {
                mutableAccountActionUiState.value = mutableAccountActionUiState.value.copy(
                    isChangingPassword = false,
                )
                mutableAccountEffects.emit(AccountEffect.PasswordChanged)
            }

            is MoilResult.Failure -> {
                mutableAccountActionUiState.value = mutableAccountActionUiState.value.copy(
                    isChangingPassword = false,
                    changePasswordError = result.error,
                )
            }
        }
    }

    /**
     * 회원 탈퇴 확인 다이얼로그에서 호출됩니다.
     * 성공하면 Repository가 세션을 종료하므로 앱이 자동으로 로그인 화면으로 돌아가고,
     * 실패하면(계정 정보 불일치 등) 로그인 상태를 유지한 채 오류만 보여줍니다.
     */
    fun deleteAccount(
        email: String,
        password: String,
        shouldKeepSchedules: Boolean,
    ) = viewModelScope.launch {
        if (mutableAccountActionUiState.value.isDeletingAccount) {
            return@launch
        }

        mutableAccountActionUiState.value = mutableAccountActionUiState.value.copy(
            isDeletingAccount = true,
            deleteAccountError = null,
        )

        when (val result = deleteAccountUseCase(email, password, shouldKeepSchedules)) {
            is MoilResult.Success -> {
                mutableAccountActionUiState.value = AccountActionUiState()
            }

            is MoilResult.Failure -> {
                mutableAccountActionUiState.value = mutableAccountActionUiState.value.copy(
                    isDeletingAccount = false,
                    deleteAccountError = result.error,
                )
            }
        }
    }

    // 계정 화면에 다시 들어오거나 입력을 고치면 이전 요청 오류를 지운다.
    fun clearAccountActionErrors() {
        mutableAccountActionUiState.value = mutableAccountActionUiState.value.copy(
            changePasswordError = null,
            deleteAccountError = null,
        )
    }

    // 이름 또는 아바타 입력이 변경되면 이전 저장 오류를 지워 다음 저장 시도를 명확하게 합니다.
    fun clearProfileSaveError() {
        if (mutableProfileUpdateUiState.value.saveError != null) {
            mutableProfileUpdateUiState.value = mutableProfileUpdateUiState.value.copy(saveError = null)
        }
    }
}
