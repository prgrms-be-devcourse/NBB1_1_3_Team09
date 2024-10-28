package com.grepp.nbe1_3_team9.admin.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.grepp.nbe1_3_team9.admin.dto.CustomUserInfoDTO
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.security.Key
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class JwtUtil(
    @Value("\${jwt.secret}") secretKey: String,
    @Value("\${jwt.access_expiration_time}") private val accessTokenExpTime: Long,
    @Value("\${jwt.refresh_expiration_time}") private val refreshTokenExpTime: Long,
    @Qualifier("jwtRedisTemplate") private val redisTemplate: RedisTemplate<String, String>
) {

    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule()) // LocalDateTime 처리를 위한 모듈 등록
    }

    private val log = LoggerFactory.getLogger(JwtUtil::class.java)

    fun createAccessToken(user: CustomUserInfoDTO): String {
        log.info("AccessToken 생성 완료 - userId: {}, email: {}", user.userId, user.email)
        return createToken(user, accessTokenExpTime)
    }

    fun createRefreshToken(user: CustomUserInfoDTO): String {
        val token = createToken(user, refreshTokenExpTime)
        val refreshToken = RefreshToken(user.email, token)
        try {
            val refreshTokenJson = objectMapper.writeValueAsString(refreshToken)
            redisTemplate.opsForValue().set(user.email, refreshTokenJson, refreshTokenExpTime, TimeUnit.SECONDS)
            log.info("RefreshToken 생성 및 Redis 저장 완료 - userId: {}, email: {}", user.userId, user.email)
        } catch (e: Exception) {
            log.error("RefreshToken 저장 실패 - userId: {}, email: {}", user.userId, user.email, e)
        }
        return token
    }

    private fun createToken(user: CustomUserInfoDTO, expireTime: Long): String {
        val claims = Jwts.claims().apply {
            put("userId", user.userId)
            put("username", user.username)
            put("email", user.email)
            put("role", user.role)
            put("joinedDate", user.signUpDate.toInstant(ZoneOffset.UTC).toEpochMilli())
        }

        val now = ZonedDateTime.now()
        val tokenValidity = now.plusSeconds(expireTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(now.toInstant()))
            .setExpiration(Date.from(tokenValidity.toInstant()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getRefreshToken(email: String): RefreshToken? {
        val refreshTokenJson = redisTemplate.opsForValue().get(email)
        return try {
            log.info("Redis에서 RefreshToken 조회 - email: {}", email)
            objectMapper.readValue(refreshTokenJson, RefreshToken::class.java)
        } catch (e: Exception) {
            log.error("Redis에서 RefreshToken 조회 실패 - email: {}", email, e)
            null
        }
    }

    fun deleteRefreshToken(email: String) {
        log.info("Redis에서 RefreshToken 삭제 - email: {}", email)
        redisTemplate.delete(email)
    }

    fun getUserId(token: String): Long {
        val userId = parseClaims(token)["userId", Long::class.java]
        log.info("JWT에서 userId 추출 - userId: {}", userId)
        return userId
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: SecurityException) {
            throw JwtException(ExceptionMessage.INVALID_TOKEN)
        } catch (e: MalformedJwtException) {
            throw JwtException(ExceptionMessage.INVALID_TOKEN)
        } catch (e: ExpiredJwtException) {
            throw JwtException(ExceptionMessage.EXPIRED_TOKEN)
        } catch (e: UnsupportedJwtException) {
            throw JwtException(ExceptionMessage.UNSUPPORTED_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw JwtException(ExceptionMessage.EMPTY_CLAIMS)
        }
    }

    fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}