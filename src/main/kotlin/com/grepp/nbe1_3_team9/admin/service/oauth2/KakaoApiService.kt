package com.grepp.nbe1_3_team9.admin.service.oauth2

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team9.admin.jwt.CookieUtil
import com.grepp.nbe1_3_team9.admin.jwt.JwtUtil
import com.grepp.nbe1_3_team9.admin.jwt.TokenRes
import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.domain.entity.user.OAuthProvider
import com.grepp.nbe1_3_team9.domain.entity.user.Role
import com.grepp.nbe1_3_team9.domain.entity.user.User
import com.grepp.nbe1_3_team9.domain.repository.user.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Service
class KakaoApiService(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    @Value("\${spring.security.oauth2.client.registration.kakao.client-id}") private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.kakao.client-secret}") private val clientSecret: String,
    @Value("\${spring.security.oauth2.client.registration.kakao.redirect-uri}") private val redirectUri: String
): OAuth2ApiService {

    private val log = LoggerFactory.getLogger(KakaoApiService::class.java)

    @Throws(JsonProcessingException::class)
    override fun getAccessToken(code: String): String {
        val tokenUri = "https://kauth.kakao.com/oauth/token"

        return try {
            val response = WebClient.create()
                .post()
                .uri(tokenUri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(
                    BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("redirect_uri", redirectUri)
                        .with("code", code)
                        .with("client_secret", clientSecret)
                )
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            log.info("AccessToken 요청 성공: {}", response)
            val tokenData: Map<String, Any> = ObjectMapper().readValue(response, object : TypeReference<Map<String, Any>>() {})
            tokenData["access_token"].toString()
        } catch (e: WebClientResponseException) {
            log.error("AccessToken 요청 실패: {}", e.responseBodyAsString)
            throw e
        } catch (e: Exception) {
            log.error("AccessToken 요청 중 에러 발생", e)
            throw RuntimeException("카카오 액세스 토큰 요청 실패")
        }
    }

    @Throws(JsonProcessingException::class)
    override fun getUserInfo(accessToken: String): OAuth2UserInfo {
        val userInfoUri = "https://kapi.kakao.com/v2/user/me"

        val response = WebClient.create(userInfoUri)
            .post()
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        log.info("사용자 정보 응답: {}", response)
        val attributes: Map<String, Any> = ObjectMapper().readValue(response, object : TypeReference<Map<String, Any>>() {})
        return OAuth2UserInfo(
            providerId = attributes["id"].toString(),
            email = (attributes["kakao_account"] as Map<String, Any>)["email"]?.toString(),
            name = (attributes["properties"] as Map<String, Any>)["nickname"].toString()
        )
    }

    override fun processUser(userInfo: OAuth2UserInfo, response: HttpServletResponse): TokenRes {
        val providerId = userInfo.providerId
        var user = userRepository.findByProviderId(providerId)

        if (user == null) {
            val uniqueIdentifier = "kakao_$providerId"
            val secureRandomPassword = UUID.randomUUID().toString()

            user = User(
                provider = OAuthProvider.KAKAO,
                providerId = providerId,
                username = userInfo.name,
                email = uniqueIdentifier + "@kakao.com",
                password = secureRandomPassword,
                role = Role.MEMBER
            )
            userRepository.save(user)
            log.info("신규 유저 등록: {}", userInfo.name)
        } else {
            log.info("기존 유저 로그인: {}", userInfo.name)
        }

        val authentication = UsernamePasswordAuthenticationToken(
            CustomUserDetails(user), null, listOf(SimpleGrantedAuthority(user.role.name))
        )
        val token = jwtUtil.generateToken(authentication, response)

        CookieUtil.createAccessTokenCookie(token.accessToken, response)
        CookieUtil.createRefreshTokenCookie(token.refreshToken, response)

        response.sendRedirect("http://localhost:3000/")
        return token
    }
}
