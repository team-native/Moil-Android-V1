package com.example.moil.feature.profile.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilProfileDimension
import com.example.moil.ui.theme.MoilTheme

/** 마이페이지 계정 섹션의 비밀번호 변경 행이다. 이메일로 가입한 계정에서만 보여준다. */
@Composable
internal fun ProfileAccountCard(onChangePasswordClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilProfileDimension.SettingRowHeight)
                .clickable(
                    role = Role.Button,
                    onClick = onChangePasswordClick,
                )
                .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.profile_change_password),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(R.string.family_setting_next),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileAccountCardPreview() {
    MoilTheme(darkTheme = true) {
        ProfileAccountCard(onChangePasswordClick = {})
    }
}
