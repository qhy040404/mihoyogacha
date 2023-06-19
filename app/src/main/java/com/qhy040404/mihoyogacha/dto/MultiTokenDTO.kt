package com.qhy040404.mihoyogacha.dto

data class MultiTokenDTO(
    val data: Tokens,
) {
    data class Tokens(
        val list: List<DataObj>,
    )

    data class DataObj(
        val name: String,
        val token: String,
    )
}