package com.example.moil.core.network

import android.net.Uri
import com.example.moil.BuildConfig

object InviteLinkFormatter {
    fun create(groupId: Long): String = Uri.parse(BuildConfig.BASE_URL)
        .buildUpon()
        .appendPath("join")
        .appendPath(groupId.toString())
        .build()
        .toString()
}
