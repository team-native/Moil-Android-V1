package com.example.moil.core.component

import android.content.res.Resources
import com.example.moil.R
import com.example.moil.core.domain.MoilError

/**
 * 공통 오류를 사용자에게 보여줄 문구로 바꾼다.
 *
 * 서버 응답 메시지는 명세상 사용자용 한국어 안내이므로 그대로 쓰고,
 * HTTP 원문(예: "Bad Request")이나 내부 설정 오류 메시지는 노출하지 않고 공통 문구로 대신한다.
 */
fun MoilError.toUserMessage(resources: Resources): String = when (this) {
    is MoilError.Server -> message.ifBlank {
        resources.getString(R.string.common_error_unknown)
    }

    is MoilError.Http,
    is MoilError.Configuration,
    -> resources.getString(R.string.common_error_unknown)

    MoilError.Network -> resources.getString(R.string.common_error_network)
}
