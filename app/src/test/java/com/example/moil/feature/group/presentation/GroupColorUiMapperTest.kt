package com.example.moil.feature.group.presentation

import com.example.moil.R
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.feature.group.domain.toWireValue
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupColorUiMapperTest {
    @Test
    fun `아바타의 표시 색상을 서버 colorId에 매핑한다`() {
        assertEquals(GroupColor.Red, groupColorForAvatar(R.drawable.family_avatar_mom))
        assertEquals(GroupColor.Yellow, groupColorForAvatar(R.drawable.family_avatar_mine))
        assertEquals(GroupColor.Teal, groupColorForAvatar(R.drawable.family_avatar_member_teal))
        assertEquals(GroupColor.Green, groupColorForAvatar(R.drawable.family_avatar_member_green))
        assertEquals(GroupColor.Sky, groupColorForAvatar(R.drawable.family_avatar_member_blue))
        assertEquals(GroupColor.Violet, groupColorForAvatar(R.drawable.family_avatar_dad))
        assertEquals(GroupColor.Magenta, groupColorForAvatar(R.drawable.family_avatar_sibling))
    }

    @Test
    fun `선택한 아바타의 colorId를 서버 wire 값으로 전송한다`() {
        assertEquals("RED", groupColorForAvatar(R.drawable.family_avatar_mom).toWireValue())
        assertEquals("YELLOW", groupColorForAvatar(R.drawable.family_avatar_mine).toWireValue())
        assertEquals("TEAL", groupColorForAvatar(R.drawable.family_avatar_member_teal).toWireValue())
        assertEquals("GREEN", groupColorForAvatar(R.drawable.family_avatar_member_green).toWireValue())
        assertEquals("SKY", groupColorForAvatar(R.drawable.family_avatar_member_blue).toWireValue())
        assertEquals("VIOLET", groupColorForAvatar(R.drawable.family_avatar_dad).toWireValue())
        assertEquals("MAGENTA", groupColorForAvatar(R.drawable.family_avatar_sibling).toWireValue())
    }

    @Test
    fun `서버 colorId는 같은 색상의 아바타로 표시한다`() {
        assertEquals(R.drawable.family_avatar_mom, avatarResourceForGroupColor(GroupColor.Red))
        assertEquals(R.drawable.family_avatar_mine, avatarResourceForGroupColor(GroupColor.Yellow))
        assertEquals(R.drawable.family_avatar_member_teal, avatarResourceForGroupColor(GroupColor.Teal))
        assertEquals(R.drawable.family_avatar_member_green, avatarResourceForGroupColor(GroupColor.Green))
        assertEquals(R.drawable.family_avatar_member_blue, avatarResourceForGroupColor(GroupColor.Sky))
        assertEquals(R.drawable.family_avatar_dad, avatarResourceForGroupColor(GroupColor.Violet))
        assertEquals(R.drawable.family_avatar_sibling, avatarResourceForGroupColor(GroupColor.Magenta))
    }
}
