package com.qhy040404.mihoyogacha.dto

data class AccountDTO(
    val data: Data,
) {
    data class Data(
        val account_info: AccountInfo,
    )

    data class AccountInfo(
        val account_id: Int,
        val weblogin_token: String,
    )
}