package com.example.moil.feature.auth.module.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.moil.feature.auth.module.domain.model.SignInMethod
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Application Context와 불변 프로필 데이터만 보관합니다.
 * 화면 객체·콜백·CoroutineScope를 참조하지 않아 화면 수명과 분리되어도 누수가 발생하지 않습니다.
 */
@Singleton
class DefaultCurrentUserProfileStore @Inject constructor(
    @ApplicationContext appContext: Context,
) : CurrentUserProfileStore {
    private val encryptedPreferences = EncryptedSharedPreferences.create(
        appContext,
        PREFERENCES_NAME,
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val mutableProfile = MutableStateFlow(readProfile())

    override val profile: StateFlow<UserProfile?> = mutableProfile.asStateFlow()

    private val mutableSignInMethod = MutableStateFlow(readSignInMethod())

    override val signInMethod: StateFlow<SignInMethod?> = mutableSignInMethod.asStateFlow()

    override fun save(profile: UserProfile) {
        encryptedPreferences.edit()
            .putLong(USER_ID_KEY, profile.userId)
            .putString(NAME_KEY, profile.name)
            .putString(EMAIL_KEY, profile.email)
            .apply()
        mutableProfile.value = profile
    }

    override fun saveSignInMethod(signInMethod: SignInMethod) {
        encryptedPreferences.edit()
            .putString(SIGN_IN_METHOD_KEY, signInMethod.name)
            .apply()
        mutableSignInMethod.value = signInMethod
    }

    override fun clear() {
        encryptedPreferences.edit().clear().apply()
        mutableProfile.value = null
        mutableSignInMethod.value = null
    }

    private fun readSignInMethod(): SignInMethod? {
        val storedValue = encryptedPreferences.getString(SIGN_IN_METHOD_KEY, null)

        return SignInMethod.entries.firstOrNull { signInMethod -> signInMethod.name == storedValue }
    }

    private fun readProfile(): UserProfile? {
        val userId = encryptedPreferences.getLong(USER_ID_KEY, MISSING_USER_ID)
        val name = encryptedPreferences.getString(NAME_KEY, null)
        val email = encryptedPreferences.getString(EMAIL_KEY, null)

        return if (userId == MISSING_USER_ID || name.isNullOrBlank() || email.isNullOrBlank()) {
            null
        } else {
            UserProfile(userId, name, email)
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "moil_current_user_profile"
        const val USER_ID_KEY = "user_id"
        const val NAME_KEY = "name"
        const val EMAIL_KEY = "email"
        const val SIGN_IN_METHOD_KEY = "sign_in_method"
        const val MISSING_USER_ID = -1L
    }
}
