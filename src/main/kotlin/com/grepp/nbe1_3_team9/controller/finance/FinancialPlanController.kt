package com.grepp.nbe1_3_team9.controller.finance

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.AddFinancialPlanReq
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.FinancialPlanDTO
import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import com.grepp.nbe1_3_team9.domain.service.finance.FinancialPlanService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/financial-plan")
class FinancialPlanController(
    private val financialPlanService: FinancialPlanService
) {

    //금전 계획 추가
    @PostMapping("/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addFinancialPlan(
        @PathVariable groupId: String,
        @RequestBody financialPlanDTO: AddFinancialPlanReq,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) {
        val userId=customUserDetails.getUserId()
        financialPlanService.addFinancialPlan(groupId, financialPlanDTO, userId)
    }

    //금전 계획 조회
    @GetMapping("/{groupId}")
    fun getFinancialPlan(
        @PathVariable groupId: String,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ):List<FinancialPlanDTO> {
        val userId=customUserDetails.getUserId()
        return financialPlanService.getFinancialPlan(groupId, userId)
    }

    //금전 계획 수정
    @PutMapping("/{groupId}")
    fun updateFinancialPlan(
        @PathVariable groupId: String,
        @RequestBody financialPlanDTO: FinancialPlanDTO,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ):FinancialPlanDTO {
        val userId=customUserDetails.getUserId()
        return financialPlanService.updateFinancialPlan(groupId, financialPlanDTO, userId)
    }
}
