package com.grepp.nbe1_3_team9.controller.chatBot.dto

class ChatGPTResDTO(
    val choices: List<Choice>? = null // 자동으로 getChoices()가 생성됨
) {
    class Choice(
        var index: Int = 0,
        var message: Message
    )
}
