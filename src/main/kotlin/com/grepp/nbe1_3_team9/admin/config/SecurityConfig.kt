package com.grepp.nbe1_3_team9.admin.config

import com.grepp.nbe1_3_team9.admin.jwt.JwtFilter
import com.grepp.nbe1_3_team9.admin.jwt.JwtUtil
import com.grepp.nbe1_3_team9.admin.service.CustomUserDetailsService
import com.grepp.nbe1_3_team9.admin.service.oauth2.OAuth2LoginFailureHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil,
    private val oAuth2LoginFailureHandler: OAuth2LoginFailureHandler
) {

    companion object {
        private val AUTH_WHITELIST = arrayOf(
            "/swagger-ui/**", "/api-docs", "/users/**", "/ws/**"
        )
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/ws/**").disable()
            }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .sessionManagement { sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .formLogin(AbstractHttpConfigurer<*, *>::disable)
            .httpBasic(AbstractHttpConfigurer<*, *>::disable)
            .oauth2Login { oauth2 ->
                oauth2.failureHandler(oAuth2LoginFailureHandler)
            }
            .addFilterBefore(JwtFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(*AUTH_WHITELIST).permitAll()
                    .anyRequest().permitAll()
            }

        return httpSecurity.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}