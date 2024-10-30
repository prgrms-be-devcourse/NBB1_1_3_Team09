package com.grepp.nbe1_3_team9.domain.service.finance

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.ExchangeRateException
import com.grepp.nbe1_3_team9.controller.finance.dto.ExchangeRateReqDTO
import com.grepp.nbe1_3_team9.controller.finance.dto.ExchangeRateResDTO
import com.grepp.nbe1_3_team9.domain.entity.ExchangeRate
import com.grepp.nbe1_3_team9.domain.repository.finance.ExchangeRateRepository
import jakarta.persistence.EntityManager
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime

@Service
class ExchangeRateService(
    private val exchangeRateRepository: ExchangeRateRepository,
    @Value("\${exchangeRate-API_KEY}") private val exchangeRateApiKey: String,
    private val em: EntityManager
) {
    fun exchangeRate(exchangeRateReqDTO: ExchangeRateReqDTO): ExchangeRateResDTO {
        try {
            val conversionRate: BigDecimal = exchangeRateRepository.findConversionRate(
                exchangeRateReqDTO.toCountry,
                exchangeRateReqDTO.fromCountry
            )

            val result = ExchangeRateResDTO(
                time = exchangeRateTime,
                toCountry = exchangeRateReqDTO.toCountry,
                fromCountry = exchangeRateReqDTO.fromCountry,
                toAmount = BigDecimal(exchangeRateReqDTO.amount),
                conversionRate = conversionRate,
                fromAmount = conversionRate.multiply(BigDecimal(exchangeRateReqDTO.amount))
            )


            return result
        } catch (e: Exception) {
            //log.warn(">>>> {} : {} <<<<", e, ExchangeRateException(ExceptionMessage.EXCHANGE_ERROR))
            throw ExchangeRateException(ExceptionMessage.EXCHANGE_ERROR)
        }
    }

    // 특정 시간마다 환율 정보 갱신
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 0시에 실행
    fun exchangeRateSchedule() {
        exchangeRateTime = LocalDateTime.now()
        val size = currencyCode.size

        for (i in 0 until size) {
            val urlStr =
                "https://v6.exchangerate-api.com/v6/$exchangeRateApiKey/latest/${currencyCode[i]}" // toCountry

            try {
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/json")

                if (conn.responseCode != 200) {
                    throw RuntimeException("Failed : HTTP error code : " + conn.responseCode)
                }

                val br = BufferedReader(InputStreamReader(conn.inputStream))
                val responseBuilder = StringBuilder()
                var line: String?

                while (br.readLine().also { line = it } != null) {
                    responseBuilder.append(line)
                }

                br.close()
                val response = responseBuilder.toString()
                conn.disconnect()

                val json = JSONObject(response)
                val rates = json.getJSONObject("conversion_rates")
                val map = ObjectMapper().readValue(rates.toString(), MutableMap::class.java) as Map<*, *>

                for (j in currencyCode.indices) {
                    val rate = map[currencyCode[j]].toString()
                    val id: Long = exchangeRateRepository.findIdByFromCountryAndToCountry(
                        currencyCode[j],
                        currencyCode[i]
                    )
                    val exchangeRate = em.find(ExchangeRate::class.java, id)
                    if (exchangeRate != null) {
                        exchangeRate.conversionRate = BigDecimal(rate) // 환율 업데이트
                        em.persist(exchangeRate)
                    }
                }
            } catch (e: Exception) {
                //log.warn(">>>> {} : {} <<<<", e, ExchangeRateException(ExceptionMessage.EXCHANGE_ERROR))
                throw ExchangeRateException(ExceptionMessage.EXCHANGE_ERROR)
            }
        }
    }

    companion object {
        private var exchangeRateTime: LocalDateTime = LocalDateTime.now()
    }

    //    private val currencyCode = arrayOf("AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTN", "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC", "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR", "FJD", "FKP", "FOK", "GBP", "GEL", "GGP", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR", "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KID", "KMF", "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK", "SGD", "SHP", "SLE", "SOS", "SRD", "SSP", "STN", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", "TVD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VES", "VND", "VUV", "WST", "XAF", "XCD", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL")
    private val currencyCode = arrayOf("AED", "AFN", "ALL", "AMD")
}
