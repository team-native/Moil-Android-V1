package com.example.moil.core.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.example.moil.ui.theme.MoilRadius

/**
 * 바텀시트 목적지의 표시 방식이다.
 *
 * - [Schedule]: 상태바까지 올라오는 일정 시트. 부분 확장 단계를 허용한다.
 * - [Overlay]: 화면 하단에 짧게 뜨는 설정용 시트. 항상 전체 확장 상태로만 표시한다.
 */
enum class MoilBottomSheetStyle {
    Schedule,
    Overlay,
}

/** [style]에 맞는 [SheetState]를 만든다. 시트를 여는 쪽이 닫힘 애니메이션까지 제어할 수 있게 상태를 분리해 노출한다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMoilBottomSheetState(style: MoilBottomSheetStyle): SheetState =
    rememberModalBottomSheetState(
        skipPartiallyExpanded = style == MoilBottomSheetStyle.Overlay,
    )

/** moil의 모든 바텀시트 오버레이가 공유하는 Material 3 컨테이너다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoilModalBottomSheet(
    style: MoilBottomSheetStyle,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val sheetCornerRadius = when (style) {
        MoilBottomSheetStyle.Schedule -> MoilRadius.ScheduleSheet
        MoilBottomSheetStyle.Overlay -> MoilRadius.OverlaySheet
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = when (style) {
            MoilBottomSheetStyle.Schedule -> MaterialTheme.colorScheme.scrim.copy(alpha = SCHEDULE_SCRIM_ALPHA)
            MoilBottomSheetStyle.Overlay -> MaterialTheme.colorScheme.scrim.copy(alpha = OVERLAY_SCRIM_ALPHA)
        },
        shape = RoundedCornerShape(
            topStart = sheetCornerRadius,
            topEnd = sheetCornerRadius,
        ),
        dragHandle = null,
        contentWindowInsets = when (style) {
            MoilBottomSheetStyle.Schedule -> {
                { WindowInsets.statusBars }
            }
            MoilBottomSheetStyle.Overlay -> {
                { BottomSheetDefaults.windowInsets }
            }
        },
    ) {
        content()
    }
}

private const val SCHEDULE_SCRIM_ALPHA = 0.32f
private const val OVERLAY_SCRIM_ALPHA = 0.45f
