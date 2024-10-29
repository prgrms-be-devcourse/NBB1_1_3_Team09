package com.grepp.nbe1_3_team9.admin.jwt

import com.grepp.nbe1_3_team9.admin.dto.CustomUserInfoDTO
import com.grepp.nbe1_3_team9.admin.service.CustomUserDetailsService
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtFilter(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtFilter::class.java)

    companion object {
        private const val ACCESS_HEADER = "Authorization"
        private const val BEARER_TYPE = "Bearer"
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val jwt = resolveToken(request)

        if (StringUtils.hasText(jwt)) {
            try {
                if (jwtUtil.validateToken(jwt.toString())) {
                    val userId = jwtUtil.getUserId(jwt.toString())
                    val userDetails = customUserDetailsService.loadUserByUsername(userId.toString())

                    if (userDetails != null) {
                        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                        authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authenticationToken
                    }
                }
            } catch (e: ExpiredJwtException) {
                val refreshToken = CookieUtil.getRefreshTokenFromCookies(request)
                if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                    val userId = jwtUtil.getUserId(refreshToken)
                    val userDetails = customUserDetailsService.loadUserByUsername(userId.toString())
                    if (userDetails != null) {
                        val newAccessToken = jwtUtil.createAccessToken(userDetails as CustomUserInfoDTO)
                        CookieUtil.createAccessTokenCookie(newAccessToken, response)

                        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                        authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authenticationToken
                    }
                }
            } catch (e: Exception) {
                log.error("JWT 검증 오류", e)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val token = CookieUtil.getAccessTokenFromCookies(request)
        log.info("AccessToken이 쿠키에서 추출되었습니다: {}", token)
        return token
    }
}