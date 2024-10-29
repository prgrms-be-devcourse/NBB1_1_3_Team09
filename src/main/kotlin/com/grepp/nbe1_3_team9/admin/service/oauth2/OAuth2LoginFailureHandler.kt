package com.grepp.nbe1_3_team9.admin.service.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class OAuth2LoginFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    private val log = LoggerFactory.getLogger(OAuth2LoginFailureHandler::class.java)

    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        log.error("로그인 실패: {}", exception.message)

        redirectStrategy.sendRedirect(request, response, "/users/signin")
    }
}