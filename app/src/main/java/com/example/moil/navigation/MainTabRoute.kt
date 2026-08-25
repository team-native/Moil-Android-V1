package com.example.moil.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.applyDialogBackdropBlur
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.feature.calendar.view.CalendarScreen
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarUiState
import com.example.moil.feature.calendar.view.ScheduleBottomSheet
import com.example.moil.feature.calendar.viewmodel.reduce
import com.example.moil.feature.calendar.viewmodel.toCalendarEvents
import com.example.moil.feature.calendar.viewmodel.toCalendarGroups
import com.example.moil.feature.calendar.viewmodel.toCalendarMembers
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.calendar.view.ScheduleDatePickerDialog
import com.example.moil.feature.calendar.view.ScheduleLocationDialog
import com.example.moil.feature.calendar.view.ScheduleTimePickerDialog
import com.example.moil.ui.theme.MoilTimePickerDimension
import com.example.moil.feature.family.view.FamilyScreen
import com.example.moil.feature.family.view.MemberScreen
import com.example.moil.feature.family.viewmodel.FamilyScreenEvent
import com.example.moil.feature.family.viewmodel.FamilyUiState
import com.example.moil.feature.family.viewmodel.toFamilyGroups
import com.example.moil.feature.family.viewmodel.toFamilyMemberRole
import com.example.moil.feature.family.view.FamilyAdministratorTransferDialog
import com.example.moil.feature.family.view.FamilyGroupNameDialog
import com.example.moil.feature.family.view.FamilyInviteShareBottomSheet
import com.example.moil.feature.family.view.FamilyMemberPermissionsBottomSheet
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.view.CreateGroupScreen
import com.example.moil.feature.group.view.JoinGroupScreen
import com.example.moil.feature.group.viewmodel.CreateGroupNameValidator
import com.example.moil.feature.group.viewmodel.CreateGroupScreenEvent
import com.example.moil.feature.group.viewmodel.CreateGroupUiState
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.feature.group.viewmodel.JoinGroupScreenEvent
import com.example.moil.feature.group.viewmodel.JoinGroupStep
import com.example.moil.feature.group.viewmodel.JoinGroupUiState
import com.example.moil.feature.group.viewmodel.groupColorForAvatar
import com.example.moil.feature.group.viewmodel.toJoinGroupProfileOptions
import com.example.moil.feature.profile.view.ProfileScreen
import com.example.moil.feature.profile.viewmodel.ProfileScreenEvent
import com.example.moil.feature.profile.view.ProfileEditScreen
import com.example.moil.feature.profile.viewmodel.ProfileEditScreenEvent
import com.example.moil.feature.profile.viewmodel.ProfileEditUiState
import com.example.moil.feature.profile.viewmodel.ProfileUiState
import com.example.moil.feature.profile.viewmodel.toProfileEditUiState
import com.example.moil.feature.profile.viewmodel.toUpdatedProfileUiState
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
    val viewModel: MainTabViewModel = hiltViewModel()
    val profileUpdateUiState by viewModel.profileUpdateUiState.collectAsStateWithLifecycle()
    val currentUserProfile by viewModel.currentUserProfile.collectAsStateWithLifecycle()
    val groupViewModel: GroupViewModel = hiltViewModel()
    val groupUiState by groupViewModel.uiState.collectAsStateWithLifecycle()
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val calendarRemoteUiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val inviteCodeLabel = stringResource(R.string.family_group_invite_code_label)
    var selectedDestination by remember { mutableStateOf(MoilNavigationDestination.Calendar) }
    var calendarUiState by remember {
        mutableStateOf(
            CalendarUiState(
                displayedMonth = YearMonth.now(),
                selectedDate = LocalDate.now(),
            ),
        )
    }
    var familyUiState by remember { mutableStateOf(FamilyUiState()) }
    var profileUiState by remember { mutableStateOf(ProfileUiState(isDarkTheme = isDarkTheme)) }
    var profileEditUiState by remember { mutableStateOf(ProfileEditUiState()) }

    LaunchedEffect(isDarkTheme) {
        profileUiState = profileUiState.copy(isDarkTheme = isDarkTheme)
    }
    var createGroupUiState by remember { mutableStateOf(CreateGroupUiState()) }
    var joinGroupUiState by remember { mutableStateOf(JoinGroupUiState()) }
    var calendarOverlay by remember { mutableStateOf<CalendarOverlay>(CalendarOverlay.None) }
    var familyOverlay by remember { mutableStateOf<FamilyOverlay>(FamilyOverlay.None) }
    var previousDestination by remember { mutableStateOf(MoilNavigationDestination.Calendar) }
    var shouldOpenServerGroup by remember { mutableStateOf(false) }

    LaunchedEffect(profileUpdateUiState) {
        profileEditUiState = profileEditUiState.copy(
            isSaving = profileUpdateUiState.isSaving,
            saveError = profileUpdateUiState.saveError,
        )
    }

    LaunchedEffect(currentUserProfile) {
        currentUserProfile?.let { profile ->
            profileUiState = profileUiState.copy(profileName = profile.name)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.profileUpdateEffects.collect { effect ->
            when (effect) {
                is ProfileUpdateEffect.Saved -> {
                    profileUiState = profileEditUiState
                        .copy(profileName = effect.profile.name)
                        .toUpdatedProfileUiState(profileUiState)
                    selectedDestination = MoilNavigationDestination.Profile
                }
            }
        }
    }

    LaunchedEffect(groupUiState.groups, groupUiState.selectedGroupId, groupUiState.members, groupUiState.isLoading, groupUiState.error) {
        calendarUiState = calendarUiState.copy(
            groups = groupUiState.groups.toCalendarGroups(),
            selectedGroupId = groupUiState.selectedGroupId,
            members = groupUiState.members.toCalendarMembers(),
            isGroupsLoading = groupUiState.isLoading,
            groupLoadError = groupUiState.error,
            events = if (groupUiState.selectedGroupId == null) emptyList() else calendarUiState.events,
        )

        familyUiState = familyUiState.copy(
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
    ) {
        calendarUiState = calendarUiState.copy(
            events = calendarRemoteUiState.events.toCalendarEvents(
                fallbackProfileColor = groupUiState.selectedGroup?.myColor
                    ?: GroupColor.Unknown,
            ),
        )
    }

    LaunchedEffect(calendarRemoteUiState.currentMonthEventCount) {
        familyUiState = familyUiState.copy(
            currentMonthEventCount = calendarRemoteUiState.currentMonthEventCount,
        )
    }

    LaunchedEffect(groupUiState.inviteVerification) {
        val inviteVerification = groupUiState.inviteVerification

        if (inviteVerification != null && selectedDestination == MoilNavigationDestination.JoinGroup) {
            val profileOptions = groupUiState.joinGroupMembers.toJoinGroupProfileOptions()

            joinGroupUiState = joinGroupUiState.copy(
                step = JoinGroupStep.ProfileSetup,
                verifiedGroupName = inviteVerification.groupName,
                verifiedMemberCount = inviteVerification.memberCount,
                usedProfiles = profileOptions.usedProfiles,
                availableProfileColors = profileOptions.availableColors,
                selectedProfileColor = profileOptions.availableColors.firstOrNull(),
            )
        }
    }

    LaunchedEffect(groupUiState.isCurrentUserNameMissing, selectedDestination) {
        if (groupUiState.isCurrentUserNameMissing && selectedDestination == MoilNavigationDestination.CreateGroup) {
            createGroupUiState = createGroupUiState.copy(
                groupNameError = com.example.moil.feature.group.viewmodel.CreateGroupNameError.MissingUserName,
            )
        }
    }

    LaunchedEffect(groupUiState.selectedGroupId, shouldOpenServerGroup) {
        if (shouldOpenServerGroup && groupUiState.selectedGroupId != null) {
            shouldOpenServerGroup = false
            createGroupUiState = CreateGroupUiState()
            joinGroupUiState = JoinGroupUiState()
            selectedDestination = MoilNavigationDestination.Calendar
        }
    }

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
            is CalendarScreenEvent.GroupSelected -> {
                calendarUiState = calendarUiState.reduce(event)
                groupViewModel.selectGroup(event.groupId)
            }
            CalendarScreenEvent.EmptyGroupJoinClicked -> {
                previousDestination = selectedDestination
                selectedDestination = MoilNavigationDestination.JoinGroup
            }
            CalendarScreenEvent.EmptyGroupCreateClicked -> {
                previousDestination = selectedDestination
                selectedDestination = MoilNavigationDestination.CreateGroup
            }
            CalendarScreenEvent.RetryGroupsClicked -> groupViewModel.loadGroups()
            CalendarScreenEvent.ScheduleStartDateClicked -> calendarOverlay = CalendarOverlay.StartDatePicker
            CalendarScreenEvent.ScheduleEndDateClicked -> calendarOverlay = CalendarOverlay.EndDatePicker
            CalendarScreenEvent.ScheduleTimeClicked -> calendarOverlay = CalendarOverlay.TimePicker
            CalendarScreenEvent.ScheduleLocationClicked -> calendarOverlay = CalendarOverlay.LocationDialog
            CalendarScreenEvent.ScheduleSheetDismissed -> {
                calendarUiState = calendarUiState.reduce(event)
                calendarOverlay = CalendarOverlay.None
            }
            CalendarScreenEvent.ScheduleSaveClicked -> {
                val selectedGroupId = calendarUiState.selectedGroupId

                if (selectedGroupId != null && calendarUiState.scheduleTitle.isNotBlank()) {
                    val scheduleStartTime = calendarUiState.scheduleTime.toString()
                    val scheduleEndTime = calendarUiState.scheduleTime.plusHours(1).toString()
                    val selectedSharedMembers = calendarUiState.members
                        .filter { member -> member.id in calendarUiState.sharedMemberIds }
                    val sharedMembers = selectedSharedMembers.ifEmpty {
                        calendarUiState.members.filter { member -> member.isCurrentUser }
                    }
                    val sharedMemberIds = sharedMembers.map { member -> member.id }
                    val eventMembers = sharedMembers
                        .map { member ->
                            EventMember(
                                userId = member.id,
                                nickname = member.name,
                                color = member.color,
                            )
                        }

                    calendarViewModel.createEvent(
                        event = GroupEvent(
                            id = 0L,
                            title = calendarUiState.scheduleTitle.trim(),
                            startDate = calendarUiState.scheduleStartDate.toString(),
                            endDate = calendarUiState.scheduleEndDate.toString(),
                            isAllDay = calendarUiState.isAllDay,
                            startTime = if (calendarUiState.isAllDay) null else scheduleStartTime,
                            endTime = if (calendarUiState.isAllDay) null else scheduleEndTime,
                            location = calendarUiState.scheduleLocation.ifBlank { null },
                            members = eventMembers,
                        ),
                        sharedMemberIds = sharedMemberIds,
                    )
                }

                calendarUiState = calendarUiState.reduce(event)
                calendarOverlay = CalendarOverlay.None
            }
            CalendarScreenEvent.PreviousMonthClicked,
            CalendarScreenEvent.NextMonthClicked -> {
                calendarUiState = calendarUiState.reduce(event)
                calendarViewModel.selectMonth(calendarUiState.displayedMonth)
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

            val isTimePickerVisible = calendarOverlay == CalendarOverlay.TimePicker
            val calendarBackdropModifier = Modifier.applyDialogBackdropBlur(
                shouldBlur = isTimePickerVisible,
                blurRadius = MoilTimePickerDimension.BackgroundBlur,
            )

            CalendarScreen(
                uiState = calendarUiState,
                onEvent = onCalendarEvent,
                modifier = calendarBackdropModifier,
            )

            if (isScheduleSheetRendered && !isTimePickerVisible) {
                ScheduleBottomSheet(
                    uiState = calendarUiState,
                    sheetState = scheduleSheetState,
                    onEvent = onCalendarEvent,
                )
            }

            if (calendarUiState.isScheduleSheetVisible) {
                when (calendarOverlay) {
                    CalendarOverlay.None -> Unit
                    CalendarOverlay.StartDatePicker -> ScheduleDatePickerDialog(
                        selectedDate = calendarUiState.scheduleStartDate,
                        onDateConfirmed = { selectedDate ->
                            onCalendarEvent(CalendarScreenEvent.ScheduleStartDateChanged(selectedDate))
                            calendarOverlay = CalendarOverlay.None
                        },
                        onDismiss = { calendarOverlay = CalendarOverlay.None },
                    )
                    CalendarOverlay.EndDatePicker -> ScheduleDatePickerDialog(
                        selectedDate = calendarUiState.scheduleEndDate,
                        minimumSelectableDate = calendarUiState.scheduleStartDate,
                        onDateConfirmed = { selectedDate ->
                            onCalendarEvent(CalendarScreenEvent.ScheduleEndDateChanged(selectedDate))
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
                        FamilyScreenEvent.EmptyGroupJoinClicked -> {
                            previousDestination = selectedDestination
                            selectedDestination = MoilNavigationDestination.JoinGroup
                        }
                        FamilyScreenEvent.EmptyGroupCreateClicked -> {
                            previousDestination = selectedDestination
                            selectedDestination = MoilNavigationDestination.CreateGroup
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
                        groupViewModel.selectGroup(event.groupId.toLong())
                        }
                        is FamilyScreenEvent.NotificationsChanged -> familyUiState = familyUiState.copy(notificationsEnabled = event.isEnabled)
                        FamilyScreenEvent.GroupNameChangeClicked -> familyOverlay = FamilyOverlay.GroupName
                        FamilyScreenEvent.MemberPermissionsClicked -> familyOverlay = FamilyOverlay.MemberPermissions
                        FamilyScreenEvent.InviteCodeCopyClicked -> {
                            familyUiState.selectedGroup?.let { selectedGroup ->
                                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboardManager.setPrimaryClip(
                                    ClipData.newPlainText(
                                        inviteCodeLabel,
                                        selectedGroup.inviteCode,
                                    ),
                                )
                            }
                        }
                        FamilyScreenEvent.InviteLinkShareClicked -> familyOverlay = FamilyOverlay.InviteShare
                        FamilyScreenEvent.AdministratorTransferClicked -> familyOverlay = FamilyOverlay.AdministratorTransfer
                    }
                },
            )

            when (familyOverlay) {
                FamilyOverlay.None -> Unit
                FamilyOverlay.GroupName -> FamilyGroupNameDialog(
                    groupName = requireNotNull(familyUiState.selectedGroup).name,
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                    onSaveClick = { updatedGroupName ->
                        groupViewModel.renameSelectedGroup(updatedGroupName.trim())
                        familyOverlay = FamilyOverlay.None
                    },
                )
                FamilyOverlay.MemberPermissions -> FamilyMemberPermissionsBottomSheet(
                    members = requireNotNull(familyUiState.selectedGroup).members,
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
                    inviteCode = requireNotNull(familyUiState.selectedGroup).inviteCode,
                    sheetState = familySheetState,
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                )
                FamilyOverlay.AdministratorTransfer -> FamilyAdministratorTransferDialog(
                    members = requireNotNull(familyUiState.selectedGroup).members,
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                    onConfirmClick = { selectedMember ->
                        groupViewModel.transferAdmin(selectedMember.id)
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
                            groupViewModel.verifyInvite(joinGroupUiState.inviteCode.trim())
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
                        is JoinGroupScreenEvent.ProfileColorSelected -> {
                            joinGroupUiState = joinGroupUiState.copy(
                                selectedProfileColor = event.color,
                            )
                        }
                        JoinGroupScreenEvent.JoinGroupConfirmed -> {
                            val selectedProfileColor = joinGroupUiState.selectedProfileColor

                            if (selectedProfileColor != null) {
                                shouldOpenServerGroup = true
                                groupViewModel.joinGroup(
                                    inviteCode = joinGroupUiState.inviteCode.trim(),
                                    nickname = joinGroupUiState.profileName.trim(),
                                    color = selectedProfileColor,
                                )
                            }
                        }
                    }
                },
            )
        }

        MoilNavigationDestination.CreateGroup -> {
            val existingGroupNames = groupUiState.groups.map { group -> group.name }

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
                                groupNameError = com.example.moil.feature.group.viewmodel.CreateGroupNameError.Duplicate,
                            )
                        } else {
                            shouldOpenServerGroup = true
                            groupViewModel.createGroup(
                                name = normalizedGroupName,
                                color = groupColorForAvatar(createGroupUiState.selectedProfileAvatarRes),
                            )
                        }
                    }
                }
            },
            )
        }

        MoilNavigationDestination.Profile -> {
            ProfileScreen(
                uiState = profileUiState,
                groups = familyUiState.groups.mapIndexed { index, group ->
                    com.example.moil.feature.profile.viewmodel.ProfileGroupUiModel(
                        id = group.id,
                        name = group.name,
                        indicator = if (index == 0) {
                            com.example.moil.feature.profile.viewmodel.ProfileGroupIndicator.Primary
                        } else {
                            com.example.moil.feature.profile.viewmodel.ProfileGroupIndicator.Secondary
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
                            groupViewModel.selectGroup(event.groupId.toLong())
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
                        ProfileScreenEvent.ProfileImageClicked -> {
                            profileEditUiState = profileUiState.toProfileEditUiState()
                            selectedDestination = MoilNavigationDestination.ProfileEdit
                        }
                        ProfileScreenEvent.LogoutClicked -> viewModel.logout()
                    }
                },
            )
        }

        MoilNavigationDestination.ProfileEdit -> {
            ProfileEditScreen(
                uiState = profileEditUiState,
                onEvent = { event ->
                    when (event) {
                        ProfileEditScreenEvent.BackClicked -> {
                            selectedDestination = MoilNavigationDestination.Profile
                        }
                        is ProfileEditScreenEvent.NameChanged -> {
                            viewModel.clearProfileSaveError()
                            profileEditUiState = profileEditUiState.copy(
                                profileName = event.profileName,
                                saveError = null,
                            )
                        }
                        is ProfileEditScreenEvent.ProfileAvatarSelected -> {
                            viewModel.clearProfileSaveError()
                            profileEditUiState = profileEditUiState.copy(
                                selectedProfileAvatarRes = event.avatarRes,
                                saveError = null,
                            )
                        }
                        ProfileEditScreenEvent.SaveClicked -> {
                            if (profileEditUiState.canSave) {
                                viewModel.updateProfileName(profileEditUiState.profileName.trim())
                            }
                        }
                    }
                },
            )
        }
    }
}

private sealed interface CalendarOverlay {
    data object None : CalendarOverlay
    data object StartDatePicker : CalendarOverlay
    data object EndDatePicker : CalendarOverlay
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
