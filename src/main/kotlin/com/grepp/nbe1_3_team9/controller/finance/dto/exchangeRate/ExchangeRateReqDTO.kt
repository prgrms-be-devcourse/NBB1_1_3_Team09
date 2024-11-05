package com.grepp.nbe1_3_team9.controller.finance.dto.exchangeRate

data class ExchangeRateReqDTO(
    val toCountry: String = "",
    val fromCountry: String = "",
    val amount: String = ""
)
