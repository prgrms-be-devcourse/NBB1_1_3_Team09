package com.grepp.nbe1_3_team9.controller.finance

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.AddFinancialPlanReq
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.DeleteFinancialPlanReq
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.FinancialPlanDTO
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
    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addFinancialPlan(
        @PathVariable eventId: Long,
        @RequestBody financialPlanDTO: AddFinancialPlanReq,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) {
        val userId=customUserDetails.getUserId()
        financialPlanService.addFinancialPlan(eventId, financialPlanDTO, userId)
    }

    //금전 계획 조회
    @GetMapping("/{eventId}")
    fun getFinancialPlan(
        @PathVariable eventId: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ):List<FinancialPlanDTO> {
        val userId=customUserDetails.getUserId()
        return financialPlanService.getFinancialPlan(eventId, userId)
    }

    //금전 계획 수정
    @PutMapping("/{eventId}")
    fun updateFinancialPlan(
        @PathVariable eventId: Long,
        @RequestBody financialPlanDTO: FinancialPlanDTO,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ):FinancialPlanDTO {
        val userId=customUserDetails.getUserId()
        return financialPlanService.updateFinancialPlan(eventId, financialPlanDTO, userId)
    }

    @DeleteMapping("/{eventId}")
    fun deleteFinancialPlan(
        @PathVariable eventId: Long,
        @RequestBody deleteFinancialPlanReq: DeleteFinancialPlanReq,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ){
        val userId=customUserDetails.getUserId()
        financialPlanService.deleteFinancialPlan(eventId, deleteFinancialPlanReq, userId)
    }

    //설정한 항목들 전체 전송
    @GetMapping("/{eventId}/items")
    fun getFinancialPlanItems(
        @PathVariable eventId: Long,
    ):List<String>{
        return financialPlanService.getFinancialPlanItems(eventId)
    }
}
