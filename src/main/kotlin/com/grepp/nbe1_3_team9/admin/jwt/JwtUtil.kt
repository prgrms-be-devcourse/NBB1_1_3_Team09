package com.grepp.nbe1_3_team9.admin.jwt

import com.grepp.nbe1_3_team9.admin.redis.RefreshTokenService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtUtil(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService
) {

    // Jwt 생성 및 쿠키 저장
    fun generateToken(authentication: Authentication, response: HttpServletResponse): TokenRes {
        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        val refreshToken = jwtTokenProvider.createRefreshToken()

        refreshTokenService.saveRefreshToken(authentication.name, refreshToken, jwtTokenProvider.refreshTokenExpTime)

        CookieUtil.createAccessTokenCookie(accessToken, response)
        CookieUtil.createRefreshTokenCookie(refreshToken, response)

        return TokenRes(accessToken, refreshToken)
    }

    // RefreshToken으로 AccessToken 재발급
    fun reissueToken(authentication: Authentication, response: HttpServletResponse): String? {
        val storedRefreshToken = refreshTokenService.getRefreshToken(authentication.name)
        return storedRefreshToken?.let {
            val newAccessToken = jwtTokenProvider.createAccessToken(authentication)
            CookieUtil.createAccessTokenCookie(newAccessToken, response)
            newAccessToken
        }
    }

    // AccessToken, RefreshToken 삭제
    fun deleteTokens(authentication: Authentication, response: HttpServletResponse) {
        refreshTokenService.deleteRefreshToken(authentication.name)
        CookieUtil.deleteAccessTokenCookie(response)
        CookieUtil.deleteRefreshTokenCookie(response)
    }

    fun createAuthentication(email: String): Authentication {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        return UsernamePasswordAuthenticationToken(email, null, authorities)
    }

    // 래핑 메서드 추가
    fun validateToken(token: String): Boolean = jwtTokenProvider.validateToken(token)
    fun getAuthentication(token: String): Authentication = jwtTokenProvider.getAuthentication(token)
}
