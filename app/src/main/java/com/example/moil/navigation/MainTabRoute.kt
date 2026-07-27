package com.example.moil.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.feature.calendar.presentation.CalendarScreen
import com.example.moil.feature.calendar.presentation.CalendarScreenEvent
import com.example.moil.feature.calendar.presentation.CalendarUiState
import com.example.moil.feature.calendar.presentation.ScheduleBottomSheet
import com.example.moil.feature.calendar.presentation.reduce
import com.example.moil.feature.calendar.presentation.component.dialog.ScheduleDatePickerDialog
import com.example.moil.feature.calendar.presentation.component.dialog.ScheduleLocationDialog
import com.example.moil.feature.calendar.presentation.component.dialog.ScheduleTimePickerDialog
import com.example.moil.feature.family.presentation.FamilyScreen
import com.example.moil.feature.family.presentation.MemberScreen
import com.example.moil.feature.family.presentation.FamilyScreenEvent
import com.example.moil.feature.family.presentation.FamilyUiState
import com.example.moil.feature.family.presentation.FamilyMemberUiModel
import com.example.moil.feature.family.presentation.GroupUiModel
import com.example.moil.feature.family.presentation.FamilyAdministratorTransferDialog
import com.example.moil.feature.family.presentation.FamilyGroupNameDialog
import com.example.moil.feature.family.presentation.FamilyInviteShareBottomSheet
import com.example.moil.feature.family.presentation.FamilyMemberPermissionsBottomSheet
import com.example.moil.feature.group.presentation.CreateGroupScreen
import com.example.moil.feature.group.presentation.CreateGroupScreenEvent
import com.example.moil.feature.group.presentation.CreateGroupUiState
import com.example.moil.feature.group.presentation.CreateGroupNameValidator
import com.example.moil.feature.group.presentation.JoinGroupScreen
import com.example.moil.feature.group.presentation.JoinGroupScreenEvent
import com.example.moil.feature.group.presentation.JoinGroupStep
import com.example.moil.feature.group.presentation.JoinGroupUiState
import com.example.moil.feature.profile.presentation.ProfileScreen
import com.example.moil.feature.profile.presentation.ProfileScreenEvent
import com.example.moil.feature.profile.presentation.ProfileUiState
import com.example.moil.core.model.GroupMemberRole
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabRoute(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    currentUserRole: GroupMemberRole,
    onCurrentUserRoleChanged: (GroupMemberRole) -> Unit,
) {
    var selectedDestination by remember { mutableStateOf(MoilNavigationDestination.Calendar) }
    var calendarUiState by remember {
        mutableStateOf(
            CalendarUiState(
                displayedMonth = YearMonth.of(2026, 7),
                selectedDate = LocalDate.of(2026, 7, 22),
            ),
        )
    }
    var familyUiState by remember {
        mutableStateOf(FamilyUiState(currentUserRole = currentUserRole))
    }

    LaunchedEffect(currentUserRole) {
        familyUiState = familyUiState.copy(currentUserRole = currentUserRole)
    }
    var profileUiState by remember { mutableStateOf(ProfileUiState(isDarkTheme = isDarkTheme)) }

    LaunchedEffect(isDarkTheme) {
        profileUiState = profileUiState.copy(isDarkTheme = isDarkTheme)
    }
    var createGroupUiState by remember { mutableStateOf(CreateGroupUiState()) }
    var joinGroupUiState by remember { mutableStateOf(JoinGroupUiState()) }
    var calendarOverlay by remember { mutableStateOf<CalendarOverlay>(CalendarOverlay.None) }
    var familyOverlay by remember { mutableStateOf<FamilyOverlay>(FamilyOverlay.None) }
    var previousDestination by remember { mutableStateOf(MoilNavigationDestination.Calendar) }
    val joinedGroupNameFormat = stringResource(R.string.group_joined_name_format)

    val onCalendarEvent: (CalendarScreenEvent) -> Unit = { event ->
        when (event) {
            is CalendarScreenEvent.DestinationClicked -> {
                if (event.destination == MoilNavigationDestination.JoinGroup) {
                    previousDestination = selectedDestination
                    selectedDestination = MoilNavigationDestination.JoinGroup
                } else {
                    selectedDestination = event.destination
                }
            }
            CalendarScreenEvent.ScheduleDateClicked -> calendarOverlay = CalendarOverlay.DatePicker
            CalendarScreenEvent.ScheduleTimeClicked -> calendarOverlay = CalendarOverlay.TimePicker
            CalendarScreenEvent.ScheduleLocationClicked -> calendarOverlay = CalendarOverlay.LocationDialog
            CalendarScreenEvent.ScheduleSheetDismissed -> {
                calendarUiState = calendarUiState.reduce(event)
                calendarOverlay = CalendarOverlay.None
            }
            else -> calendarUiState = calendarUiState.reduce(event)
        }
    }

    when (selectedDestination) {
        MoilNavigationDestination.Calendar -> {
            val scheduleSheetState = rememberModalBottomSheetState()
            var isScheduleSheetRendered by remember {
                mutableStateOf(calendarUiState.isScheduleSheetVisible)
            }

            LaunchedEffect(calendarUiState.isScheduleSheetVisible) {
                if (calendarUiState.isScheduleSheetVisible) {
                    isScheduleSheetRendered = true
                    scheduleSheetState.show()
                } else if (isScheduleSheetRendered) {
                    scheduleSheetState.hide()
                    isScheduleSheetRendered = false
                }
            }

            CalendarScreen(uiState = calendarUiState, onEvent = onCalendarEvent)

            if (isScheduleSheetRendered) {
                ScheduleBottomSheet(
                    uiState = calendarUiState,
                    sheetState = scheduleSheetState,
                    onEvent = onCalendarEvent,
                )
            }

            if (calendarUiState.isScheduleSheetVisible) {
                when (calendarOverlay) {
                    CalendarOverlay.None -> Unit
                    CalendarOverlay.DatePicker -> ScheduleDatePickerDialog(
                        selectedDate = calendarUiState.selectedDate,
                        onDateConfirmed = { selectedDate ->
                            onCalendarEvent(CalendarScreenEvent.ScheduleDateChanged(selectedDate))
                            calendarOverlay = CalendarOverlay.None
                        },
                        onDismiss = { calendarOverlay = CalendarOverlay.None },
                    )
                    CalendarOverlay.TimePicker -> ScheduleTimePickerDialog(
                        selectedTime = calendarUiState.scheduleTime,
                        onTimeConfirmed = { selectedTime ->
                            onCalendarEvent(CalendarScreenEvent.ScheduleTimeChanged(selectedTime))
                            calendarOverlay = CalendarOverlay.None
                        },
                        onDismiss = { calendarOverlay = CalendarOverlay.None },
                    )
                    CalendarOverlay.LocationDialog -> ScheduleLocationDialog(
                        initialLocation = calendarUiState.scheduleLocation,
                        onLocationConfirmed = { location ->
                            onCalendarEvent(CalendarScreenEvent.ScheduleLocationChanged(location))
                            calendarOverlay = CalendarOverlay.None
                        },
                        onDismiss = { calendarOverlay = CalendarOverlay.None },
                    )
                }
            }
        }

        MoilNavigationDestination.Family -> {
            val familySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            MemberScreen(
                uiState = familyUiState,
                onEvent = { event ->
                    when (event) {
                        FamilyScreenEvent.BackClicked -> {
                            selectedDestination = MoilNavigationDestination.Profile
                        }
                        is FamilyScreenEvent.DestinationClicked -> {
                            if (event.destination == MoilNavigationDestination.JoinGroup) {
                                previousDestination = selectedDestination
                                selectedDestination = MoilNavigationDestination.JoinGroup
                            } else {
                                selectedDestination = event.destination
                            }
                        }
                    is FamilyScreenEvent.GroupClicked -> {
                        familyUiState = familyUiState.copy(
                            selectedGroupId = event.groupId,
                            memberRoleOverrides = emptyMap(),
                            )
                        }
                        is FamilyScreenEvent.NotificationsChanged -> familyUiState = familyUiState.copy(notificationsEnabled = event.isEnabled)
                        FamilyScreenEvent.GroupNameChangeClicked -> familyOverlay = FamilyOverlay.GroupName
                        FamilyScreenEvent.MemberPermissionsClicked -> familyOverlay = FamilyOverlay.MemberPermissions
                        FamilyScreenEvent.InviteLinkShareClicked -> familyOverlay = FamilyOverlay.InviteShare
                        FamilyScreenEvent.AdministratorTransferClicked -> familyOverlay = FamilyOverlay.AdministratorTransfer
                    }
                },
            )

            when (familyOverlay) {
                FamilyOverlay.None -> Unit
                FamilyOverlay.GroupName -> FamilyGroupNameDialog(
                    groupName = familyUiState.selectedGroup.customName
                        ?: stringResource(requireNotNull(familyUiState.selectedGroup.nameRes)),
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                    onSaveClick = { updatedGroupName ->
                        val updatedGroups = familyUiState.groups.map { group ->
                            if (group.id == familyUiState.selectedGroupId) {
                                group.copy(customName = updatedGroupName.trim())
                            } else {
                                group
                            }
                        }
                        familyUiState = familyUiState.copy(
                            groups = updatedGroups,
                        )
                        familyOverlay = FamilyOverlay.None
                    },
                )
                FamilyOverlay.MemberPermissions -> FamilyMemberPermissionsBottomSheet(
                    members = familyUiState.selectedGroup.members,
                    sheetState = familySheetState,
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                    onConfirmClick = { memberRoleOverrides ->
                        familyUiState = familyUiState.copy(
                            memberRoleOverrides = memberRoleOverrides,
                        )
                        familyOverlay = FamilyOverlay.None
                    },
                )
                FamilyOverlay.InviteShare -> FamilyInviteShareBottomSheet(
                    inviteCode = familyUiState.selectedGroup.customInviteCode
                        ?: stringResource(requireNotNull(familyUiState.selectedGroup.inviteCodeRes)),
                    sheetState = familySheetState,
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                )
                FamilyOverlay.AdministratorTransfer -> FamilyAdministratorTransferDialog(
                    members = familyUiState.selectedGroup.members,
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                    onConfirmClick = { selectedMember ->
                        familyUiState = familyUiState.copy(
                            memberRoleOverrides = mapOf(
                                R.string.family_member_me to R.string.family_member_role,
                                selectedMember.nameRes to R.string.family_member_administrator,
                            ),
                            currentUserRole = GroupMemberRole.Member,
                        )
                        onCurrentUserRoleChanged(GroupMemberRole.Member)
                        familyOverlay = FamilyOverlay.None
                    },
                )
            }
        }

        MoilNavigationDestination.GroupDetail -> {
            FamilyScreen(
                uiState = familyUiState,
                onEvent = { event ->
                    when (event) {
                        FamilyScreenEvent.BackClicked -> {
                            selectedDestination = MoilNavigationDestination.Profile
                        }
                        else -> Unit
                    }
                },
            )
        }

        MoilNavigationDestination.JoinGroup -> {
            JoinGroupScreen(
                uiState = joinGroupUiState,
                onEvent = { event ->
                    when (event) {
                        is JoinGroupScreenEvent.DestinationClicked -> {
                            selectedDestination = event.destination
                        }
                        is JoinGroupScreenEvent.InviteCodeChanged -> {
                            joinGroupUiState = joinGroupUiState.copy(
                                inviteCode = event.inviteCode,
                            )
                        }
                        JoinGroupScreenEvent.InviteCodeConfirmed -> {
                            joinGroupUiState = joinGroupUiState.copy(
                                step = JoinGroupStep.ProfileSetup,
                            )
                        }
                        JoinGroupScreenEvent.ProfileSetupBackClicked -> {
                            joinGroupUiState = joinGroupUiState.copy(
                                step = JoinGroupStep.InviteCode,
                            )
                        }
                        is JoinGroupScreenEvent.ProfileNameChanged -> {
                            joinGroupUiState = joinGroupUiState.copy(
                                profileName = event.profileName,
                            )
                        }
                        is JoinGroupScreenEvent.ProfileAvatarSelected -> {
                            joinGroupUiState = joinGroupUiState.copy(
                                selectedProfileAvatarRes = event.avatarRes,
                            )
                        }
                        JoinGroupScreenEvent.JoinGroupConfirmed -> {
                            val joinedGroupNumber = familyUiState.groups.size + 1
                            val joinedGroupId = "joined-$joinedGroupNumber"
                            val joinedGroup = GroupUiModel(
                                id = joinedGroupId,
                                customName = joinedGroupNameFormat.format(joinedGroupNumber),
                                customInviteCode = joinGroupUiState.inviteCode.trim(),
                                profileColor = com.example.moil.core.model.GroupProfileColor.Cyan,
                                members = listOf(
                                    FamilyMemberUiModel(
                                        nameRes = R.string.family_member_me,
                                        avatarRes = joinGroupUiState.selectedProfileAvatarRes,
                                        roleRes = R.string.family_member_role,
                                        customName = joinGroupUiState.profileName.trim(),
                                    ),
                                ),
                            )
                            familyUiState = familyUiState.copy(
                                groups = familyUiState.groups + joinedGroup,
                                selectedGroupId = joinedGroupId,
                                memberRoleOverrides = emptyMap(),
                            )
                            onCurrentUserRoleChanged(GroupMemberRole.Member)
                            joinGroupUiState = JoinGroupUiState()
                            selectedDestination = MoilNavigationDestination.Family
                        }
                    }
                },
            )
        }

        MoilNavigationDestination.CreateGroup -> {
            val existingGroupNames = familyUiState.groups.map { group ->
                group.customName ?: stringResource(requireNotNull(group.nameRes))
            }

            CreateGroupScreen(
                uiState = createGroupUiState,
                onEvent = { event ->
                when (event) {
                    CreateGroupScreenEvent.BackClicked -> {
                        selectedDestination = previousDestination
                    }
                    is CreateGroupScreenEvent.GroupNameChanged -> {
                        createGroupUiState = createGroupUiState.copy(
                            groupName = event.groupName,
                            groupNameError = null,
                        )
                    }
                    is CreateGroupScreenEvent.ProfileAvatarSelected -> {
                        createGroupUiState = createGroupUiState.copy(
                            selectedProfileAvatarRes = event.avatarRes,
                        )
                    }
                    CreateGroupScreenEvent.CreateGroupClicked -> {
                        val normalizedGroupName = createGroupUiState.groupName.trim()
                        val isDuplicateGroupName = CreateGroupNameValidator.isDuplicate(
                            groupName = normalizedGroupName,
                            existingGroupNames = existingGroupNames,
                        )

                        if (isDuplicateGroupName) {
                            createGroupUiState = createGroupUiState.copy(
                                groupNameError = com.example.moil.feature.group.presentation.CreateGroupNameError.Duplicate,
                            )
                        } else {
                            val createdGroupNumber = familyUiState.groups.size + 1
                            val createdGroupId = "custom-$createdGroupNumber"
                            val createdGroup = GroupUiModel(
                                id = createdGroupId,
                                customName = normalizedGroupName,
                                customInviteCode = "GROUP-$createdGroupNumber",
                                profileColor = com.example.moil.core.model.GroupProfileColor.Cyan,
                                members = listOf(
                                    FamilyMemberUiModel(
                                        nameRes = R.string.family_member_me,
                                        avatarRes = createGroupUiState.selectedProfileAvatarRes,
                                        roleRes = R.string.family_member_administrator,
                                    ),
                                ),
                            )
                            familyUiState = familyUiState.copy(
                                groups = familyUiState.groups + createdGroup,
                                selectedGroupId = createdGroupId,
                                memberRoleOverrides = emptyMap(),
                            )
                            onCurrentUserRoleChanged(GroupMemberRole.Administrator)
                            createGroupUiState = CreateGroupUiState()
                            selectedDestination = MoilNavigationDestination.Family
                        }
                    }
                }
            },
            )
        }

        MoilNavigationDestination.Profile -> ProfileScreen(
            uiState = profileUiState,
            groups = familyUiState.groups.mapIndexed { index, group ->
                com.example.moil.feature.profile.presentation.ProfileGroupUiModel(
                    id = group.id,
                    name = group.customName ?: stringResource(requireNotNull(group.nameRes)),
                    indicator = if (index == 0) {
                        com.example.moil.feature.profile.presentation.ProfileGroupIndicator.Primary
                    } else {
                        com.example.moil.feature.profile.presentation.ProfileGroupIndicator.Secondary
                    },
                )
            },
            onEvent = { event ->
                when (event) {
                    is ProfileScreenEvent.DestinationClicked -> {
                        if (event.destination == MoilNavigationDestination.JoinGroup) {
                            previousDestination = selectedDestination
                            selectedDestination = MoilNavigationDestination.JoinGroup
                        } else {
                            selectedDestination = event.destination
                        }
                    }
                    is ProfileScreenEvent.GroupClicked -> {
                        familyUiState = familyUiState.copy(
                            selectedGroupId = event.groupId,
                            memberRoleOverrides = emptyMap(),
                        )
                        selectedDestination = MoilNavigationDestination.GroupDetail
                    }
                    is ProfileScreenEvent.DarkThemeChanged -> {
                        profileUiState = profileUiState.copy(isDarkTheme = event.isDarkTheme)
                        onDarkThemeChanged(event.isDarkTheme)
                    }
                    ProfileScreenEvent.CreateGroupClicked -> {
                        previousDestination = selectedDestination
                        selectedDestination = MoilNavigationDestination.CreateGroup
                    }
                }
            },
        )
    }
}

private sealed interface CalendarOverlay {
    data object None : CalendarOverlay
    data object DatePicker : CalendarOverlay
    data object TimePicker : CalendarOverlay
    data object LocationDialog : CalendarOverlay
}

private sealed interface FamilyOverlay {
    data object None : FamilyOverlay
    data object GroupName : FamilyOverlay
    data object MemberPermissions : FamilyOverlay
    data object InviteShare : FamilyOverlay
    data object AdministratorTransfer : FamilyOverlay
}
