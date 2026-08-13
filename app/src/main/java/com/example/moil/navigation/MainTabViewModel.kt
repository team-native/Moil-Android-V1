package com.example.moil.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.SessionManager
import com.example.moil.feature.auth.domain.LogoutUseCase
import com.example.moil.feature.auth.domain.UpdateProfileNameUseCase
import com.example.moil.feature.profile.presentation.ProfileEditSaveError
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
    data class Saved(val name: String) : ProfileUpdateEffect
}

@HiltViewModel
class MainTabViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val updateProfileNameUseCase: UpdateProfileNameUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val mutableProfileUpdateUiState = MutableStateFlow(ProfileUpdateUiState())
    private val mutableProfileUpdateEffects = MutableSharedFlow<ProfileUpdateEffect>()

    val profileUpdateUiState: StateFlow<ProfileUpdateUiState> = mutableProfileUpdateUiState.asStateFlow()
    val profileUpdateEffects: SharedFlow<ProfileUpdateEffect> = mutableProfileUpdateEffects.asSharedFlow()

    /** 프로필 로그아웃 이벤트에서 호출되어 서버 세션 종료 후 로컬 세션을 종료합니다. */
    fun logout() = viewModelScope.launch {
        when (logoutUseCase()) {
            is MoilResult.Success -> sessionManager.expireSession()
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
                sessionManager.updateUserName(result.value.name)
                mutableProfileUpdateUiState.value = ProfileUpdateUiState()
                mutableProfileUpdateEffects.emit(ProfileUpdateEffect.Saved(result.value.name))
            }

            is MoilResult.Failure -> {
                mutableProfileUpdateUiState.value = ProfileUpdateUiState(
                    saveError = ProfileEditSaveError.SaveFailed,
                )
            }
        }
    }

    // 이름 또는 아바타 입력이 변경되면 이전 저장 오류를 지워 다음 저장 시도를 명확하게 합니다.
    fun clearProfileSaveError() {
        if (mutableProfileUpdateUiState.value.saveError != null) {
            mutableProfileUpdateUiState.value = mutableProfileUpdateUiState.value.copy(saveError = null)
        }
    }
}
