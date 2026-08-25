package com.example.moil.feature.auth.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.moil.ui.theme.MoilAuthDimension

@Composable
internal fun AuthVerificationCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
) {
    val focusRequester = remember { FocusRequester() }
    val verificationCodeInteractionSource = remember { MutableInteractionSource() }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val verificationCodeCellShape = RoundedCornerShape(12.dp)
    val errorCellIndex = value.length.coerceAtMost(verificationCodeLength - 1)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = verificationCodeInteractionSource,
                        indication = null,
                    ) {
                        focusRequester.requestFocus()
                        softwareKeyboardController?.show()
                    },
                horizontalArrangement = Arrangement.spacedBy(MoilAuthDimension.VerificationCodeCellSpacing),
            ) {
                repeat(verificationCodeLength) { index ->
                    val isErrorCell = isError && index == errorCellIndex
                    val cellBorderColor = if (isErrorCell) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                    Box(
                        modifier = Modifier
                            .width(MoilAuthDimension.VerificationCodeCellWidth)
                            .height(MoilAuthDimension.VerificationCodeCellHeight)
                            .clip(verificationCodeCellShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = MoilAuthDimension.ErrorBorderWidth,
                                color = cellBorderColor,
                                shape = verificationCodeCellShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = value.getOrNull(index)?.toString().orEmpty(),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(1.dp)
                        .alpha(0f),
                ) {
                    innerTextField()
                }
            }
        },
    )
}
