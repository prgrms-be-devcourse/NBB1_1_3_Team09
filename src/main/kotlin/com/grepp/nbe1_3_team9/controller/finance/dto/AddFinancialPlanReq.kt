package com.grepp.nbe1_3_team9.controller.finance.dto

import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import java.math.BigDecimal

class AddFinancialPlanReq (
    var itemName:String,
    var amount: String,
){
    companion object{
        fun toEntity(financialPlanDTO: AddFinancialPlanReq): FinancialPlan {
            return FinancialPlan(financialPlanDTO.itemName, BigDecimal(financialPlanDTO.amount))
        }
    }
}