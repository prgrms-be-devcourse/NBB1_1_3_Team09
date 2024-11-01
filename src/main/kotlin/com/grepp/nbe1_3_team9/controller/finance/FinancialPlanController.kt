package com.grepp.nbe1_3_team9.controller.finance

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.controller.finance.dto.AddFinancialPlanReq
import com.grepp.nbe1_3_team9.domain.service.finance.FinancialPlanService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/financial-plan")
class FinancialPlanController(
    private val financialPlanService: FinancialPlanService
) {

    //금전 계획 추가
    @PostMapping("/{groupId}")
    fun addFinancialPlan(
        @PathVariable groupId: String,
        @RequestBody financialPlanDTO: AddFinancialPlanReq,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) {
        val userId=customUserDetails.getUserId()
        financialPlanService.addFinancialPlan(groupId, financialPlanDTO, userId)
    }
}