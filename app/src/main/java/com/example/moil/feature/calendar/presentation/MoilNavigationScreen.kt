package com.example.moil.feature.calendar.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilBottomNavigation
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.ui.component.CommonSwitch
import com.example.moil.ui.theme.MoilSpacing
import com.example.moil.ui.theme.MoilTheme

@Composable
fun FamilyScreen(
    onDestinationClick: (MoilNavigationDestination) -> Unit,
) {
    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.Family,
        onDestinationClick = onDestinationClick,
    ) { contentModifier ->
        Column(modifier = contentModifier) {
            Text(
                text = stringResource(R.string.family_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.family_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.height(MoilSpacing.ContentTop))

            FamilyMemberRow(
                nameRes = R.string.family_member_me,
                avatarRes = R.drawable.family_avatar_mine,
            )
            FamilyMemberRow(
                nameRes = R.string.family_member_mom,
                avatarRes = R.drawable.family_avatar_mom,
            )
            FamilyMemberRow(
                nameRes = R.string.family_member_dad,
                avatarRes = R.drawable.family_avatar_dad,
            )
            FamilyMemberRow(
                nameRes = R.string.family_member_sister,
                avatarRes = R.drawable.family_avatar_sibling,
            )
        }
    }
}

@Composable
fun AddScheduleScreen(
    onDestinationClick: (MoilNavigationDestination) -> Unit,
) {
    var scheduleTitle by remember { mutableStateOf("") }

    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.AddSchedule,
        onDestinationClick = onDestinationClick,
    ) { contentModifier ->
        Column(modifier = contentModifier) {
            Text(
                text = stringResource(R.string.add_schedule_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            Image(
                painter = painterResource(R.drawable.common_mascot),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(MoilSpacing.ContentTop))

            OutlinedTextField(
                value = scheduleTitle,
                onValueChange = { updatedTitle ->
                    scheduleTitle = updatedTitle
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.add_schedule_placeholder)) },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(24.dp))

            FormValueRow(
                label = stringResource(R.string.family_tab),
                value = stringResource(R.string.add_schedule_group),
            )
            FormValueRow(
                label = stringResource(R.string.calendar_tab),
                value = stringResource(R.string.add_schedule_date),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onDestinationClick(MoilNavigationDestination.Calendar)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(text = stringResource(R.string.add_schedule_complete))
            }
        }
    }
}

@Composable
fun ProfileScreen(
    notificationsEnabled: Boolean,
    onNotificationChange: (Boolean) -> Unit,
    onDestinationClick: (MoilNavigationDestination) -> Unit,
) {
    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.Profile,
        onDestinationClick = onDestinationClick,
    ) { contentModifier ->
        Column(modifier = contentModifier) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(MoilSpacing.ContentTop))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.family_avatar_member_blue),
                    contentDescription = stringResource(R.string.profile_name),
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.profile_name),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.profile_account),
                style = MaterialTheme.typography.bodyLarge,
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 18.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.profile_notifications),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.profile_notifications_description),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                CommonSwitch(
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationChange,
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 18.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Text(
                text = stringResource(R.string.profile_group_settings),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun MoilTabScaffold(
    selectedDestination: MoilNavigationDestination,
    onDestinationClick: (MoilNavigationDestination) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            MoilBottomNavigation(
                selectedDestination = selectedDestination,
                onDestinationClick = onDestinationClick,
            )
        },
    ) { innerPadding ->
        content(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = MoilSpacing.ScreenHorizontal,
                    vertical = MoilSpacing.HeaderTop,
                ),
        )
    }
}

@Composable
private fun FamilyMemberRow(
    @StringRes nameRes: Int,
    @DrawableRes avatarRes: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = stringResource(nameRes),
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = stringResource(nameRes),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(R.string.family_member_role),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun FormValueRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun ProfileScreenPreview() {
    MoilTheme(darkTheme = false) {
        ProfileScreen(
            notificationsEnabled = true,
            onNotificationChange = {},
            onDestinationClick = {},
        )
    }
}
