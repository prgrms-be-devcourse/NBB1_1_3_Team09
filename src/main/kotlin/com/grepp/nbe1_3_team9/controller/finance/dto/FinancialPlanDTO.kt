package com.grepp.nbe1_3_team9.controller.finance.dto

import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import java.math.BigDecimal

class FinancialPlanDTO (
    var financialPlanId: Long,
    var ItemName:String,
    var amount: String,
){
    companion object{
        fun toDTO(financialPlan: FinancialPlan): FinancialPlanDTO {
            return FinancialPlanDTO(financialPlan.expenseItemId, financialPlan.itemName, financialPlan.amount.toString())
        }
        fun toEntity(financialPlanDTO: FinancialPlanDTO): FinancialPlan {
            return FinancialPlan(financialPlanDTO.ItemName, BigDecimal(financialPlanDTO.amount), financialPlanDTO.financialPlanId)
        }
    }
}