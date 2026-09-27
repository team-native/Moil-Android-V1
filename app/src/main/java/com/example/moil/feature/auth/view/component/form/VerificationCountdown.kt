package com.example.moil.feature.auth.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.moil.feature.auth.viewmodel.VERIFICATION_CODE_EXPIRATION
import com.example.moil.feature.auth.viewmodel.VERIFICATION_CODE_RESEND_DELAY
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlinx.coroutines.delay

/** 인증번호 발송 시점을 기준으로 계산한 만료·재발송 남은 시간이다. */
internal data class VerificationCountdown(
    val remainingUntilExpiration: Duration,
    val remainingUntilResend: Duration,
) {
    val isExpired: Boolean
        get() = remainingUntilExpiration <= Duration.ZERO

    val canResend: Boolean
        get() = remainingUntilResend <= Duration.ZERO
}

/**
 * 인증번호 발송 시점([codeIssuedAt])부터 1초마다 남은 시간을 다시 계산한다.
 * 발송 시점이 바뀌면(재발송) 카운트다운을 처음부터 다시 시작한다.
 */
@Composable
internal fun rememberVerificationCountdown(
    codeIssuedAt: TimeSource.Monotonic.ValueTimeMark?,
): VerificationCountdown {
    var elapsedSinceIssued by remember(codeIssuedAt) {
        mutableStateOf(codeIssuedAt?.elapsedNow() ?: Duration.ZERO)
    }

    LaunchedEffect(codeIssuedAt) {
        if (codeIssuedAt == null) {
            return@LaunchedEffect
        }

        while (elapsedSinceIssued < VERIFICATION_CODE_EXPIRATION) {
            delay(1.seconds)
            elapsedSinceIssued = codeIssuedAt.elapsedNow()
        }
    }

    return VerificationCountdown(
        remainingUntilExpiration = (VERIFICATION_CODE_EXPIRATION - elapsedSinceIssued)
            .coerceAtLeast(Duration.ZERO),
        remainingUntilResend = (VERIFICATION_CODE_RESEND_DELAY - elapsedSinceIssued)
            .coerceAtLeast(Duration.ZERO),
    )
}
