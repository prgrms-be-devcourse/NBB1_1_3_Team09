package com.grepp.nbe1_3_team9.domain.repository.finance

import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FinancialPlanRepository :JpaRepository<FinancialPlan, Long> {

    fun findAllByGroup_GroupId(groupId: Long): List<FinancialPlan>
}
