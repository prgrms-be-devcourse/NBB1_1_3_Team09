package com.grepp.nbe1_3_team9.controller.finance

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.controller.finance.dto.*
import com.grepp.nbe1_3_team9.controller.finance.dto.accountBook.*
import com.grepp.nbe1_3_team9.domain.service.finance.AccountBookService
import com.grepp.nbe1_3_team9.domain.service.finance.OCRService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/accountBook")
class AccountBookController(
    private val accountBookService: AccountBookService,
    private val ocrService: OCRService
) {

    // 가계부 지출 기록
    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addAccountBook(
        @PathVariable eventId: Long,
        @RequestBody accountBookReq: AccountBookReq,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) {
        val userId = customUserDetails.getUserId()
        accountBookService.addAccountBook(eventId, accountBookReq, userId)
    }

    // 가계부 목록 전체 조회
    @GetMapping("/{eventId}")
    fun findAllAccountBooks(
        @PathVariable eventId: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): List<AccountBookAllResp> {
        val userId = customUserDetails.getUserId()
        return accountBookService.findAllAccountBooks(eventId, userId)
    }

    // 가계부 목록 상세 조회
    @PostMapping
    fun findAccountBook(
        @RequestBody expenseId: Map<String, String>,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): AccountBookOneResp {
        val userId = customUserDetails.getUserId()
        return accountBookService.findAccountBook(expenseId["expenseId"]!!.toLong(), userId)
    }

    // 가계부 지출 기록 수정
    @PutMapping
    fun updateAccountBook(
        @RequestBody updateAccountBookReq: UpdateAccountBookReq,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) {
        val userId = customUserDetails.getUserId()
        accountBookService.updateAccountBook(updateAccountBookReq, userId)
    }

    // 가계부 지출 삭제
    @DeleteMapping
    fun deleteAccountBook(
        @RequestBody expenseId: Map<String, String>,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) {
        val userId = customUserDetails.getUserId()
        accountBookService.deleteAccountBook(expenseId["expenseId"]!!.toLong(), userId)
    }

    @PostMapping("/receipt")
    @Throws(Exception::class)
    fun receiptOCR(@RequestBody image: Map<String?, String?>): Map<Int, String> {
        return ocrService.extractTextFromImage(image["image"])
    }

    // 받은 문자열 포매팅
    @PostMapping("/receipt/formatting")
    fun receiptFormatting(@RequestBody receipt: ReceiptDTO): ReceiptDTO {
        return ocrService.formatting(receipt)
    }
}
