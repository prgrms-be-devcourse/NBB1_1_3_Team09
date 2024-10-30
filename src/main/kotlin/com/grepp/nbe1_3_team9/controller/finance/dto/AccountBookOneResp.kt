package com.grepp.nbe1_3_team9.controller.finance.dto

import com.grepp.nbe1_3_team9.domain.entity.Expense
import java.math.BigDecimal
import java.time.LocalDateTime

data class AccountBookOneResp(
    val expensesId: Long?,
    val expensesDate: LocalDateTime,
    val itemName: String,
    val amount: String?,
    val paidByUserId: String?,
    var receiptImage: String? = null // 필드는 정의하되, 메서드에서 선택적으로 사용
) {
    companion object {
        fun toEntity(accountBookDTO: AccountBookOneResp): Expense {
            val expense = Expense()

            expense.expenseId = accountBookDTO.expensesId
            expense.expenseDate = accountBookDTO.expensesDate
            expense.itemName = accountBookDTO.itemName
            expense.amount = if (accountBookDTO.amount != null && accountBookDTO.amount.isNotEmpty()) {
                BigDecimal(accountBookDTO.amount)
            } else {
                BigDecimal.ZERO
            }
            expense.paidBy = accountBookDTO.paidByUserId ?: ""

            return expense
        }

        fun toDTO(expense: Expense): AccountBookOneResp {
            return AccountBookOneResp(
                expensesId = expense.expenseId,
                expensesDate = expense.expenseDate,
                itemName = expense.itemName,
                amount = expense.amount.toString(),
                paidByUserId = expense.paidBy
                // receiptImage는 선택적으로 제외 (필요하면 포함 가능)
            )
        }
    }
}
