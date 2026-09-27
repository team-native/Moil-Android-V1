package com.example.moil.feature.group.module.domain.model

import com.example.moil.feature.group.module.data.dto.MemberRoleRequestDto
import com.example.moil.feature.group.module.data.mapper.toMemberRoleRequestDtoOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GroupWireMapperTest {
    @Test
    fun `역할 wire 값을 명시적으로 매핑한다`() {
        assertEquals(GroupRole.Owner, "OWNER".toGroupRole())
        assertEquals(GroupRole.Admin, "ADMIN".toGroupRole())
        assertEquals(GroupRole.Member, "MEMBER".toGroupRole())
        assertEquals(GroupRole.Unknown, "FUTURE_ROLE".toGroupRole())
    }

    @Test
    fun `서버 응답 역할은 대소문자와 미지 값을 안전한 domain enum으로 변환한다`() {
        assertEquals(GroupRole.Admin, "admin".toGroupRole())
        assertEquals(GroupRole.Member, "MEMBER".toGroupRole())
        assertEquals(GroupRole.Unknown, "FUTURE_ROLE".toGroupRole())
    }

    @Test
    fun `권한 변경 요청에는 관리자와 일반 멤버만 변환되고 나머지는 예외 없이 null이 된다`() {
        assertEquals(MemberRoleRequestDto.Admin, GroupRole.Admin.toMemberRoleRequestDtoOrNull())
        assertEquals(MemberRoleRequestDto.Member, GroupRole.Member.toMemberRoleRequestDtoOrNull())
        assertNull(GroupRole.Owner.toMemberRoleRequestDtoOrNull())
        assertNull(GroupRole.Unknown.toMemberRoleRequestDtoOrNull())
    }

    @Test
    fun `색상 wire 값을 명시적으로 매핑한다`() {
        assertEquals(GroupColor.Sky, "SKY".toGroupColor())
        assertEquals(GroupColor.Magenta, "MAGENTA".toGroupColor())
        assertEquals(GroupColor.Unknown, "FUTURE_COLOR".toGroupColor())
        assertEquals("VIOLET", GroupColor.Violet.toWireValue())
    }
}
