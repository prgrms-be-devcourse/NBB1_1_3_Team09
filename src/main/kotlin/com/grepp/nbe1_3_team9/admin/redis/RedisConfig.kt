package com.grepp.nbe1_3_team9.admin.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.grepp.nbe1_3_team9.notification.entity.Notification
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int
    // @Value("\${spring.data.redis.password}") private val password: String
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration().apply {
            hostName = host
            port = this@RedisConfig.port
            // password = this@RedisConfig.password
        }
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean(name = ["jwtRedisTemplate"])
    fun redisTemplate(): RedisTemplate<String, String> {
        return RedisTemplate<String, String>().apply {
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            connectionFactory = redisConnectionFactory()
        }
    }

    @Bean("notificationRedisTemplate")
    fun notificationRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Notification> {
        val objectMapper = JsonMapper.builder()
            .addModule(JavaTimeModule())
            .build()

        val serializer = Jackson2JsonRedisSerializer(objectMapper, Notification::class.java)

        return RedisTemplate<String, Notification>().apply {
            setConnectionFactory(connectionFactory)

            keySerializer = StringRedisSerializer()
            valueSerializer = serializer

            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = serializer
        }
    }

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        // GenericJackson2JsonRedisSerializer 사용하여 리스트 직렬화
        val serializer = GenericJackson2JsonRedisSerializer()

        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .entryTtl(Duration.ofHours(6))

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfig)
            .build()
    }

}
