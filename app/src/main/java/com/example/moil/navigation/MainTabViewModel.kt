package com.example.moil.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.SessionManager
import com.example.moil.feature.auth.domain.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainTabViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {
    /** 프로필 로그아웃 이벤트에서 호출되어 서버 세션 종료 후 로컬 세션을 종료합니다. */
    fun logout() = viewModelScope.launch {
        when (logoutUseCase()) {
            is MoilResult.Success -> sessionManager.expireSession()
            is MoilResult.Failure -> Unit
        }
    }
}
