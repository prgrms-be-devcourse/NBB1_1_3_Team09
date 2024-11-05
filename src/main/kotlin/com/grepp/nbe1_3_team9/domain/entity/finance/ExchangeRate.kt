package com.grepp.nbe1_3_team9.domain.entity.finance

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "exchange_rate_tb")
class ExchangeRate (
    var toCountry: String,
    var fromCountry: String,

    @Column(nullable = false, precision = 15, scale = 8)
    var conversionRate: BigDecimal=BigDecimal(0),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val exchangeRateId: Long=0L,
)
