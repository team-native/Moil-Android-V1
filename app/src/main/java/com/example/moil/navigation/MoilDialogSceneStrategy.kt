package com.example.moil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * `dialog()` metadata가 붙은 목적지를 이전 화면 위에 겹쳐 표시한다.
 *
 * moil의 다이얼로그 Composable(`AlertDialog`, `DatePickerDialog`, `MoilOverlayDialog`)은
 * 각자 자신의 Dialog window를 직접 생성하고, 그 window가 있어야 `ApplyDialogWindowBackgroundBlur`의
 * 배경 블러가 동작한다. 그래서 Navigation 3 기본 `DialogSceneStrategy`처럼 Dialog로 한 번 더 감싸지 않고,
 * 이 전략은 "다이얼로그 뒤 화면을 계속 구성 상태로 둔다"는 overlay 동작만 담당한다.
 */
internal class MoilDialogSceneStrategy : SceneStrategy<NavKey> {
    override fun SceneStrategyScope<NavKey>.calculateScene(
        entries: List<NavEntry<NavKey>>,
    ): Scene<NavKey>? {
        val lastEntry = entries.lastOrNull() ?: return null

        if (lastEntry.metadata.get(DialogKey) == null || entries.size <= 1) {
            return null
        }

        return MoilDialogScene(
            entry = lastEntry,
            previousEntries = entries.dropLast(1),
        )
    }

    companion object {
        /** 다이얼로그 목적지를 entry metadata로 표시하기 위한 Navigation 3 키이다. */
        object DialogKey : NavMetadataKey<Unit>

        /** 특정 entry를 자기 Dialog window를 가진 오버레이로 렌더링하도록 표시한다. */
        fun dialog(): Map<String, Any> = metadata {
            put(DialogKey, Unit)
        }
    }
}

/** 다이얼로그 entry를 그대로 그리면서 아래 화면을 유지하는 overlay scene이다. */
private data class MoilDialogScene(
    private val entry: NavEntry<NavKey>,
    override val previousEntries: List<NavEntry<NavKey>>,
) : OverlayScene<NavKey> {
    override val key: Any = entry.contentKey
    override val entries: List<NavEntry<NavKey>> = listOf(entry)
    override val overlaidEntries: List<NavEntry<NavKey>> = previousEntries

    override val content: @Composable () -> Unit = {
        entry.Content()
    }
}
