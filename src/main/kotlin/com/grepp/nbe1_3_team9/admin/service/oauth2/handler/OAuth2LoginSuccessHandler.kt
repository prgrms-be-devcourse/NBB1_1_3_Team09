package com.grepp.nbe1_3_team9.admin.service.oauth2.handler

import com.grepp.nbe1_3_team9.admin.jwt.CookieUtil
import com.grepp.nbe1_3_team9.admin.jwt.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginSuccessHandler(
    private val jwtUtil: JwtUtil
) : AuthenticationSuccessHandler {

    private val log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler::class.java)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user = authentication?.principal as OAuth2User
        val email = user.attributes["email"] as? String ?
        val username = user.attributes["name"] as? String ?

        if (email.isNullOrEmpty() || username.isNullOrEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "필수 사용자 정보가 누락되었습니다. 이메일과 사용자 이름이 필요합니다.")
            return
        }

        val authentication = jwtUtil.createAuthentication(email)
        val token = jwtUtil.generateToken(authentication, response)
        CookieUtil.createAccessTokenCookie(token.accessToken, response)

        log.info("OAuth2 로그인 성공 - 토큰을 쿠키에 저장했습니다.")
        response.sendRedirect("/")
    }
}