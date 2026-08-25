package com.example.moil.feature.profile.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilProfileDimension

@Composable
internal fun ProfileLogoutButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilProfileDimension.LogoutHeight)
                .clickable(onClick = onClick)
                .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.profile_logout),
                color = LocalMoilExtraColors.current.profileLogout,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
