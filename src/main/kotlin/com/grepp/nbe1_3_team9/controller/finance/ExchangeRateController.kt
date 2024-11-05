package com.grepp.nbe1_3_team9.controller.finance

import com.grepp.nbe1_3_team9.controller.finance.dto.exchangeRate.ExchangeRateReqDTO
import com.grepp.nbe1_3_team9.controller.finance.dto.exchangeRate.ExchangeRateResDTO
import com.grepp.nbe1_3_team9.domain.service.finance.ExchangeRateService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/exchangeRate")
class ExchangeRateController(
    private val exchangeRateService: ExchangeRateService
) {

    @PostMapping
    fun exchangeRate(@RequestBody exchangeRateReqDTO: ExchangeRateReqDTO): ExchangeRateResDTO {
        return exchangeRateService.exchangeRate(exchangeRateReqDTO)
    }
}
