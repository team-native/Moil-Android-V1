package com.example.moil.core.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.moil.core.model.GroupMemberRole
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val memberRolePreferencesDataStoreName = "member_role_preferences"

private val Context.memberRolePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = memberRolePreferencesDataStoreName,
)

/**
 * 선택된 그룹에서 현재 사용자의 권한을 보관합니다.
 *
 * Application Context만 참조하며 UI 객체나 콜백을 보관하지 않으므로 Activity 수명과 분리되어도
 * 화면 누수가 발생하지 않습니다.
 */
class MemberRolePreferencesRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val appContext = context.applicationContext

    val currentUserRole: Flow<GroupMemberRole> = appContext.memberRolePreferencesDataStore.data.map {
            preferences ->
        preferences[currentUserRoleKey]
            ?.let(::savedRoleOrMember)
            ?: GroupMemberRole.Administrator
    }

    suspend fun setCurrentUserRole(role: GroupMemberRole) {
        appContext.memberRolePreferencesDataStore.edit { preferences ->
            preferences[currentUserRoleKey] = role.name
        }
    }

    private fun savedRoleOrMember(savedRoleName: String): GroupMemberRole =
        GroupMemberRole.entries.firstOrNull { role -> role.name == savedRoleName }
            ?: GroupMemberRole.Member

    private companion object {
        val currentUserRoleKey = stringPreferencesKey("current_user_role")
    }
}
