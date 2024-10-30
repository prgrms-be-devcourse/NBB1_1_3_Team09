package com.grepp.nbe1_3_team9.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "exchange_rate_tb")
class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val exchangeRateId: Long? = null

    var toCountry: String=""
    var fromCountry: String=""

    @Column(nullable = false, precision = 15, scale = 8)
    var conversionRate: BigDecimal=BigDecimal(0)
}
