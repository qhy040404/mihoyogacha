package com.qhy040404.mihoyogacha.dto

data class AuthKeyPostData(
    val auth_appid: String,
    val game_biz: String,
    val game_uid: String,
    val region: String,
)