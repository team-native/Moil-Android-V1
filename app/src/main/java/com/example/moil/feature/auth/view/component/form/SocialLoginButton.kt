package com.example.moil.feature.auth.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider

@Composable
internal fun SocialLoginButton(
    provider: SocialLoginProvider,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(provider.labelRes),
        )
    }
}

private val SocialLoginProvider.labelRes: Int
    get() = when (this) {
        SocialLoginProvider.Google -> R.string.auth_login_google
        SocialLoginProvider.Kakao -> R.string.auth_login_kakao
        SocialLoginProvider.Apple -> R.string.auth_login_apple
    }
