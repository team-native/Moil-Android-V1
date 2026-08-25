package com.example.moil.feature.auth.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R

@Composable
internal fun AuthBranding() {
    Image(
        painter = painterResource(R.drawable.common_mascot),
        contentDescription = null,
        modifier = Modifier.size(76.dp),
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = stringResource(R.string.app_name),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineMedium,
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = stringResource(R.string.auth_app_tagline),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
}
