package com.example.moil.feature.group.module.data.remote

import com.example.moil.feature.event.module.data.dto.EventRequestDto
import com.example.moil.feature.event.module.data.dto.UpdateEventRequestDto
import com.example.moil.feature.event.module.data.remote.EventApiService
import com.example.moil.feature.group.module.data.dto.CreateGroupRequestDto
import com.example.moil.feature.group.module.data.dto.JoinGroupRequestDto
import com.example.moil.feature.group.module.data.dto.MemberRoleChangeDto
import com.example.moil.feature.group.module.data.dto.MemberRoleRequestDto
import com.example.moil.feature.group.module.data.dto.NotificationRequestDto
import com.example.moil.feature.group.module.data.dto.RenameGroupRequestDto
import com.example.moil.feature.group.module.data.dto.TransferAdminRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMemberRolesRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMyGroupProfileRequestDto
import com.example.moil.feature.group.module.data.dto.VerifyInviteRequestDto
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class GroupAndEventApiServiceContractTest {
    private val mockWebServer = MockWebServer()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var groupApiService: GroupApiService
    private lateinit var eventApiService: EventApiService

    @Before
    fun setUp() {
        mockWebServer.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        groupApiService = retrofit.create(GroupApiService::class.java)
        eventApiService = retrofit.create(EventApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `그룹 endpoint는 명세의 method path body를 사용한다`() = runBlocking {
        enqueue("[]")
        groupApiService.getMyGroups()
        assertRequest("POST", "/groups/me")

        enqueue(groupSummaryJson)
        groupApiService.createGroup(CreateGroupRequestDto("우리 가족", "모일", "RED"))
        assertRequest("POST", "/groups", "\"colorId\":\"RED\"")

        enqueue("{\"groupId\":1,\"name\":\"우리 가족\",\"memberCount\":2,\"inviteCode\":\"FAM-1\"}")
        groupApiService.verifyInvite(VerifyInviteRequestDto("FAM-1"))
        assertRequest("POST", "/groups/join/verify", "\"inviteCode\":\"FAM-1\"")

        enqueue(groupJoinJson)
        val joinResponse = groupApiService.joinGroup(JoinGroupRequestDto("FAM-1", "모일", "SKY"))
        assertNull(joinResponse.body()?.data?.inviteCode)
        assertEquals("/image/joined", joinResponse.body()?.data?.myImagePath)
        assertRequest("POST", "/groups/join", "\"inviteCode\":\"FAM-1\"")

        enqueue(groupDetailJson)
        val groupDetailResponse = groupApiService.getGroup(1)
        assertEquals("RED", groupDetailResponse.body()?.data?.members?.single()?.colorId)
        assertRequest("GET", "/groups/1")

        enqueue("null")
        groupApiService.leaveGroup(1)
        assertRequest("DELETE", "/groups/1/members/me")

        enqueue("{\"groupId\":1,\"userId\":2,\"nickname\":\"모일\",\"colorId\":null,\"imagePath\":\"/image/profile\"}")
        val profileResponse = groupApiService.updateMyGroupProfile(
            groupId = 1,
            request = UpdateMyGroupProfileRequestDto("모일", "SKY"),
        )
        assertNull(profileResponse.body()?.data?.colorId)
        assertEquals("/image/profile", profileResponse.body()?.data?.imagePath)
        assertRequest("PATCH", "/groups/1/members/me", "\"colorId\":\"SKY\"")

        enqueue("[{\"userId\":2,\"nickname\":\"모일\",\"email\":\"moil@example.com\",\"role\":\"member\",\"colorId\":null,\"imagePath\":\"/image/member\",\"isMe\":true}]")
        val membersResponse = groupApiService.getMembers(1)
        assertNull(membersResponse.body()?.data?.single()?.colorId)
        assertEquals("/image/member", membersResponse.body()?.data?.single()?.imagePath)
        assertRequest("GET", "/groups/1/members")

        enqueue("{\"groupId\":1,\"notificationEnabled\":true}")
        val notificationResponse = groupApiService.updateNotification(1, NotificationRequestDto(true))
        assertEquals(true, notificationResponse.body()?.data?.notificationEnabled)
        assertRequest("PATCH", "/groups/1/notification", "\"enabled\":true")

        enqueue("null")
        groupApiService.renameGroup(1, RenameGroupRequestDto("새 이름"))
        assertRequest("PATCH", "/groups/1", "\"name\":\"새 이름\"")

        enqueue("null")
        groupApiService.updateMemberRoles(1, UpdateMemberRolesRequestDto(listOf(MemberRoleChangeDto(2, MemberRoleRequestDto.Admin))))
        assertRequest("PATCH", "/groups/1/members", "\"role\":\"admin\"")

        enqueue(eventJson)
        groupApiService.transferAdmin(1, TransferAdminRequestDto(2))
        assertRequest("POST", "/groups/1/transfer-admin", "\"targetUserId\":2")
    }

    @Test
    fun `일정 endpoint는 명세의 method path query body를 사용한다`() = runBlocking {
        enqueue("[]")
        eventApiService.getGroupEvents(1, "2026-07")
        assertRequest("GET", "/groups/1/events?month=2026-07")

        enqueue("{\"eventId\":5}")
        eventApiService.createEvent(EventRequestDto(1, "식사", "2026-07-22", "18:00", "20:00", "서울", "메모", listOf(1, 2)))
        assertRequest("POST", "/events", "\"date\":\"2026-07-22\"")

        enqueue("null")
        eventApiService.getEvent(5)
        assertRequest("GET", "/events/5")

        enqueue("null")
        eventApiService.updateEvent(5, UpdateEventRequestDto("식사", "2026-07-22", "18:00", "20:00", "서울", "메모", listOf(1)))
        assertRequest("PATCH", "/events/5", "\"memo\":\"메모\"")

        enqueue("null")
        eventApiService.deleteEvent(5)
        assertRequest("DELETE", "/events/5")
    }

    private fun enqueue(data: String) {
        mockWebServer.enqueue(MockResponse.Builder().code(200).body("{\"success\":true,\"status\":0,\"message\":\"ok\",\"data\":$data}").build())
    }

    private fun assertRequest(method: String, path: String, bodyFragment: String? = null) {
        val request = mockWebServer.takeRequest()
        assertEquals(method, request.method)
        assertEquals(path, request.target)
        if (bodyFragment != null) assertTrue(request.body?.utf8().orEmpty().contains(bodyFragment))
    }

    private companion object {
        const val groupSummaryJson = "{\"groupId\":1,\"name\":\"우리 가족\",\"inviteCode\":\"FAM-1\",\"myRole\":\"admin\"}"
        const val groupJoinJson = "{\"groupId\":1,\"name\":\"우리 가족\",\"myRole\":\"member\",\"myNickname\":\"모일\",\"myColor\":\"SKY\",\"myImagePath\":\"/image/joined\"}"
        const val groupDetailJson = "{\"groupId\":1,\"name\":\"우리 가족\",\"inviteCode\":\"FAM-1\",\"memberCount\":1,\"monthlyEventCount\":0,\"myRole\":\"admin\",\"members\":[{\"userId\":1,\"nickname\":\"모일\",\"role\":\"admin\",\"colorId\":\"RED\",\"imagePath\":null}]}"
        const val eventJson = "{\"eventId\":5,\"title\":\"식사\",\"date\":\"2026-07-22\",\"startTime\":\"18:00\",\"endTime\":\"20:00\",\"location\":\"서울\",\"memo\":\"메모\",\"members\":[]}"
    }
}
