package com.example.moil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.feature.calendar.viewmodel.CalendarEffect
import com.example.moil.feature.calendar.viewmodel.CalendarScheduleSheetMode
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.calendar.viewmodel.reduce
import com.example.moil.feature.calendar.viewmodel.toCalendarEvents
import com.example.moil.feature.calendar.viewmodel.toCalendarGroups
import com.example.moil.feature.calendar.viewmodel.toCalendarMembers
import com.example.moil.feature.calendar.viewmodel.toCalendarSchedules
import com.example.moil.feature.family.viewmodel.toFamilyGroups
import com.example.moil.feature.family.viewmodel.toFamilyMemberRole
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.viewmodel.CreateGroupNameError
import com.example.moil.feature.group.viewmodel.CreateGroupUiState
import com.example.moil.feature.group.viewmodel.GroupEffect
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.feature.group.viewmodel.JoinGroupStep
import com.example.moil.feature.group.viewmodel.JoinGroupUiState
import com.example.moil.feature.group.viewmodel.toJoinGroupProfileOptions
import com.example.moil.feature.profile.viewmodel.toUpdatedProfileUiState

/**
 * 메인 플로우에서 여러 화면이 공유하는 상태를 ViewModel 흐름과 연결한다.
 *
 * 화면을 그리지 않고 연결만 담당하며, 특정 entry가 아니라 `MoilMainNavDisplay` 수준에 놓여
 * 지금 어떤 탭·화면이 보이든 동일하게 동작한다(예: 일정 저장 완료는 어느 화면에서든 시트를 닫아야 한다).
 */
@Composable
internal fun MoilMainUiStateEffects(
    mainUiState: MoilMainUiState,
    isDarkTheme: Boolean,
    mainTabViewModel: MainTabViewModel,
    groupViewModel: GroupViewModel,
    calendarViewModel: CalendarViewModel,
    navigationState: MoilMainNavigationState,
    navigator: MoilMainNavigator,
) {
    val profileUpdateUiState by mainTabViewModel.profileUpdateUiState.collectAsStateWithLifecycle()
    val currentUserProfile by mainTabViewModel.currentUserProfile.collectAsStateWithLifecycle()
    val groupUiState by groupViewModel.uiState.collectAsStateWithLifecycle()
    val calendarRemoteUiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val isJoinGroupTabActive = navigationState.topLevelRoute == MoilMainDestination.JoinGroup
    val isCreateGroupVisible = navigator.isOnCurrentBackStack(MoilMainDestination.CreateGroup)
    val isScheduleSheetOpen = navigator.isOnAnyBackStack(MoilMainDestination.ScheduleSheet)

    // 일정 시트는 스크림 탭·스와이프·시스템 뒤로가기로도 닫히고 이 경로는 CalendarScreenEvent를 거치지 않는다.
    // back stack에서 사라진 뒤에도 달력이 딤 처리된 채로 남지 않도록 시트 관련 상태를 함께 되돌린다.
    LaunchedEffect(isScheduleSheetOpen) {
        if (!isScheduleSheetOpen && mainUiState.calendarUiState.isScheduleSheetVisible) {
            mainUiState.calendarUiState = mainUiState.calendarUiState
                .reduce(CalendarScreenEvent.ScheduleSheetDismissed)
        }
    }

    LaunchedEffect(isDarkTheme) {
        mainUiState.profileUiState = mainUiState.profileUiState.copy(isDarkTheme = isDarkTheme)
    }

    LaunchedEffect(profileUpdateUiState) {
        mainUiState.profileEditUiState = mainUiState.profileEditUiState.copy(
            isSaving = profileUpdateUiState.isSaving,
            saveError = profileUpdateUiState.saveError,
        )
    }

    LaunchedEffect(currentUserProfile) {
        currentUserProfile?.let { profile ->
            mainUiState.profileUiState = mainUiState.profileUiState.copy(profileName = profile.name)
        }
    }

    LaunchedEffect(Unit) {
        mainTabViewModel.profileUpdateEffects.collect { effect ->
            when (effect) {
                is ProfileUpdateEffect.Saved -> {
                    mainUiState.profileUiState = mainUiState.profileEditUiState
                        .copy(profileName = effect.profile.name)
                        .toUpdatedProfileUiState(mainUiState.profileUiState)

                    // 프로필 편집은 Profile 탭 back stack에 push된 화면이므로, 저장 성공 시 그 화면만 걷어내면
                    // 자연스럽게 Profile 탭 시작 화면으로 돌아간다.
                    navigator.navigateToTab(MoilMainDestination.Profile)
                    navigator.close(MoilMainDestination.ProfileEdit)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        groupViewModel.effects.collect { effect ->
            when (effect) {
                is GroupEffect.GroupOperationCompleted -> {
                    mainUiState.createGroupUiState = CreateGroupUiState()
                    mainUiState.joinGroupUiState = JoinGroupUiState()

                    // 그룹 생성 화면은 진입 직전 탭 위에 push되어 있으므로, Calendar 탭으로 옮기기 전에 먼저 걷어낸다.
                    navigator.close(MoilMainDestination.CreateGroup)
                    navigator.navigateToTab(MoilMainDestination.Calendar)
                }
            }
        }
    }

    LaunchedEffect(
        groupUiState.groups,
        groupUiState.selectedGroupId,
        groupUiState.members,
        groupUiState.isLoading,
        groupUiState.error,
    ) {
        mainUiState.calendarUiState = mainUiState.calendarUiState.copy(
            groups = groupUiState.groups.toCalendarGroups(),
            selectedGroupId = groupUiState.selectedGroupId,
            members = groupUiState.members.toCalendarMembers(),
            isGroupsLoading = groupUiState.isLoading,
            groupLoadError = groupUiState.error,
            events = if (groupUiState.selectedGroupId == null) {
                emptyList()
            } else {
                mainUiState.calendarUiState.events
            },
            schedules = if (groupUiState.selectedGroupId == null) {
                emptyList()
            } else {
                mainUiState.calendarUiState.schedules
            },
        )

        mainUiState.familyUiState = mainUiState.familyUiState.copy(
            groups = groupUiState.groups.toFamilyGroups(
                selectedGroupId = groupUiState.selectedGroupId,
                selectedGroupMembers = groupUiState.members,
            ),
            selectedGroupId = groupUiState.selectedGroupId?.toString(),
            isGroupsLoading = groupUiState.isLoading,
            hasGroupLoadError = groupUiState.error != null,
            currentUserRole = groupUiState.selectedGroup
                ?.myRole
                ?.toFamilyMemberRole()
                ?: GroupMemberRole.Member,
        )
    }

    LaunchedEffect(groupUiState.selectedGroupId) {
        groupUiState.selectedGroupId?.let { selectedGroupId ->
            calendarViewModel.selectGroup(selectedGroupId)
        }
    }

    LaunchedEffect(
        calendarRemoteUiState.events,
        groupUiState.selectedGroup?.myColor,
        groupUiState.members,
    ) {
        mainUiState.calendarUiState = mainUiState.calendarUiState.copy(
            events = calendarRemoteUiState.events.toCalendarEvents(
                fallbackProfileColor = groupUiState.selectedGroup?.myColor
                    ?: GroupColor.Unknown,
            ),
            schedules = calendarRemoteUiState.events.toCalendarSchedules(
                fallbackProfileColor = groupUiState.selectedGroup?.myColor
                    ?: GroupColor.Unknown,
                groupMembers = groupUiState.members,
            ),
        )
    }

    LaunchedEffect(Unit) {
        calendarViewModel.effects.collect { effect ->
            when (effect) {
                CalendarEffect.ScheduleCreated,
                CalendarEffect.ScheduleDeleted -> {
                    mainUiState.calendarUiState = mainUiState.calendarUiState
                        .reduce(CalendarScreenEvent.ScheduleSheetDismissed)

                    // 일정 시트를 닫으면 그 위에 열려 있던 삭제 확인 다이얼로그도 함께 제거된다.
                    navigator.close(MoilMainDestination.ScheduleSheet)
                }

                is CalendarEffect.ScheduleUpdated -> {
                    mainUiState.calendarUiState = mainUiState.calendarUiState.copy(
                        scheduleSheetMode = CalendarScheduleSheetMode.Detail,
                        selectedEventId = effect.eventId,
                    )
                    calendarViewModel.loadEvent(effect.eventId)
                }
            }
        }
    }

    LaunchedEffect(calendarRemoteUiState.currentMonthEventCount) {
        mainUiState.familyUiState = mainUiState.familyUiState.copy(
            currentMonthEventCount = calendarRemoteUiState.currentMonthEventCount,
        )
    }

    LaunchedEffect(groupUiState.inviteVerification, isJoinGroupTabActive) {
        val inviteVerification = groupUiState.inviteVerification

        if (inviteVerification != null && isJoinGroupTabActive) {
            val profileOptions = groupUiState.joinGroupMembers.toJoinGroupProfileOptions()

            mainUiState.joinGroupUiState = mainUiState.joinGroupUiState.copy(
                step = JoinGroupStep.ProfileSetup,
                verifiedGroupName = inviteVerification.groupName,
                verifiedMemberCount = inviteVerification.memberCount,
                usedProfiles = profileOptions.usedProfiles,
                availableProfileColors = profileOptions.availableColors,
                selectedProfileColor = profileOptions.availableColors.firstOrNull(),
            )
        }
    }

    LaunchedEffect(groupUiState.isCurrentUserNameMissing, isCreateGroupVisible) {
        if (groupUiState.isCurrentUserNameMissing && isCreateGroupVisible) {
            mainUiState.createGroupUiState = mainUiState.createGroupUiState.copy(
                groupNameError = CreateGroupNameError.MissingUserName,
            )
        }
    }
}
