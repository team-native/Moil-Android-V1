package com.example.moil.feature.auth.viewmodel

import com.example.moil.feature.auth.view.canCreateAccount
import com.example.moil.feature.auth.view.canLogin
import com.example.moil.feature.auth.view.isVerificationCodeValid
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpValidationTest {
    @Test
    fun canLogin_acceptsValidEmailAndPasswordWithAtLeastEightCharacters() {
        val loginUiState = LoginUiState(
            email = "user@example.com",
            password = "12345678",
        )

        assertTrue(loginUiState.canLogin())
    }

    @Test
    fun canLogin_rejectsInvalidEmail() {
        val loginUiState = LoginUiState(
            email = "invalid-email",
            password = "12345678",
        )

        assertFalse(loginUiState.canLogin())
    }

    @Test
    fun canLogin_rejectsBlankValuesAndShortPassword() {
        assertFalse(LoginUiState().canLogin())
        assertFalse(
            LoginUiState(
                email = "user@example.com",
                password = "1234567",
            ).canLogin(),
        )
    }

    @Test
    fun passwordRegex_rejectsPasswordShorterThanEightCharacters() {
        assertFalse(passwordRegex.matches("1234567"))
    }

    @Test
    fun canCreateAccount_acceptsMatchingPasswordWithAtLeastEightCharacters() {
        val signUpUiState = SignUpUiState(
            password = "12345678",
            passwordConfirmation = "12345678",
        )

        assertTrue(signUpUiState.canCreateAccount())
    }

    @Test
    fun canCreateAccount_rejectsMismatchedPasswordConfirmation() {
        val signUpUiState = SignUpUiState(
            password = "12345678",
            passwordConfirmation = "87654321",
        )

        assertFalse(signUpUiState.canCreateAccount())
    }

    @Test
    fun verificationCode_rejectsNonNumericSixCharacterCode() {
        val signUpUiState = SignUpUiState(verificationCode = "12345a")

        assertFalse(signUpUiState.isVerificationCodeValid())
    }

    @Test
    fun verificationCode_acceptsSixDigits() {
        val signUpUiState = SignUpUiState(verificationCode = "123456")

        assertTrue(signUpUiState.isVerificationCodeValid())
    }
}
