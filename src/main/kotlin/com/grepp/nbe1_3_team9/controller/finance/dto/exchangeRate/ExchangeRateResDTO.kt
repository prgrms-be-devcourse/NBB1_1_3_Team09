package com.grepp.nbe1_3_team9.controller.finance.dto.exchangeRate

import java.math.BigDecimal
import java.time.LocalDateTime

data class ExchangeRateResDTO(
    val time: LocalDateTime?,
    val toCountry: String = "",
    val fromCountry: String = "",
    val toAmount: BigDecimal? = null,
    val conversionRate: BigDecimal? = null,
    val fromAmount: BigDecimal? = null
)
