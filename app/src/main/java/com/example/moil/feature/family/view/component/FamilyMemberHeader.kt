package com.example.moil.feature.family.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun FamilyMemberHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Keep the member title centered across the full screen width.
        Text(
            text = stringResource(R.string.family_title),
            modifier = Modifier.fillMaxWidth(),
            style = LocalMoilExtraTypography.current.groupJoinTitle,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "Centered Member Header", showBackground = true, widthDp = 390)
@Composable
private fun FamilyMemberHeaderPreview() {
    MoilTheme(darkTheme = false) {
        FamilyMemberHeader()
    }
}
