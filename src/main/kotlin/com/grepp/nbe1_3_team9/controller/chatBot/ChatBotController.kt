package com.grepp.nbe1_3_team9.controller.chatBot

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.domain.service.chatBot.ChatBotService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chat")
class ChatBotController (
    private val chatBotService: ChatBotService
){
    @PostMapping
    fun chat(@RequestBody message: Map<String, String?>,
             @AuthenticationPrincipal user: CustomUserDetails
    ): String {
        val userId: String = user.getUsername()
        val inputMessage:String= message["message"].toString()
        return chatBotService.chat(inputMessage, userId)

    }
}
