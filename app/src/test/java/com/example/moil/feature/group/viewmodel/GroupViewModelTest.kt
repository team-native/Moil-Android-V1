package com.example.moil.feature.group.viewmodel

import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupDetail
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupMemberProfile
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.model.InviteVerification
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import com.example.moil.feature.group.module.domain.usecase.CreateGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.GetGroupMembersUseCase
import com.example.moil.feature.group.module.domain.usecase.GetGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.GetMyGroupsUseCase
import com.example.moil.feature.group.module.domain.usecase.JoinGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.LeaveGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.RenameGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.TransferAdminUseCase
import com.example.moil.feature.group.module.domain.usecase.UpdateGroupNotificationUseCase
import com.example.moil.feature.group.module.domain.usecase.UpdateMemberRolesUseCase
import com.example.moil.feature.group.module.domain.usecase.VerifyInviteUseCase
import com.example.moil.feature.image.module.domain.model.UploadedProfileImage
import com.example.moil.feature.image.module.domain.repository.ImageRepository
import com.example.moil.feature.image.module.domain.usecase.UploadProfileImageUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroupViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `유일한 관리자이고 다른 멤버가 있으면 나가기 전에 권한 양도가 필요하다`() = runTest {
        val repository = FakeGroupRepository(
            myRole = GroupRole.Admin,
            members = listOf(
                member(userId = 1L, role = GroupRole.Admin, isMe = true),
                member(userId = 2L, role = GroupRole.Member, isMe = false),
            ),
        )

        val viewModel = createViewModel(repository)

        assertEquals(GroupLeavePolicy.TransferRequired, viewModel.uiState.value.leavePolicy)
    }

    @Test
    fun `혼자 남은 그룹은 나가면 삭제된다는 경고 절차를 쓴다`() = runTest {
        val repository = FakeGroupRepository(
            myRole = GroupRole.Admin,
            members = listOf(member(userId = 1L, role = GroupRole.Admin, isMe = true)),
        )

        val viewModel = createViewModel(repository)

        assertEquals(GroupLeavePolicy.LastMember, viewModel.uiState.value.leavePolicy)
    }

    @Test
    fun `다른 관리자가 있거나 일반 멤버면 일반 나가기 확인을 쓴다`() = runTest {
        val repository = FakeGroupRepository(
            myRole = GroupRole.Member,
            members = listOf(
                member(userId = 1L, role = GroupRole.Member, isMe = true),
                member(userId = 2L, role = GroupRole.Admin, isMe = false),
            ),
        )

        val viewModel = createViewModel(repository)

        assertEquals(GroupLeavePolicy.Confirm, viewModel.uiState.value.leavePolicy)
    }

    @Test
    fun `작업 실패는 로드 오류가 되지 않고 일회성 실패 효과로만 알린다`() = runTest {
        val failure = MoilError.Server(status = 400, message = "관리자만 변경할 수 있습니다.")
        val repository = FakeGroupRepository(renameResult = MoilResult.Failure(failure))
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.renameSelectedGroup("새 이름")

        assertEquals(GroupEffect.OperationFailed(failure), effect.await())
        assertNull(viewModel.uiState.value.loadError)
        assertFalse(viewModel.uiState.value.isManagementInProgress)
    }

    @Test
    fun `나가기에 성공하면 나간 그룹을 알리고 목록을 다시 불러온다`() = runTest {
        val repository = FakeGroupRepository()
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.leaveSelectedGroup()

        assertEquals(GroupEffect.GroupLeft(groupId = GROUP_ID, groupName = GROUP_NAME), effect.await())
        assertEquals(listOf(GROUP_ID), repository.leftGroupIds)
        assertEquals(2, repository.getMyGroupsCallCount)
    }

    @Test
    fun `권한 양도가 실패하면 나가기를 요청하지 않는다`() = runTest {
        val failure = MoilError.Server(status = 400, message = "관리자 권한을 이전할 수 없는 멤버입니다.")
        val repository = FakeGroupRepository(transferResult = MoilResult.Failure(failure))
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.transferAdminAndLeaveSelectedGroup(targetUserId = 2L)

        assertEquals(GroupEffect.OperationFailed(failure), effect.await())
        assertTrue(repository.leftGroupIds.isEmpty())
    }

    @Test
    fun `권한 양도 후 나가기가 실패하면 목록을 다시 불러와 바뀐 역할을 반영한다`() = runTest {
        val failure = MoilError.Network
        val repository = FakeGroupRepository(leaveResult = MoilResult.Failure(failure))
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.transferAdminAndLeaveSelectedGroup(targetUserId = 2L)

        assertEquals(GroupEffect.OperationFailed(failure), effect.await())
        assertEquals(listOf(2L), repository.transferTargetIds)
        assertEquals(2, repository.getMyGroupsCallCount)
    }

    @Test
    fun `권한 변경은 달라진 관리자·멤버 역할만 보내고 Owner는 제외한다`() = runTest {
        val repository = FakeGroupRepository(
            members = listOf(
                member(userId = 1L, role = GroupRole.Owner, isMe = true),
                member(userId = 2L, role = GroupRole.Member, isMe = false),
                member(userId = 3L, role = GroupRole.Admin, isMe = false),
            ),
        )
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.updateSelectedMemberRoles(
            mapOf(
                1L to GroupRole.Member,
                2L to GroupRole.Admin,
                3L to GroupRole.Admin,
            ),
        )

        assertEquals(GroupEffect.MemberRolesUpdated, effect.await())
        assertEquals(listOf(mapOf(2L to GroupRole.Admin)), repository.requestedRoleChanges)
    }

    @Test
    fun `바뀐 권한이 없으면 요청 없이 완료로 처리한다`() = runTest {
        val repository = FakeGroupRepository()
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.updateSelectedMemberRoles(mapOf(1L to GroupRole.Admin))

        assertEquals(GroupEffect.MemberRolesUpdated, effect.await())
        assertTrue(repository.requestedRoleChanges.isEmpty())
    }

    @Test
    fun `알림 설정 성공은 서버가 확정한 값을 선택 그룹 상세에 반영한다`() = runTest {
        val repository = FakeGroupRepository(
            notificationEnabled = true,
            notificationUpdateResult = MoilResult.Success(false),
        )
        val viewModel = createViewModel(repository)

        assertEquals(true, viewModel.uiState.value.selectedGroupNotificationEnabled)

        viewModel.updateNotification(false)

        assertEquals(false, viewModel.uiState.value.selectedGroupNotificationEnabled)
        assertFalse(viewModel.uiState.value.isNotificationUpdating)
    }

    @Test
    fun `서버가 알림 설정값을 주지 않으면 임의 기본값 없이 null로 둔다`() = runTest {
        val viewModel = createViewModel(FakeGroupRepository(notificationEnabled = null))

        assertNull(viewModel.uiState.value.selectedGroupNotificationEnabled)
    }

    @Test
    fun `알림 설정 실패는 이전 값을 유지하고 실패 효과만 보낸다`() = runTest {
        val repository = FakeGroupRepository(
            notificationEnabled = true,
            notificationUpdateResult = MoilResult.Failure(MoilError.Network),
        )
        val viewModel = createViewModel(repository)
        val effects = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.take(1).toList() }

        viewModel.updateNotification(false)

        assertEquals(listOf(GroupEffect.OperationFailed(MoilError.Network)), effects.await())
        assertEquals(true, viewModel.uiState.value.selectedGroupNotificationEnabled)
    }

    private fun createViewModel(repository: FakeGroupRepository): GroupViewModel = GroupViewModel(
        getMyGroupsUseCase = GetMyGroupsUseCase(repository),
        createGroupUseCase = CreateGroupUseCase(repository),
        verifyInviteUseCase = VerifyInviteUseCase(repository),
        joinGroupUseCase = JoinGroupUseCase(repository),
        getGroupUseCase = GetGroupUseCase(repository),
        getGroupMembersUseCase = GetGroupMembersUseCase(repository),
        updateGroupNotificationUseCase = UpdateGroupNotificationUseCase(repository),
        renameGroupUseCase = RenameGroupUseCase(repository),
        updateMemberRolesUseCase = UpdateMemberRolesUseCase(repository),
        transferAdminUseCase = TransferAdminUseCase(repository),
        leaveGroupUseCase = LeaveGroupUseCase(repository),
        uploadProfileImageUseCase = UploadProfileImageUseCase(UnusedImageRepository),
        currentUserProfileStore = FakeCurrentUserProfileStore(),
    )

    private companion object {
        const val GROUP_ID = 7L
        const val GROUP_NAME = "우리 가족"
    }
}

private fun member(
    userId: Long,
    role: GroupRole,
    isMe: Boolean,
): GroupMember = GroupMember(
    userId = userId,
    nickname = "멤버$userId",
    email = null,
    role = role,
    color = GroupColor.Sky,
    isMe = isMe,
)

private class FakeGroupRepository(
    private val myRole: GroupRole = GroupRole.Admin,
    private val members: List<GroupMember> = listOf(
        member(userId = 1L, role = GroupRole.Admin, isMe = true),
        member(userId = 2L, role = GroupRole.Member, isMe = false),
    ),
    private val notificationEnabled: Boolean? = true,
    private val notificationUpdateResult: MoilResult<Boolean> = MoilResult.Success(true),
    private val renameResult: MoilResult<Unit> = MoilResult.Success(Unit),
    private val transferResult: MoilResult<Unit> = MoilResult.Success(Unit),
    private val leaveResult: MoilResult<Unit> = MoilResult.Success(Unit),
) : GroupRepository {
    var getMyGroupsCallCount = 0
    val leftGroupIds = mutableListOf<Long>()
    val transferTargetIds = mutableListOf<Long>()
    val requestedRoleChanges = mutableListOf<Map<Long, GroupRole>>()

    private val group = GroupSummary(
        id = 7L,
        name = "우리 가족",
        inviteCode = "FAM-7X2Q",
        myRole = myRole,
        myNickname = "나",
        myColor = GroupColor.Sky,
        memberCount = members.size,
    )

    override suspend fun getMyGroups(): MoilResult<List<GroupSummary>> {
        getMyGroupsCallCount += 1
        return MoilResult.Success(listOf(group))
    }

    override suspend fun getGroup(groupId: Long): MoilResult<GroupDetail> = MoilResult.Success(
        GroupDetail(
            id = groupId,
            name = group.name,
            inviteCode = group.inviteCode.orEmpty(),
            memberCount = members.size,
            monthlyEventCount = 0,
            myRole = myRole,
            members = members,
            notificationEnabled = notificationEnabled,
        ),
    )

    override suspend fun getMembers(groupId: Long): MoilResult<List<GroupMember>> = MoilResult.Success(members)

    override suspend fun leaveGroup(groupId: Long): MoilResult<Unit> {
        leftGroupIds += groupId
        return leaveResult
    }

    override suspend fun updateNotification(groupId: Long, enabled: Boolean): MoilResult<Boolean> =
        notificationUpdateResult

    override suspend fun renameGroup(groupId: Long, name: String): MoilResult<Unit> = renameResult

    override suspend fun updateMemberRoles(groupId: Long, roles: Map<Long, GroupRole>): MoilResult<Unit> {
        requestedRoleChanges += roles
        return MoilResult.Success(Unit)
    }

    override suspend fun transferAdmin(groupId: Long, targetUserId: Long): MoilResult<Unit> {
        transferTargetIds += targetUserId
        return transferResult
    }

    override suspend fun createGroup(
        name: String,
        nickname: String,
        color: GroupColor?,
        imagePath: String?,
    ): MoilResult<GroupSummary> = unused()

    override suspend fun verifyInvite(inviteCode: String): MoilResult<InviteVerification> = unused()

    override suspend fun joinGroup(
        inviteCode: String,
        nickname: String,
        color: GroupColor?,
        imagePath: String?,
    ): MoilResult<GroupSummary> = unused()

    override suspend fun updateMyGroupProfile(
        groupId: Long,
        nickname: String,
        color: GroupColor?,
        imagePath: String?,
    ): MoilResult<GroupMemberProfile> = unused()

    private fun unused(): Nothing = error("이 테스트에서 사용하지 않는 요청입니다.")
}

private object UnusedImageRepository : ImageRepository {
    override suspend fun uploadProfileImage(contentUri: String): MoilResult<UploadedProfileImage> =
        error("이 테스트에서 사용하지 않는 요청입니다.")
}

private class FakeCurrentUserProfileStore : CurrentUserProfileStore {
    private val mutableProfile = MutableStateFlow<UserProfile?>(null)

    override val profile: StateFlow<UserProfile?> = mutableProfile

    override fun save(profile: UserProfile) {
        mutableProfile.value = profile
    }

    override fun clear() {
        mutableProfile.value = null
    }
}
