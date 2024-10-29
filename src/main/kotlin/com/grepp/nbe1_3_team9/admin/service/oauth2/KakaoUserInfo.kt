package com.grepp.nbe1_3_team9.admin.service.oauth2

class KakaoUserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {

    override fun getProviderId(): String {
        return attributes["id"].toString()
    }

    override fun getProvider(): String {
        return "kakao"
    }

    override fun getName(): String? {
        val properties = attributes["properties"] as? Map<*, *>
        return properties?.get("nickname") as? String
    }

    fun getEmail(): String? {
        val kakaoAccount = attributes["kakao_account"] as? Map<*, *>
        return kakaoAccount?.get("email") as? String
    }
}