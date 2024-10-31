package com.grepp.nbe1_3_team9.admin.jwt

import com.grepp.nbe1_3_team9.admin.dto.CustomUserInfoDTO
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.TokenException
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.time.ZonedDateTime
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") secretKey: String,
    @Value("\${jwt.access_expiration_time}") private val accessTokenExpTime: Long,
    @Value("\${jwt.refresh_expiration_time}") private val refreshTokenExpTime: Long
) {
    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun createAccessToken(user: CustomUserInfoDTO): String {
        return createToken(user, accessTokenExpTime)
    }

    fun createRefreshToken(user: CustomUserInfoDTO): String {
        return createToken(user, refreshTokenExpTime)
    }

    // 토큰 생성 공통 메서드
    private fun createToken(user: CustomUserInfoDTO, expireTime: Long): String {
        val claims = Jwts.claims().apply {
            put("userId", user.userId)
            put("username", user.username)
            put("email", user.email)
            put("role", user.role)
        }

        val now = ZonedDateTime.now()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(now.toInstant()))
            .setExpiration(Date.from(now.plusSeconds(expireTime).toInstant()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    // 토큰 유효성 검증
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            throw TokenException(ExceptionMessage.INVALID_TOKEN)
        } catch (e: ExpiredJwtException) {
            throw TokenException(ExceptionMessage.EXPIRED_TOKEN)
        } catch (e: UnsupportedJwtException) {
            throw TokenException(ExceptionMessage.UNSUPPORTED_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw TokenException(ExceptionMessage.EMPTY_CLAIMS)
        }
    }

    // 토큰에서 User ID 추출
    fun getUserId(token: String): Long {
        return parseClaims(token)["userId"] as Long
    }

    // JWT Claim 추출
    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}