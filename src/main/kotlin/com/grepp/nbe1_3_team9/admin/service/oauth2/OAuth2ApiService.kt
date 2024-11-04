package com.grepp.nbe1_3_team9.admin.service.oauth2

import com.grepp.nbe1_3_team9.domain.entity.user.User

interface OAuth2ApiService {
    fun getAccessToken(code: String): String
    fun getUserInfo(accessToken: String): OAuth2UserInfo
    fun processUser(userInfo: OAuth2UserInfo): User
}
