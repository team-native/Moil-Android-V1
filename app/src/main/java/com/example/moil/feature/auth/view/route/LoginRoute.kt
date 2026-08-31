package com.example.moil.feature.auth.view

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.feature.auth.viewmodel.AuthEffect
import com.example.moil.feature.auth.viewmodel.AuthViewModel
import com.example.moil.feature.auth.viewmodel.LoginScreenEvent
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginFailure

@Composable
fun LoginRoute(
    initialEmail: String,
    socialLoginCallback: SocialLoginCallback? = null,
    onSocialLoginCallbackConsumed: () -> Unit = {},
    socialLoginFailure: SocialLoginFailure? = null,
    onSocialLoginFailureConsumed: () -> Unit = {},
    onNavigateToSignUp: () -> Unit,
    onLoginCompleted: () -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val loginUiState = viewModel.loginUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(initialEmail) {
        viewModel.setLoginEmail(initialEmail)
    }

    LoginScreen(
        uiState = loginUiState.value,
        onEvent = { event ->
            when (event) {
                LoginScreenEvent.SignUpClicked -> onNavigateToSignUp()
                else -> viewModel.onLoginEvent(event)
            }
        },
    )

    LaunchedEffect(socialLoginCallback) {
        socialLoginCallback?.let { callback ->
            onSocialLoginCallbackConsumed()
            viewModel.handleSocialLoginCallback(callback)
        }
    }

    LaunchedEffect(socialLoginFailure) {
        socialLoginFailure?.let { failure ->
            onSocialLoginFailureConsumed()
            viewModel.handleSocialLoginFailure(failure)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AuthEffect.LoginCompleted -> onLoginCompleted()
                is AuthEffect.OpenSocialLogin -> {
                    val authorizationUri = Uri.parse(effect.authorizationUrl)
                    runCatching {
                        CustomTabsIntent.Builder()
                            .build()
                            .launchUrl(context, authorizationUri)
                    }.getOrElse {
                        context.startActivity(Intent(Intent.ACTION_VIEW, authorizationUri))
                    }
                }
                is AuthEffect.SignUpCompleted -> Unit
            }
        }
    }
}
