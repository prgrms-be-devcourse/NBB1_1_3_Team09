package com.grepp.nbe1_3_team9.domain.service.finance

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.FinancialPlanException
import com.grepp.nbe1_3_team9.controller.finance.dto.FinancialPlanDTO
import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import com.grepp.nbe1_3_team9.domain.repository.finance.FinancialPlanRepository
import org.springframework.stereotype.Service

@Service
class FinancialPlanService (
    private val financialPlanRepository: FinancialPlanRepository
){
    fun addFinancialPlan(groupId: String, financialPlanDTO: FinancialPlanDTO, userId: Long) {
        val financialPlan:FinancialPlan = FinancialPlanDTO.toEntity(financialPlanDTO)
        val result=financialPlanRepository.save(financialPlan)

        if(result.itemName!=financialPlan.itemName){
            throw FinancialPlanException(ExceptionMessage.DB_ERROR)
        }
    }

}