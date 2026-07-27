package com.example.moil.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilAuthDimension

@Composable
internal fun AuthScaffold(
    title: String,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit,
    bottomContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MoilAuthDimension.ScreenHorizontalPadding)
                .navigationBarsPadding(),
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (canNavigateBack) {
                    Box(
                        modifier = Modifier.size(MoilAuthDimension.BackButtonSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.common_chevron_back),
                            contentDescription = stringResource(R.string.auth_back),
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    role = Role.Button,
                                    onClick = onBackClick,
                                ),
                        )
                    }

                    Spacer(modifier = Modifier.width(MoilAuthDimension.BackButtonTitleSpacing))
                }

                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displaySmall,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                content()
            }

            bottomContent()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
