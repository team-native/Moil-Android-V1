package com.example.moil.feature.auth.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * 인증번호 남은 시간과 재전송 버튼을 한 줄로 보여준다.
 *
 * 만료 1분 전부터 남은 시간을 오류색으로 강조하고, 재발송은 서버 규칙(3분)이 지나야 누를 수 있다.
 */
@Composable
internal fun AuthVerificationTimer(
    countdown: VerificationCountdown,
    isResendEnabled: Boolean,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isExpiringSoon = countdown.remainingUntilExpiration <= EXPIRING_SOON_THRESHOLD

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (countdown.isExpired) {
                stringResource(R.string.auth_verification_expired)
            } else {
                stringResource(
                    R.string.auth_verification_remaining,
                    countdown.remainingUntilExpiration.inWholeMinutes,
                    countdown.remainingUntilExpiration.inWholeSeconds % SECONDS_PER_MINUTE,
                )
            },
            color = if (countdown.isExpired || isExpiringSoon) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            style = MaterialTheme.typography.labelMedium,
        )

        if (countdown.canResend || countdown.isExpired) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minHeight = MoilAuthDimension.ActionMinTouchTarget)
                    .clickable(
                        enabled = isResendEnabled,
                        role = Role.Button,
                        onClick = onResendClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.auth_resend_verification),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        } else {
            Text(
                text = stringResource(
                    R.string.auth_resend_available_in,
                    countdown.remainingUntilResend.inWholeMinutes,
                    countdown.remainingUntilResend.inWholeSeconds % SECONDS_PER_MINUTE,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

private val EXPIRING_SOON_THRESHOLD: Duration = 1.minutes
private const val SECONDS_PER_MINUTE = 60

@Preview(showBackground = true)
@Composable
private fun AuthVerificationTimerWaitingPreview() {
    MoilTheme(darkTheme = false) {
        AuthVerificationTimer(
            countdown = VerificationCountdown(
                remainingUntilExpiration = 4.minutes + 12.seconds,
                remainingUntilResend = 2.minutes + 12.seconds,
            ),
            isResendEnabled = true,
            onResendClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthVerificationTimerExpiredPreview() {
    MoilTheme(darkTheme = true) {
        AuthVerificationTimer(
            countdown = VerificationCountdown(
                remainingUntilExpiration = Duration.ZERO,
                remainingUntilResend = Duration.ZERO,
            ),
            isResendEnabled = true,
            onResendClick = {},
        )
    }
}
