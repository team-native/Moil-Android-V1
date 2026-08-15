package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.moil.ui.theme.MoilRadius

@Composable
internal fun MoilBottomNavigationItem(
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
) {
    val label = stringResource(labelRes)

    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(MoilRadius.Event))
            .clickable(
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics { contentDescription = label }
            .padding(12.dp),
        contentScale = ContentScale.Fit,
    )
}
