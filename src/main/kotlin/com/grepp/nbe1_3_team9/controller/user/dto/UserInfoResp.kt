package com.grepp.nbe1_3_team9.controller.user.dto

import java.time.LocalDateTime

data class UserInfoResp(
    val userId: Long? = null,
    val username: String = "",
    val email: String = "",
    val role: String = "",
    val joinedDate: LocalDateTime? = null
)