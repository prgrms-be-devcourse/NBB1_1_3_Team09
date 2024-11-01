package com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan

import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import java.math.BigDecimal

class FinancialPlanDTO (
    var financialPlanId: Long,
    var itemName:String,
    var amount: String,
){
    companion object{
        fun toDTO(financialPlan: FinancialPlan): FinancialPlanDTO {
            return FinancialPlanDTO(financialPlan.expenseItemId, financialPlan.itemName, financialPlan.amount.toString())
        }
        fun toEntity(financialPlanDTO: FinancialPlanDTO): FinancialPlan {
            return FinancialPlan(financialPlanDTO.itemName, BigDecimal(financialPlanDTO.amount), financialPlanDTO.financialPlanId)
        }
    }
}
