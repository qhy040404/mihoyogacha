package com.qhy040404.mihoyogacha.utils

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import com.qhy040404.mihoyogacha.GachaApp

val cm by lazy { GachaApp.app.getSystemService<ClipboardManager>()!! }

fun Any.copyToClipBoard() = cm.setPrimaryClip(
    ClipData.newPlainText("", this.toString())
)
