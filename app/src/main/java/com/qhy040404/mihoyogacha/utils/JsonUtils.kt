package com.qhy040404.mihoyogacha.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.intellij.lang.annotations.Language

val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

@Language("JSON")
inline fun <reified T> T.toJson(): String? = runCatching {
    moshi.adapter(T::class.java).toJson(this)
}.getOrNull()

inline fun <reified T> String.decode(): T? {
    return runCatching {
        moshi.adapter(T::class.java).fromJson(this.trim())
    }.getOrNull()
}