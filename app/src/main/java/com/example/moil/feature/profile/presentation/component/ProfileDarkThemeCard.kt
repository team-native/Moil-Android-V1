package com.example.moil.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
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
import com.example.moil.R
import com.example.moil.core.component.MoilSwitch
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilProfileDimension

@Composable
internal fun ProfileDarkThemeCard(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilProfileDimension.SettingRowHeight)
                .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.profile_dark_mode),
                style = MaterialTheme.typography.bodyMedium,
            )

            MoilSwitch(
                checked = isDarkTheme,
                onCheckedChange = onDarkThemeChanged,
            )
        }
    }
}
