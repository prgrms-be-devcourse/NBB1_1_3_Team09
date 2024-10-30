package com.grepp.nbe1_3_team9.domain.service.finance

import com.google.cloud.vision.v1.*
import com.google.protobuf.ByteString
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.AccountBookException
import com.grepp.nbe1_3_team9.controller.chatBot.dto.ChatGPTReqDTO
import com.grepp.nbe1_3_team9.controller.chatBot.dto.ChatGPTResDTO
import com.grepp.nbe1_3_team9.controller.chatBot.dto.Message
import com.grepp.nbe1_3_team9.controller.finance.dto.ReceiptDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.MediaType
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

import java.util.*

@Service
class OCRService(
    @Value("\${openai.model}") private val model: String,
    @Value("\${openai.api.url}") private val apiURL: String,
    @Value("\${openai.api.key}") private val openAiKey: String
) {

    @Throws(AccountBookException::class)
    fun extractTextFromImage(image: String?): Map<Int, String> {
        val decodedBytes = Base64.getDecoder().decode(image)
        val imgBytes = ByteString.copyFrom(decodedBytes)

        val img = Image.newBuilder().setContent(imgBytes).build()
        val feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build()
        val request = AnnotateImageRequest.newBuilder()
            .addFeatures(feat)
            .setImage(img)
            .build()

        val requests = mutableListOf(request)
        val results = mutableMapOf<Int, String>()
        val stringBuilder = StringBuilder()

        try {
            ImageAnnotatorClient.create().use { client ->
                val response = client.batchAnnotateImages(requests)
                response.responsesList.forEach { res ->
                    if (res.hasError()) {
                        when (res.error.code) {
                            3 -> throw AccountBookException(ExceptionMessage.IMAGE_NOT_FOUND)
                            else -> throw AccountBookException(ExceptionMessage.OCR_ERROR)
                        }
                    }
                    stringBuilder.append(res.fullTextAnnotation.text)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw AccountBookException(ExceptionMessage.OCR_ERROR)
        }

        stringBuilder.toString().split("\n").forEachIndexed { idx, str ->
            results[idx] = str
        }

        return results
    }

    fun formatting(receipt: ReceiptDTO): ReceiptDTO {
        var amount = receipt.amount?.replace("[^\\d]".toRegex(), "")
        var resultDate = ""

        try {
            val messages = listOf(
                Message(
                    role = "user",
                    content = "${receipt.expenseDate} Please convert this word into 'yyyy-MM-ddTHH:mm:ss' format. " +
                            "If you don't have enough time information, you can infer and fill it in. " +
                            "Instead, you have to keep this format 'yyyy-MM-ddTHH:mm:ss'. " +
                            "Only print 'yyyy-MM-ddTHH:mm:ss' without additional text."
                )
            )
            val reqDTO = ChatGPTReqDTO(model = model, messages = messages)

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $openAiKey")
            }

            val requestEntity = HttpEntity(reqDTO, headers)
            val restTemplate = RestTemplate()
            val chatGPTResDTO: ChatGPTResDTO? = restTemplate.postForObject(apiURL, requestEntity, ChatGPTResDTO::class.java)

            resultDate = chatGPTResDTO?.choices?.firstOrNull()?.message?.content ?: ""
            if (resultDate.length > 19) {
                resultDate = resultDate.replace("[^0-9T:-]".toRegex(), "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw AccountBookException(ExceptionMessage.FORMAT_ERROR)
        }

        return ReceiptDTO(resultDate, amount)
    }
}
