package com.grepp.nbe1_3_team9.admin.jwt

import com.grepp.nbe1_3_team9.common.exception.exceptions.TokenException
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val excludeUrls: List<String>
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val requestUri = request.requestURI
        if (excludeUrls.contains(requestUri)) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = resolveToken(request)
        if (!jwt.isNullOrEmpty()) {
            try {
                if (jwtUtil.validateToken(jwt)) {
                    // 유효한 AccessToken일 경우 사용자 인증 처리
                    val authentication = jwtUtil.getAuthentication(jwt)
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "검증되지 않은 JWT 토큰입니다.")
                    return
                }
            } catch (e: ExpiredJwtException) {
                // AccessToken이 만료된 경우 RefreshToken으로 재발급 시도
                val refreshToken = CookieUtil.getRefreshTokenFromCookies(request)
                if (!refreshToken.isNullOrEmpty() && jwtUtil.validateToken(refreshToken)) {
                    // RefreshToken이 유효할 경우 새로운 AccessToken 발급
                    val userId = jwtUtil.getAuthentication(refreshToken).name
                    val newAuthentication = jwtUtil.createAuthentication(userId)
                    val newAccessToken = jwtUtil.reissueToken(newAuthentication, response)

                    if (newAccessToken != null) {
                        SecurityContextHolder.getContext().authentication = newAuthentication
                        log.info("새로운 AccessToken이 발급되었습니다.")
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "AccessToken 재발급 실패")
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 RefreshToken입니다.")
                }
            } catch (e: TokenException) {
                log.warn("JWT 토큰 검증 실패", e)
            }
        } else {
            log.warn("JWT 토큰이 제공되지 않았습니다.")
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        return CookieUtil.getAccessTokenFromCookies(request)
    }
}
