package com.grepp.nbe1_3_team9.domain.service.chatBot

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.AccountBookException
import com.grepp.nbe1_3_team9.controller.chatBot.dto.ChatGPTReqDTO
import com.grepp.nbe1_3_team9.controller.chatBot.dto.ChatGPTResDTO
import com.grepp.nbe1_3_team9.controller.chatBot.dto.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class ChatBotService {
    @Value("\${openai.model}")
    private val model: String=""

    @Value("\${openai.api.url}")
    private val apiURL: String=""

    @Value("\${openai.api.key}")
    private val openAiKey: String=""

    private val userConversations: MutableMap<String, LinkedList<Message>> =
        HashMap<String, LinkedList<Message>>() //대화상태를 유지하는 맵

    fun chat(message: String, userId: String): String {
        var answer = "오류가 발생했습니다. 다시 시도해주세요." //이 멘트가 변경X = 오류 발생한 것
        try {
            val conversation: LinkedList<Message> =
                userConversations.computeIfAbsent(userId) { k: String? -> LinkedList<Message>() }  //사용자별 대화 상태를 유지하면서 메시지 수 제한

            conversation.add(
                Message(
                    "user",
                    "$message 여기서부터는 스타일 주문이야 출력에 이 문장 관련한거 넣지 마. 이걸 그냥 String 형태로 받고있어. 줄 바꿈 할 때 마다 하나씩 개행문자('\n')을 넣어줘."
                )
            )
            if (conversation.size > MAX_MESSAGES) {
                conversation.removeFirst()
            }

            val reqDTO: ChatGPTReqDTO = ChatGPTReqDTO(model, conversation)

            val restTemplate = RestTemplate()
            restTemplate.interceptors.add(ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray?, execution: ClientHttpRequestExecution ->
                request.headers.add(
                    "Authorization",
                    "Bearer $openAiKey"
                )
                execution.execute(request, body!!)
            })

            val chatGPTResDTO: ChatGPTResDTO? = restTemplate.postForObject(apiURL, reqDTO, ChatGPTResDTO::class.java)
            if (chatGPTResDTO != null) {
                answer = chatGPTResDTO.choices?.get(0)?.message?.content.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            log.warn(">>>> {} : {} <<<<", e, AccountBookException(ExceptionMessage.FORMAT_ERROR))
            throw AccountBookException(ExceptionMessage.FORMAT_ERROR)
        }
        return answer
    }

    companion object {
        private const val MAX_MESSAGES = 5 //사용자 별 유지할 최대 메시지 개수
    }
}
