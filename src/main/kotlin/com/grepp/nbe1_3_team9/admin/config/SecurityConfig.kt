package com.grepp.nbe1_3_team9.admin.config

import com.grepp.nbe1_3_team9.admin.jwt.JwtFilter
import com.grepp.nbe1_3_team9.admin.jwt.JwtUtil
import com.grepp.nbe1_3_team9.admin.service.oauth2.handler.OAuth2LoginFailureHandler
import com.grepp.nbe1_3_team9.admin.service.oauth2.handler.OAuth2LoginSuccessHandler
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

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler,
    private val oAuth2LoginFailureHandler: OAuth2LoginFailureHandler,
    private val corsConfig: CorsConfig
) {

    companion object {
        private val AUTH_WHITELIST = arrayOf(
            "/swagger-ui/**", "/api-docs", "/ws/**", "/users/signup", "/users/signin"
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
                cors.configurationSource(corsConfig.corsConfigurationSource())
            }
            .sessionManagement { sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .formLogin(AbstractHttpConfigurer<*, *>::disable)
            .httpBasic(AbstractHttpConfigurer<*, *>::disable)
            .oauth2Login { oauth2 ->
                oauth2
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/users/signin")
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler(oAuth2LoginFailureHandler)
            }
            .addFilterBefore(JwtFilter(jwtUtil, listOf("/users/signin")), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(*AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated()
            }

        return httpSecurity.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
