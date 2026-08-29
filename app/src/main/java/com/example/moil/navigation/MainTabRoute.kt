package com.example.moil.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.moil.core.network.InviteLinkFormatter
import com.example.moil.feature.calendar.view.CalendarScreen
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarEffect
import com.example.moil.feature.calendar.viewmodel.CalendarUiState
import com.example.moil.feature.calendar.viewmodel.CalendarScheduleSheetMode
import com.example.moil.feature.calendar.view.ScheduleBottomSheet
import com.example.moil.feature.calendar.viewmodel.reduce
import com.example.moil.feature.calendar.viewmodel.toCalendarEvents
import com.example.moil.feature.calendar.viewmodel.toCalendarGroups
import com.example.moil.feature.calendar.viewmodel.toCalendarMembers
import com.example.moil.feature.calendar.viewmodel.toCalendarSchedules
import com.example.moil.feature.calendar.viewmodel.toCalendarScheduleUiModel
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.calendar.view.ScheduleDatePickerDialog
import com.example.moil.feature.calendar.view.ScheduleLocationDialog
import com.example.moil.feature.calendar.view.ScheduleMemoDialog
import com.example.moil.feature.calendar.view.ScheduleTimePickerDialog
import com.example.moil.feature.calendar.view.ScheduleDeleteConfirmationDialog
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
import com.example.moil.feature.group.viewmodel.GroupEffect
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
import java.time.LocalTime
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabRoute(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    currentUserRole: GroupMemberRole,
    onCurrentUserRoleChanged: (GroupMemberRole) -> Unit,
    pendingJoinGroupId: Long? = null,
    onJoinGroupDeepLinkHandled: () -> Unit = {},
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
    val inviteShareTitle = stringResource(R.string.family_invite_share_title)
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
    var isScheduleDeleteConfirmationVisible by remember { mutableStateOf(false) }
    var familyOverlay by remember { mutableStateOf<FamilyOverlay>(FamilyOverlay.None) }
    var previousDestination by remember { mutableStateOf(MoilNavigationDestination.Calendar) }
    val createGroupImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { selectedUri ->
        selectedUri?.let { uri ->
            createGroupUiState = createGroupUiState.copy(
                selectedProfileAvatarRes = null,
                selectedProfileImageUri = uri.toString(),
            )
        }
    }
    val joinGroupImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { selectedUri ->
        selectedUri?.let { uri ->
            joinGroupUiState = joinGroupUiState.copy(
                selectedProfileColor = null,
                selectedProfileImageUri = uri.toString(),
            )
        }
    }

    LaunchedEffect(pendingJoinGroupId) {
        pendingJoinGroupId?.let { groupId ->
            previousDestination = selectedDestination
            joinGroupUiState = JoinGroupUiState(pendingGroupId = groupId)
            selectedDestination = MoilNavigationDestination.JoinGroup
            onJoinGroupDeepLinkHandled()
        }
    }

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

    LaunchedEffect(Unit) {
        groupViewModel.effects.collect { effect ->
            when (effect) {
                is GroupEffect.GroupOperationCompleted -> {
                    createGroupUiState = CreateGroupUiState()
                    joinGroupUiState = JoinGroupUiState()
                    selectedDestination = MoilNavigationDestination.Calendar
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
            schedules = if (groupUiState.selectedGroupId == null) emptyList() else calendarUiState.schedules,
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
        groupUiState.members,
    ) {
        calendarUiState = calendarUiState.copy(
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
                    calendarUiState = calendarUiState.reduce(CalendarScreenEvent.ScheduleSheetDismissed)
                    calendarOverlay = CalendarOverlay.None
                    isScheduleDeleteConfirmationVisible = false
                }

                is CalendarEffect.ScheduleUpdated -> {
                    calendarUiState = calendarUiState.copy(
                        scheduleSheetMode = CalendarScheduleSheetMode.Detail,
                        selectedEventId = effect.eventId,
                    )
                    calendarViewModel.loadEvent(effect.eventId)
                }
            }
        }
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
            CalendarScreenEvent.ScheduleCreateClicked,
            is CalendarScreenEvent.ScheduleItemClicked -> {
                calendarUiState = calendarUiState.reduce(event)
                if (event is CalendarScreenEvent.ScheduleItemClicked) {
                    calendarViewModel.loadEvent(event.eventId)
                }
            }
            CalendarScreenEvent.ScheduleDateClicked -> calendarOverlay = CalendarOverlay.DatePicker
            CalendarScreenEvent.ScheduleTimeClicked -> calendarOverlay = CalendarOverlay.TimePicker
            CalendarScreenEvent.ScheduleLocationClicked -> calendarOverlay = CalendarOverlay.LocationDialog
            CalendarScreenEvent.ScheduleMemoClicked -> calendarOverlay = CalendarOverlay.MemoDialog
            CalendarScreenEvent.ScheduleDetailEditClicked -> {
                val selectedEvent = calendarRemoteUiState.selectedEvent
                if (selectedEvent != null) {
                    calendarUiState = calendarUiState
                        .reduce(event)
                        .copy(
                            scheduleTitle = selectedEvent.title,
                            scheduleDate = LocalDate.parse(selectedEvent.date),
                            scheduleStartTime = selectedEvent.startTime?.let(LocalTime::parse),
                            scheduleEndTime = selectedEvent.endTime?.let(LocalTime::parse),
                            scheduleLocation = selectedEvent.location.orEmpty(),
                            scheduleMemo = selectedEvent.memo.orEmpty(),
                            sharedMemberIds = selectedEvent.members.map { member -> member.userId }.toSet(),
                        )
                }
            }
            CalendarScreenEvent.ScheduleDetailDeleteClicked -> {
                isScheduleDeleteConfirmationVisible = true
            }
            CalendarScreenEvent.ScheduleSheetDismissed -> {
                calendarUiState = calendarUiState.reduce(event)
                calendarOverlay = CalendarOverlay.None
                isScheduleDeleteConfirmationVisible = false
            }
            CalendarScreenEvent.ScheduleSaveClicked -> {
                val selectedGroupId = calendarUiState.selectedGroupId

                if (selectedGroupId != null && calendarUiState.scheduleTitle.isNotBlank()) {
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

                    val selectedEventId = calendarUiState.selectedEventId
                    if (calendarUiState.scheduleSheetMode == CalendarScheduleSheetMode.Edit &&
                        selectedEventId != null
                    ) {
                        calendarViewModel.updateEvent(
                            eventId = selectedEventId,
                            event = GroupEvent(
                                id = selectedEventId,
                                title = calendarUiState.scheduleTitle.trim(),
                                date = calendarUiState.scheduleDate.toString(),
                                startTime = calendarUiState.scheduleStartTime?.toString(),
                                endTime = calendarUiState.scheduleEndTime?.toString(),
                                location = calendarUiState.scheduleLocation.ifBlank { null },
                                memo = calendarUiState.scheduleMemo.ifBlank { null },
                                members = eventMembers,
                            ),
                            sharedMemberIds = sharedMemberIds,
                        )
                    } else {
                        calendarViewModel.createEvent(
                            event = GroupEvent(
                                id = 0L,
                                title = calendarUiState.scheduleTitle.trim(),
                                date = calendarUiState.scheduleDate.toString(),
                                startTime = calendarUiState.scheduleStartTime?.toString(),
                                endTime = calendarUiState.scheduleEndTime?.toString(),
                                location = calendarUiState.scheduleLocation.ifBlank { null },
                                memo = calendarUiState.scheduleMemo.ifBlank { null },
                                members = eventMembers,
                            ),
                            sharedMemberIds = sharedMemberIds,
                        )
                    }
                }
                calendarOverlay = CalendarOverlay.None
            }
            CalendarScreenEvent.ScheduleDeleteConfirmed -> {
                val selectedEventId = calendarUiState.selectedEventId
                if (selectedEventId != null) {
                    isScheduleDeleteConfirmationVisible = false
                    calendarViewModel.deleteEvent(selectedEventId)
                }
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
                    selectedSchedule = calendarRemoteUiState.selectedEvent
                        ?.let { event ->
                            runCatching {
                                event.toCalendarScheduleUiModel(
                                    fallbackProfileColor = groupUiState.selectedGroup?.myColor
                                        ?: GroupColor.Unknown,
                                    groupMembers = groupUiState.members,
                                )
                            }.getOrNull()
                        },
                    isSelectedEventLoading = calendarRemoteUiState.isSelectedEventLoading,
                    isMutationLoading = calendarRemoteUiState.isMutationLoading,
                    hasSelectedEventError = calendarRemoteUiState.selectedEventError != null,
                    hasMutationError = calendarRemoteUiState.mutationError != null,
                    onEvent = onCalendarEvent,
                )
            }

            if (calendarUiState.isScheduleSheetVisible) {
                when (calendarOverlay) {
                    CalendarOverlay.None -> Unit
                    CalendarOverlay.DatePicker -> ScheduleDatePickerDialog(
                        selectedDate = calendarUiState.scheduleDate,
                        onDateConfirmed = { selectedDate ->
                            onCalendarEvent(CalendarScreenEvent.ScheduleDateChanged(selectedDate))
                            calendarOverlay = CalendarOverlay.None
                        },
                        onDismiss = { calendarOverlay = CalendarOverlay.None },
                    )
                    CalendarOverlay.TimePicker -> ScheduleTimePickerDialog(
                        selectedTime = calendarUiState.scheduleStartTime ?: LocalTime.of(9, 0),
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
                    CalendarOverlay.MemoDialog -> ScheduleMemoDialog(
                        initialMemo = calendarUiState.scheduleMemo,
                        onMemoConfirmed = { memo ->
                            onCalendarEvent(CalendarScreenEvent.ScheduleMemoChanged(memo))
                            calendarOverlay = CalendarOverlay.None
                        },
                        onDismiss = { calendarOverlay = CalendarOverlay.None },
                    )
                }
            }

            if (isScheduleDeleteConfirmationVisible) {
                ScheduleDeleteConfirmationDialog(
                    onConfirm = {
                        onCalendarEvent(CalendarScreenEvent.ScheduleDeleteConfirmed)
                    },
                    onDismiss = {
                        isScheduleDeleteConfirmationVisible = false
                    },
                )
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
                    inviteLink = InviteLinkFormatter.create(
                        requireNotNull(familyUiState.selectedGroup).id.toLong(),
                    ),
                    sheetState = familySheetState,
                    onDismissRequest = { familyOverlay = FamilyOverlay.None },
                    onShareClick = {
                        familyUiState.selectedGroup?.let { selectedGroup ->
                            val inviteLink = InviteLinkFormatter.create(selectedGroup.id.toLong())
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, inviteLink)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, inviteShareTitle))
                        }
                    },
                    onCopyLinkClick = {
                        val inviteLink = InviteLinkFormatter.create(
                            requireNotNull(familyUiState.selectedGroup).id.toLong(),
                        )
                        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(inviteShareTitle, inviteLink),
                        )
                        familyOverlay = FamilyOverlay.None
                    },
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
                                selectedProfileImageUri = null,
                            )
                        }
                        JoinGroupScreenEvent.CustomProfileImageClicked -> joinGroupImagePicker.launch("image/*")
                        JoinGroupScreenEvent.JoinGroupConfirmed -> {
                            val selectedProfileColor = joinGroupUiState.selectedProfileColor
                            val selectedProfileImageUri = joinGroupUiState.selectedProfileImageUri

                            if (selectedProfileColor != null || selectedProfileImageUri != null) {
                                groupViewModel.joinGroup(
                                    inviteCode = joinGroupUiState.inviteCode.trim(),
                                    nickname = joinGroupUiState.profileName.trim(),
                                    color = selectedProfileColor,
                                    selectedImageUri = selectedProfileImageUri,
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
                            selectedProfileImageUri = null,
                        )
                    }
                    CreateGroupScreenEvent.CustomProfileImageClicked -> createGroupImagePicker.launch("image/*")
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
                            groupViewModel.createGroup(
                                name = normalizedGroupName,
                                color = createGroupUiState.selectedProfileAvatarRes
                                    ?.let(::groupColorForAvatar),
                                selectedImageUri = createGroupUiState.selectedProfileImageUri,
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
    data object DatePicker : CalendarOverlay
    data object TimePicker : CalendarOverlay
    data object LocationDialog : CalendarOverlay
    data object MemoDialog : CalendarOverlay
}

private sealed interface FamilyOverlay {
    data object None : FamilyOverlay
    data object GroupName : FamilyOverlay
    data object MemberPermissions : FamilyOverlay
    data object InviteShare : FamilyOverlay
    data object AdministratorTransfer : FamilyOverlay
}
