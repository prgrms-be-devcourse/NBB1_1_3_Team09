package com.grepp.nbe1_3_team9.admin.service.oauth2

import com.grepp.nbe1_3_team9.admin.jwt.TokenRes
import jakarta.servlet.http.HttpServletResponse

interface OAuth2ApiService {
    fun getAccessToken(code: String): String
    fun getUserInfo(accessToken: String): OAuth2UserInfo
    fun processUser(userInfo: OAuth2UserInfo, response: HttpServletResponse): TokenRes
}
