package com.example.moil.navigation

import androidx.navigation3.runtime.NavKey
import com.example.moil.core.component.MoilNavigationDestination
import kotlinx.serialization.Serializable

/** 로그인 전 인증 플로우의 Navigation 3 back stack 키다. */
@Serializable
sealed interface MoilAuthDestination : NavKey {
    @Serializable
    data object Login : MoilAuthDestination

    @Serializable
    data object SignUp : MoilAuthDestination

    /** 로그인 화면의 "비밀번호를 잊으셨나요?"에서 진입하는 비밀번호 찾기 흐름이다. */
    @Serializable
    data class PasswordReset(val email: String = "") : MoilAuthDestination
}

/**
 * 로그인 후 메인 플로우의 Navigation 3 back stack 키다.
 *
 * - 탭 목적지(Calendar/Family/JoinGroup/Profile)는 각각 독립된 back stack을 갖는 섹션이다.
 * - 나머지 목적지는 호출 시점의 활성 탭 back stack에 push되는 하위 화면 또는 오버레이다.
 */
@Serializable
sealed interface MoilMainDestination : NavKey {
    @Serializable
    data object Calendar : MoilMainDestination

    @Serializable
    data object Family : MoilMainDestination

    @Serializable
    data object JoinGroup : MoilMainDestination

    @Serializable
    data object Profile : MoilMainDestination

    /** Family 탭 back stack에 push되는 그룹 상세 화면이다. */
    @Serializable
    data class GroupDetail(val groupId: Long) : MoilMainDestination

    /** Family 탭 back stack에 push되는 그룹 안의 내 프로필 변경 화면이다. */
    @Serializable
    data object GroupProfileEdit : MoilMainDestination

    /** Profile 탭 back stack에 push되는 프로필 편집 화면이다. */
    @Serializable
    data object ProfileEdit : MoilMainDestination

    /** Profile 탭 back stack에 push되는 비밀번호 변경 화면이다. */
    @Serializable
    data object ChangePassword : MoilMainDestination

    /** Profile 탭 back stack에 push되는 회원 탈퇴 화면이다. */
    @Serializable
    data object DeleteAccount : MoilMainDestination

    /** 회원 탈퇴 최종 확인 다이얼로그다. */
    @Serializable
    data object DeleteAccountConfirmation : MoilMainDestination

    /** 진입 시점의 활성 탭 back stack에 push되는 그룹 생성 화면이다. */
    @Serializable
    data object CreateGroup : MoilMainDestination

    @Serializable
    data object ScheduleSheet : MoilMainDestination

    @Serializable
    data object ScheduleDatePicker : MoilMainDestination

    @Serializable
    data object ScheduleTimePicker : MoilMainDestination

    @Serializable
    data object ScheduleLocation : MoilMainDestination

    @Serializable
    data object ScheduleMemo : MoilMainDestination

    @Serializable
    data object ScheduleDeleteConfirmation : MoilMainDestination

    @Serializable
    data object GroupRename : MoilMainDestination

    @Serializable
    data object MemberPermissions : MoilMainDestination

    @Serializable
    data object InviteShare : MoilMainDestination

    /** 유일한 관리자가 그룹을 나가기 전에 관리자 권한을 넘길 멤버를 고르는 다이얼로그다. */
    @Serializable
    data object LeaveGroupAdministratorTransfer : MoilMainDestination

    /** 그룹 나가기(마지막 멤버면 그룹 삭제 경고) 확인 다이얼로그다. */
    @Serializable
    data object LeaveGroupConfirmation : MoilMainDestination
}

/** 하단 탭에 표시되는 최상위 목적지 4개를 탭바 표시 순서대로 제공한다. */
val moilTabDestinations: List<MoilMainDestination> = listOf(
    MoilMainDestination.Calendar,
    MoilMainDestination.Family,
    MoilMainDestination.JoinGroup,
    MoilMainDestination.Profile,
)

/** 탭바 UI가 알려온 선택 항목을 실제 탐색용 back stack 키로 바꾼다. */
fun MoilNavigationDestination.toMainDestination(): MoilMainDestination = when (this) {
    MoilNavigationDestination.Calendar -> MoilMainDestination.Calendar
    MoilNavigationDestination.Family -> MoilMainDestination.Family
    MoilNavigationDestination.JoinGroup -> MoilMainDestination.JoinGroup
    MoilNavigationDestination.Profile -> MoilMainDestination.Profile
}
