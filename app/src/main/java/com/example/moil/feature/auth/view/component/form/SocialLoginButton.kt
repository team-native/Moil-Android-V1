package com.example.moil.feature.auth.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider

@Composable
internal fun SocialLoginButton(
    provider: SocialLoginProvider,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(52.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Unspecified,
            disabledContainerColor = Color.Transparent,
        ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Image(
            painter = painterResource(provider.iconRes),
            contentDescription = stringResource(provider.labelRes),
            modifier = Modifier.size(36.dp),
        )
    }
}

private val SocialLoginProvider.labelRes: Int
    get() = when (this) {
        SocialLoginProvider.Google -> R.string.auth_login_google
        SocialLoginProvider.Kakao -> R.string.auth_login_kakao
        SocialLoginProvider.Apple -> R.string.auth_login_apple
    }

private val SocialLoginProvider.iconRes: Int
    get() = when (this) {
        SocialLoginProvider.Google -> R.drawable.google
        SocialLoginProvider.Kakao -> R.drawable.kakao
        SocialLoginProvider.Apple -> R.drawable.apple
    }
