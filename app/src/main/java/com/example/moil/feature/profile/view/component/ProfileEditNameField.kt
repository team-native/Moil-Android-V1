package com.example.moil.feature.profile.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilTextField
import com.example.moil.ui.theme.MoilGroupCreateDimension

@Composable
internal fun ProfileEditNameField(
    profileName: String,
    onProfileNameChange: (String) -> Unit,
) {
    Column {
        ProfileEditFieldLabel()

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.HeaderTitleSpacing))

        MoilTextField(
            value = profileName,
            onValueChange = onProfileNameChange,
            placeholder = stringResource(R.string.profile_edit_name_placeholder),
        )
    }
}
