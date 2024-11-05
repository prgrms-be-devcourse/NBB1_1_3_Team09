package com.grepp.nbe1_3_team9.admin.jwt

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

object CookieUtil {

    private const val ACCESS_TOKEN_COOKIE_NAME = "AccessToken"
    private const val REFRESH_TOKEN_COOKIE_NAME = "RefreshToken"
    private const val ACCESS_TOKEN_EXPIRY = 60 * 60             // 1시간
    private const val REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60   // 7일

    fun createAccessTokenCookie(token: String, response: HttpServletResponse) {
        val cookie = Cookie(ACCESS_TOKEN_COOKIE_NAME, token).apply {
            isHttpOnly = true
            secure = false
            path = "/"
            maxAge = ACCESS_TOKEN_EXPIRY
        }
        response.addCookie(cookie)
    }

    fun createRefreshTokenCookie(token: String, response: HttpServletResponse) {
        val cookie = Cookie(REFRESH_TOKEN_COOKIE_NAME, token).apply {
            isHttpOnly = true
            secure = false
            path = "/"
            maxAge = REFRESH_TOKEN_EXPIRY
        }
        response.addCookie(cookie)
    }

    fun getAccessTokenFromCookies(request: HttpServletRequest): String? {
        return getTokenFromCookies(request, ACCESS_TOKEN_COOKIE_NAME)
    }

    fun getRefreshTokenFromCookies(request: HttpServletRequest): String? {
        return getTokenFromCookies(request, REFRESH_TOKEN_COOKIE_NAME)
    }

    private fun getTokenFromCookies(request: HttpServletRequest, tokenName: String): String? {
        val cookies = request.cookies
        cookies?.forEach { cookie ->
            if (tokenName == cookie.name) {
                return cookie.value
            }
        }
        return null
    }

    fun deleteAccessTokenCookie(response: HttpServletResponse) {
        deleteCookie(response, ACCESS_TOKEN_COOKIE_NAME)
    }

    fun deleteRefreshTokenCookie(response: HttpServletResponse) {
        deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME)
    }

    private fun deleteCookie(response: HttpServletResponse, cookieName: String) {
        val cookie = Cookie(cookieName, null).apply {
            isHttpOnly = true
            secure = false
            path = "/"
            maxAge = 0
        }
        response.addCookie(cookie)
    }
}