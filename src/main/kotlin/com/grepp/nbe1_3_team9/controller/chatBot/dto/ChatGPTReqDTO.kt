package com.grepp.nbe1_3_team9.controller.chatBot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatGPTReqDTO(
    @JsonProperty("model") val model: String,
    @JsonProperty("messages") val messages: List<Message>,
    @JsonProperty("max_tokens") val maxTokens: Int = 1000
)
