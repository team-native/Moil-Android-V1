package com.example.moil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.moil.feature.calendar.viewmodel.CalendarUiState
import com.example.moil.feature.family.viewmodel.FamilyUiState
import com.example.moil.feature.group.viewmodel.CreateGroupUiState
import com.example.moil.feature.group.viewmodel.JoinGroupUiState
import com.example.moil.feature.profile.viewmodel.ProfileEditUiState
import com.example.moil.feature.profile.viewmodel.ProfileUiState
import java.time.LocalDate
import java.time.YearMonth

/**
 * 메인 플로우의 화면 상태를 모아 두는 홀더다.
 *
 * 탭 back stack 위에서 화면이 push/pop되어도 유지되어야 하고, 여러 화면이 같은 상태를 나눠 보기 때문에
 * (예: Profile 탭의 그룹 목록은 Family 상태에서 나온다) 개별 entry가 아니라 `MoilMainNavDisplay` 수준에 둔다.
 * 상태 보관만 담당하며, 상태를 채우는 연결은 [MoilMainUiStateEffects]가, back stack 조작은 [MoilMainNavigator]가 맡는다.
 */
@Stable
internal class MoilMainUiState(initialIsDarkTheme: Boolean) {
    var calendarUiState: CalendarUiState by mutableStateOf(
        CalendarUiState(
            displayedMonth = YearMonth.now(),
            selectedDate = LocalDate.now(),
        ),
    )

    var familyUiState: FamilyUiState by mutableStateOf(FamilyUiState())

    var profileUiState: ProfileUiState by mutableStateOf(
        ProfileUiState(isDarkTheme = initialIsDarkTheme),
    )

    var profileEditUiState: ProfileEditUiState by mutableStateOf(ProfileEditUiState())

    var createGroupUiState: CreateGroupUiState by mutableStateOf(CreateGroupUiState())

    var joinGroupUiState: JoinGroupUiState by mutableStateOf(JoinGroupUiState())
}

@Composable
internal fun rememberMoilMainUiState(initialIsDarkTheme: Boolean): MoilMainUiState =
    remember { MoilMainUiState(initialIsDarkTheme) }
