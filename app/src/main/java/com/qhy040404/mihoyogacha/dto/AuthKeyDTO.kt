package com.qhy040404.mihoyogacha.dto

data class AuthKeyDTO(
    val data: AuthKeyData,
) {
    data class AuthKeyData(
        val authkey: String,
    )
}