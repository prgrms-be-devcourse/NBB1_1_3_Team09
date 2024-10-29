package com.grepp.nbe1_3_team9.controller.user.dto

import jakarta.validation.constraints.NotNull

data class SignInReq(
    @field:NotNull
    val email: String = "",

    @field:NotNull
    val password: String = ""
)