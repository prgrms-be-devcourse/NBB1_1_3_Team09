package com.grepp.nbe1_3_team9.domain.repository.finance

import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FinancialPlanRepository :JpaRepository<FinancialPlan, Long> {

    @Modifying
    @Query("SELECT itemName, amount, expenseItemId, group FROM FinancialPlan WHERE group = :groupId")
    fun findALLByGroupId(groupId: Long): List<FinancialPlan>
}
