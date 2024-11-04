package com.grepp.nbe1_3_team9.admin.service.oauth2

data class OAuth2UserInfo(
    val providerId: String,
    val email: String?,
    val name: String
)