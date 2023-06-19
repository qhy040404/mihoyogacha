package com.qhy040404.mihoyogacha.dto

import com.qhy040404.mihoyogacha.constant.Game

data class GameRole(
    val type: Game,
    val nickname: String,
    val uid: String,
    val region: String,
    val biz: String,
    val authKey: String,
)