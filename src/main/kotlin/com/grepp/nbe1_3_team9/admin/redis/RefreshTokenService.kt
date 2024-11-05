package com.grepp.nbe1_3_team9.admin.redis

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RefreshTokenService(
    @Qualifier("jwtRedisTemplate") private val redisTemplate: RedisTemplate<String, String>,
) {

    fun saveRefreshToken(email: String, refreshToken: String, expirationTime: Long) {
        redisTemplate.opsForValue().set(email, refreshToken, expirationTime, TimeUnit.SECONDS)
    }

    fun getRefreshToken(email: String): String? {
        return redisTemplate.opsForValue().get(email)
    }

    fun deleteRefreshToken(email: String) {
        redisTemplate.delete(email)
    }
}
