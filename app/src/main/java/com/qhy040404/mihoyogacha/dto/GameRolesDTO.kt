package com.qhy040404.mihoyogacha.dto

data class GameRolesDTO(
    val data: GameRoles
) {
    data class GameRoles(
        val list: List<Player>,
    )

    data class Player(
        val game_biz: String,
        val game_uid: String,
        val nickname: String,
        val region: String,
    )
}