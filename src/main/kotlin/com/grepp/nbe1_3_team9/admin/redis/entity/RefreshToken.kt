package com.grepp.nbe1_3_team9.admin.redis.entity

import java.io.Serializable

data class RefreshToken(
    var id: String = "",
    var refreshToken: String = ""
) : Serializable