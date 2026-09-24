package com.example.moil.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.example.moil.core.component.MoilBottomSheetStyle
import com.example.moil.core.component.MoilModalBottomSheet
import com.example.moil.core.component.rememberMoilBottomSheetState

/**
 * `bottomSheet()` metadata가 붙은 목적지를 Material 3 ModalBottomSheet overlay로 표시한다.
 *
 * Navigation 3(1.1.6)는 바텀시트 전용 SceneStrategy를 공식 제공하지 않아, Google 공식 샘플
 * (`AnimatedBottomSheetSceneStrategy`)의 구조를 따라 프로젝트 전용으로 구현한다.
 */
internal class MoilBottomSheetSceneStrategy : SceneStrategy<NavKey> {
    override fun SceneStrategyScope<NavKey>.calculateScene(
        entries: List<NavEntry<NavKey>>,
    ): Scene<NavKey>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val bottomSheetStyle = lastEntry.metadata.get(BottomSheetKey) ?: return null

        if (entries.size <= 1) {
            return null
        }

        return MoilBottomSheetScene(
            style = bottomSheetStyle,
            entry = lastEntry,
            previousEntries = entries.dropLast(1),
            onBack = onBack,
        )
    }

    companion object {
        /** 바텀시트 목적지를 entry metadata로 표시하기 위한 Navigation 3 키이다. */
        object BottomSheetKey : NavMetadataKey<MoilBottomSheetStyle>

        /** 특정 entry를 [style]의 ModalBottomSheet overlay로 렌더링하도록 표시한다. */
        fun bottomSheet(style: MoilBottomSheetStyle): Map<String, Any> = metadata {
            put(BottomSheetKey, style)
        }
    }
}

/** Navigation 3 back stack 수명과 바텀시트 닫힘 애니메이션을 연결하는 overlay scene이다. */
@OptIn(ExperimentalMaterial3Api::class)
private data class MoilBottomSheetScene(
    private val style: MoilBottomSheetStyle,
    private val entry: NavEntry<NavKey>,
    override val previousEntries: List<NavEntry<NavKey>>,
    private val onBack: () -> Unit,
) : OverlayScene<NavKey> {
    override val key: Any = entry.contentKey
    override val entries: List<NavEntry<NavKey>> = listOf(entry)
    override val overlaidEntries: List<NavEntry<NavKey>> = previousEntries
    private var sheetState: SheetState? = null

    override val content: @Composable () -> Unit = {
        val currentSheetState = rememberMoilBottomSheetState(style)

        DisposableEffect(currentSheetState) {
            sheetState = currentSheetState

            onDispose {
                if (sheetState === currentSheetState) {
                    sheetState = null
                }
            }
        }

        MoilModalBottomSheet(
            style = style,
            sheetState = currentSheetState,
            onDismissRequest = onBack,
        ) {
            entry.Content()
        }
    }

    /** back stack entry가 제거되기 전에 닫힘 애니메이션을 완료한다. */
    override suspend fun onRemove() {
        sheetState?.hide()
    }
}
