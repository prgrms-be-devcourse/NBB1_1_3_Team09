package com.grepp.nbe1_3_team9.admin.jwt

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.admin.service.CustomUserDetailsService
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.TokenException
import com.grepp.nbe1_3_team9.common.exception.exceptions.UserException
import com.grepp.nbe1_3_team9.domain.repository.user.UserRepository
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") secretKey: String,
    @Value("\${jwt.access_expiration_time}") val accessTokenExpTime: Long,
    @Value("\${jwt.refresh_expiration_time}") val refreshTokenExpTime: Long,
    private val customUserDetailsService: CustomUserDetailsService
) {
    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    fun createAccessToken(authentication: Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        return Jwts.builder()
            .setSubject(authentication.name)
            .claim("role", authorities)
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpTime * 1000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createRefreshToken(): String {
        return Jwts.builder()
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpTime * 1000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val authorities = claims["role"]?.toString()?.split(",")?.map { SimpleGrantedAuthority(it) }

        val user = customUserDetailsService.loadUserByUsername(claims.subject)

        return UsernamePasswordAuthenticationToken(user, "", authorities)
    }

    // JWT Claim 추출
    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    // 토큰 유효성 검증
    fun validateToken(token: String): Boolean {
        return try {
            log.debug("Validating token: $token")
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
}
