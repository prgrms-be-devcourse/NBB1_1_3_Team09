package com.grepp.nbe1_3_team9.domain.repository.finance

import com.grepp.nbe1_3_team9.domain.entity.ExchangeRate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal

interface ExchangeRateRepository : JpaRepository<ExchangeRate, Long> {
    @Query("SELECT conversionRate FROM ExchangeRate WHERE fromCountry =:fromCountry AND toCountry =:toCountry")
    fun findConversionRate(toCountry: String, fromCountry: String): BigDecimal

    @Query("SELECT exchangeRateId FROM ExchangeRate WHERE fromCountry =:fromCountry AND toCountry =:toCountry")
    fun findIdByFromCountryAndToCountry(fromCountry: String, toCountry: String): Long
}
