package com.qhy040404.mihoyogacha.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object OsUtils {
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun atLeastR(): Boolean {
        return Build.VERSION.SDK_INT >= 30
    }
}
