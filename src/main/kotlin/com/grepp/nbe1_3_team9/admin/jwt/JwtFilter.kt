package com.grepp.nbe1_3_team9.admin.jwt

import com.grepp.nbe1_3_team9.common.exception.exceptions.TokenException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val jwt = resolveToken(request)

        if (!jwt.isNullOrEmpty()) {
            try {
                if (jwtUtil.validateToken(jwt)) {
                    val authentication = jwtUtil.getAuthentication(jwt)
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    log.warn("유효하지 않은 JWT 토큰입니다.")
                }
            } catch (e: TokenException) {
                log.warn("JWT 토큰 검증 실패")
            }
        } else {
            log.warn("JWT 토큰이 제공되지 않았습니다.")
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null
        val token = cookies.find { it.name == "AccessToken" }

        return token?.value
    }
}
