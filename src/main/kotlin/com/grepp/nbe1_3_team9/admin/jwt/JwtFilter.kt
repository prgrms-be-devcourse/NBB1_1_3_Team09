package com.grepp.nbe1_3_team9.admin.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    companion object {
        private const val ACCESS_HEADER = "Authorization"
        private const val BEARER_TYPE = "Bearer"
    }

    private val log = LoggerFactory.getLogger(JwtFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val jwt = resolveToken(request)

        if (StringUtils.hasText(jwt) && jwt != null && jwtUtil.validateToken(jwt)) {
            val authentication = jwtUtil.getAuthentication(jwt)
            val context: SecurityContext = SecurityContextHolder.createEmptyContext()
            context.authentication = authentication
            SecurityContextHolder.setContext(context)
        } else {
            log.warn("유효하지 않은 JWT 토큰이 요청됨: $jwt")
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val token = request.getHeader(ACCESS_HEADER)
        return if (StringUtils.hasText(token) && token.startsWith(BEARER_TYPE)) {
            token.substring(BEARER_TYPE.length)
        } else {
            null
        }
    }
}
