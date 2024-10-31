package com.grepp.nbe1_3_team9.controller.finance.dto

import com.grepp.nbe1_3_team9.domain.entity.Expense
import java.math.BigDecimal
import java.time.LocalDateTime

data class AccountBookAllResp(
    val expensesId: Long,
    val expensesDate: LocalDateTime,
    val itemName: String,
    val amount: String,
    val paidByUserId: String
) {
    companion object {
        fun toEntity(accountBookDTO: AccountBookAllResp): Expense {
            val expense: Expense = Expense()

            expense.expenseId=accountBookDTO.expensesId
            expense.expenseDate=accountBookDTO.expensesDate
            expense.itemName=accountBookDTO.itemName
            if (accountBookDTO.amount != null && !accountBookDTO.amount.isEmpty()) {
                expense.amount=BigDecimal(accountBookDTO.amount)
            } else {
                // amount 값이 null인 경우 = 0
                expense.amount=BigDecimal.ZERO
            }
            expense.paidBy=accountBookDTO.paidByUserId

            return expense
        }

        fun toDTO(expense: Expense): AccountBookAllResp {
            return AccountBookAllResp(
                expense.expenseId, expense.expenseDate, expense.itemName, expense.amount.toString(), expense.paidBy
            )
        }
    }
}
