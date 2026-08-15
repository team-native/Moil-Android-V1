package com.example.moil.feature.group.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
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
    fun `알 수 없는 역할은 권한 변경 요청에 사용할 수 없다`() {
        try {
            GroupRole.Unknown.toWireValue()
            fail("Unknown 역할은 wire 값으로 변환되면 안 됩니다.")
        } catch (_: IllegalStateException) {
        }
    }

    @Test
    fun `색상 wire 값을 명시적으로 매핑한다`() {
        assertEquals(GroupColor.Sky, "SKY".toGroupColor())
        assertEquals(GroupColor.Magenta, "MAGENTA".toGroupColor())
        assertEquals(GroupColor.Unknown, "FUTURE_COLOR".toGroupColor())
        assertEquals("VIOLET", GroupColor.Violet.toWireValue())
    }
}
