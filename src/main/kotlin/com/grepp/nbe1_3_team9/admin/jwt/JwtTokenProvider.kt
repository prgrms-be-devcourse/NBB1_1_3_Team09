package com.grepp.nbe1_3_team9.admin.jwt

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.TokenException
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") secretKey: String,
    @Value("\${jwt.access_expiration_time}") val accessTokenExpTime: Long,
    @Value("\${jwt.refresh_expiration_time}") val refreshTokenExpTime: Long
) {
    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    companion object {
        private const val AUTHORITIES_KEY = "role"
        private const val BEARER_TYPE = "Bearer"
    }

    fun createAccessToken(authentication: Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        return Jwts.builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpTime))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createRefreshToken(): String {
        return Jwts.builder()
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpTime))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val authorities = claims[AUTHORITIES_KEY]?.toString()?.split(",")?.map { SimpleGrantedAuthority(it) }
            ?: throw RuntimeException("권한 정보가 없는 토큰입니다.")

        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
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

    fun getExpiration(token: String): Long {
        val expirationDate = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.expiration
        return expirationDate.time - Date().time
    }
}
