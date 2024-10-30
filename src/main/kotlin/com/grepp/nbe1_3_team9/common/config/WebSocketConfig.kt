package com.grepp.nbe1_3_team9.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // /ws와 /ws/schedule 두 엔드포인트를 모두 등록
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()

        registry.addEndpoint("/ws/schedule")
            .setAllowedOriginPatterns("*")
            .withSockJS()

        // 로그 추가
        println("WebSocket endpoint registered: /ws and /ws/schedule")
    }
}