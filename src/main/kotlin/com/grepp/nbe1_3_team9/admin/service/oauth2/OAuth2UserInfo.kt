package com.grepp.nbe1_3_team9.admin.service.oauth2

interface OAuth2UserInfo {
    fun getProviderId(): String
    fun getProvider(): String
    fun getName(): String?
}