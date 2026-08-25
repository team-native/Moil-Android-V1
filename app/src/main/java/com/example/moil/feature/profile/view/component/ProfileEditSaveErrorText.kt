package com.example.moil.feature.profile.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.feature.profile.viewmodel.ProfileEditSaveError

/** 프로필 저장 실패 상태를 사용자가 재시도할 수 있도록 고정 문구로 안내합니다. */
@Composable
internal fun ProfileEditSaveErrorText(error: ProfileEditSaveError) {
    val messageResId = when (error) {
        ProfileEditSaveError.SaveFailed -> R.string.profile_edit_save_error
    }

    Text(
        text = stringResource(messageResId),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
    )
}
