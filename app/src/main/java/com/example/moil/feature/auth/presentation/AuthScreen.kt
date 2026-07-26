package com.example.moil.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilTheme

sealed interface LoginScreenEvent {
    data class EmailChanged(val email: String) : LoginScreenEvent
    data class PasswordChanged(val password: String) : LoginScreenEvent
    data object LoginClicked : LoginScreenEvent
    data object SignUpClicked : LoginScreenEvent
}

sealed interface SignUpScreenEvent {
    data object BackClicked : SignUpScreenEvent
    data class NameChanged(val name: String) : SignUpScreenEvent
    data class EmailChanged(val email: String) : SignUpScreenEvent
    data class VerificationCodeChanged(val code: String) : SignUpScreenEvent
    data class PasswordChanged(val password: String) : SignUpScreenEvent
    data class PasswordConfirmationChanged(val passwordConfirmation: String) : SignUpScreenEvent
    data object NextClicked : SignUpScreenEvent
    data object CreateAccountClicked : SignUpScreenEvent
    data object LoginClicked : SignUpScreenEvent
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginScreenEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MoilAuthDimension.ScreenHorizontalPadding)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AuthBranding()

            Spacer(modifier = Modifier.height(36.dp))

            AuthTextField(
                value = uiState.email,
                onValueChange = { email -> onEvent(LoginScreenEvent.EmailChanged(email)) },
                placeholder = stringResource(R.string.auth_email),
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))

            AuthTextField(
                value = uiState.password,
                onValueChange = { password -> onEvent(LoginScreenEvent.PasswordChanged(password)) },
                placeholder = stringResource(R.string.auth_password),
                isPassword = true,
            )

            Spacer(modifier = Modifier.weight(1f))

            AuthPrimaryButton(
                text = stringResource(R.string.auth_login),
                enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank(),
                onClick = { onEvent(LoginScreenEvent.LoginClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_login_prompt),
                action = stringResource(R.string.auth_signup),
                onClick = { onEvent(LoginScreenEvent.SignUpClicked) },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    when (uiState.currentStep) {
        SignUpStep.Information -> SignUpInformationContent(uiState, onEvent)
        SignUpStep.Verification -> SignUpVerificationContent(uiState, onEvent)
        SignUpStep.Password -> SignUpPasswordContent(uiState, onEvent)
    }
}

@Composable
private fun SignUpInformationContent(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    AuthScaffold(
        title = stringResource(R.string.auth_signup),
        canNavigateBack = false,
        onBackClick = { onEvent(SignUpScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_next),
                enabled = uiState.name.isNotBlank() && uiState.emailRegexMatches(),
                onClick = { onEvent(SignUpScreenEvent.NextClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_signup_prompt),
                action = stringResource(R.string.auth_login),
                onClick = { onEvent(SignUpScreenEvent.LoginClicked) },
            )
        },
    ) {
        AuthFieldLabel(text = stringResource(R.string.auth_name))
        AuthTextField(
            value = uiState.name,
            onValueChange = { name -> onEvent(SignUpScreenEvent.NameChanged(name)) },
            placeholder = stringResource(R.string.auth_name_placeholder),
        )

        Spacer(modifier = Modifier.height(18.dp))

        AuthFieldLabel(text = stringResource(R.string.auth_email))
        AuthTextField(
            value = uiState.email,
            onValueChange = { email -> onEvent(SignUpScreenEvent.EmailChanged(email)) },
            placeholder = stringResource(R.string.auth_email_placeholder),
            isError = uiState.email.isNotBlank() && !uiState.emailRegexMatches(),
            keyboardType = KeyboardType.Email,
        )
        if (uiState.email.isNotBlank() && !uiState.emailRegexMatches()) {
            AuthErrorText(text = stringResource(R.string.auth_invalid_email))
        }
    }
}

@Composable
private fun SignUpVerificationContent(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    AuthScaffold(
        title = stringResource(R.string.auth_email_verification),
        canNavigateBack = true,
        onBackClick = { onEvent(SignUpScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_next),
                enabled = true,
                onClick = { onEvent(SignUpScreenEvent.NextClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_signup_prompt),
                action = stringResource(R.string.auth_login),
                onClick = { onEvent(SignUpScreenEvent.LoginClicked) },
            )
        },
    ) {
        Text(
            text = stringResource(R.string.auth_verification_description, uiState.email),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(28.dp))

        AuthVerificationCodeField(
            value = uiState.verificationCode,
            onValueChange = { code ->
                onEvent(
                    SignUpScreenEvent.VerificationCodeChanged(
                        code.filter(Char::isDigit).take(verificationCodeLength),
                    ),
                )
            },
            isError = uiState.isVerificationValidationShown,
        )

        if (uiState.isVerificationValidationShown) {
            AuthErrorText(text = stringResource(R.string.auth_verification_code_invalid))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.auth_resend_verification),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun SignUpPasswordContent(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    val isPasswordTooShort = uiState.password.isNotEmpty() && !passwordRegex.matches(uiState.password)
    val isPasswordConfirmationInvalid = uiState.passwordConfirmation.isNotEmpty() &&
        uiState.password != uiState.passwordConfirmation

    AuthScaffold(
        title = stringResource(R.string.auth_password_setup),
        canNavigateBack = true,
        onBackClick = { onEvent(SignUpScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_create_account),
                enabled = uiState.canCreateAccount(),
                onClick = { onEvent(SignUpScreenEvent.CreateAccountClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_signup_prompt),
                action = stringResource(R.string.auth_login),
                onClick = { onEvent(SignUpScreenEvent.LoginClicked) },
            )
        },
    ) {
        AuthFieldLabel(text = stringResource(R.string.auth_password))
        AuthTextField(
            value = uiState.password,
            onValueChange = { password -> onEvent(SignUpScreenEvent.PasswordChanged(password)) },
            placeholder = stringResource(R.string.auth_password),
            isError = isPasswordTooShort,
            isPassword = true,
        )
        if (isPasswordTooShort) {
            AuthErrorText(text = stringResource(R.string.auth_password_length_error))
        }

        Spacer(modifier = Modifier.height(18.dp))

        AuthFieldLabel(text = stringResource(R.string.auth_confirm_password))
        AuthTextField(
            value = uiState.passwordConfirmation,
            onValueChange = { passwordConfirmation ->
                onEvent(SignUpScreenEvent.PasswordConfirmationChanged(passwordConfirmation))
            },
            placeholder = stringResource(R.string.auth_confirm_password_placeholder),
            isError = isPasswordConfirmationInvalid,
            isPassword = true,
        )
        if (isPasswordConfirmationInvalid) {
            AuthErrorText(text = stringResource(R.string.auth_password_mismatch_error))
        }
    }
}

@Composable
private fun AuthScaffold(
    title: String,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit,
    bottomContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MoilAuthDimension.ScreenHorizontalPadding)
                .navigationBarsPadding(),
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (canNavigateBack) {
                    Box(
                        modifier = Modifier.size(MoilAuthDimension.BackButtonSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(R.drawable.common_chevron_back),
                            contentDescription = stringResource(R.string.auth_back),
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    role = Role.Button,
                                    onClick = onBackClick,
                                ),
                        )
                    }

                    Spacer(modifier = Modifier.width(MoilAuthDimension.BackButtonTitleSpacing))
                }

                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displaySmall,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                content()
            }

            bottomContent()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AuthBranding() {
    androidx.compose.foundation.Image(
        painter = painterResource(R.drawable.common_mascot),
        contentDescription = null,
        modifier = Modifier.size(76.dp),
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = stringResource(R.string.app_name),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineMedium,
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = stringResource(R.string.auth_app_tagline),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun AuthFieldLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val fieldShape = RoundedCornerShape(MoilAuthDimension.FieldCornerRadius)
    val visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(MoilAuthDimension.FieldHeight)
            .clip(fieldShape)
            .background(MaterialTheme.colorScheme.surface)
            .then(
                if (isError) {
                    Modifier.border(
                        width = MoilAuthDimension.ErrorBorderWidth,
                        color = MaterialTheme.colorScheme.error,
                        shape = fieldShape,
                    )
                } else {
                    Modifier
                },
            ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun AuthVerificationCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
) {
    val focusRequester = remember { FocusRequester() }
    val verificationCodeInteractionSource = remember { MutableInteractionSource() }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val verificationCodeCellShape = RoundedCornerShape(12.dp)
    val errorCellIndex = value.length.coerceAtMost(verificationCodeLength - 1)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = verificationCodeInteractionSource,
                        indication = null,
                    ) {
                        focusRequester.requestFocus()
                        softwareKeyboardController?.show()
                    },
                horizontalArrangement = Arrangement.spacedBy(MoilAuthDimension.VerificationCodeCellSpacing),
            ) {
                repeat(verificationCodeLength) { index ->
                    val isErrorCell = isError && index == errorCellIndex
                    val cellBorderColor = if (isErrorCell) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                    Box(
                        modifier = Modifier
                            .width(MoilAuthDimension.VerificationCodeCellWidth)
                            .height(MoilAuthDimension.VerificationCodeCellHeight)
                            .clip(verificationCodeCellShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = MoilAuthDimension.ErrorBorderWidth,
                                color = cellBorderColor,
                                shape = verificationCodeCellShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = value.getOrNull(index)?.toString().orEmpty(),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(1.dp)
                        .alpha(0f),
                ) {
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun AuthErrorText(text: String) {
    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun AuthPrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilAuthDimension.PrimaryButtonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(MoilAuthDimension.FieldCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun AuthPrompt(
    message: String,
    action: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = action,
            modifier = Modifier.clickable(onClick = onClick),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun LoginScreenPreview() {
    MoilTheme(darkTheme = false) {
        LoginScreen(uiState = LoginUiState(), onEvent = {})
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun SignUpPasswordScreenPreview() {
    MoilTheme(darkTheme = false) {
        SignUpScreen(
            uiState = SignUpUiState(
                currentStep = SignUpStep.Password,
                password = "1234",
            ),
            onEvent = {},
        )
    }
}
