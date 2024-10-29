package com.grepp.nbe1_3_team9.admin.dto

import com.grepp.nbe1_3_team9.domain.entity.user.Role
import java.time.LocalDateTime

data class CustomUserInfoDTO(
    val userId: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: Role? = null,
    val signUpDate: LocalDateTime? = null
)