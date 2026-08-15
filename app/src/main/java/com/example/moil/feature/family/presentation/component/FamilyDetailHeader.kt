package com.example.moil.feature.family.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.ui.theme.MoilGroupDetailDimension
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun FamilyDetailHeader(
    groupName: String,
    onBackClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Keep the group name centered independently from the fixed back button.
        Text(
            text = groupName,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Image(
            painter = painterResource(R.drawable.common_chevron_back),
            contentDescription = stringResource(R.string.common_back),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(MoilGroupDetailDimension.HeaderIconTouchTarget)
                .clickable(onClick = onBackClick)
                .padding(
                    horizontal = (MoilGroupDetailDimension.HeaderIconTouchTarget - MoilGroupDetailDimension.HeaderIconWidth) / 2,
                    vertical = (MoilGroupDetailDimension.HeaderIconTouchTarget - MoilGroupDetailDimension.HeaderIconHeight) / 2,
                ),
            contentScale = ContentScale.Fit,
        )
    }
}

@Preview(name = "Centered Family Detail Header", showBackground = true, widthDp = 390)
@Composable
private fun FamilyDetailHeaderPreview() {
    MoilTheme(darkTheme = false) {
        FamilyDetailHeader(
            groupName = stringResource(R.string.family_member_permissions_title),
            onBackClick = {},
        )
    }
}
