package com.grepp.nbe1_3_team9.controller.finance.dto.accountBook

import com.grepp.nbe1_3_team9.domain.entity.finance.Expense
import java.math.BigDecimal
import java.time.LocalDateTime

data class AccountBookReq(
    var expenseDate: LocalDateTime = LocalDateTime.now(),
    val itemName: String = "",
    val amount: String? = null,
    val paidByUserId: String? = null,
    val receiptImage: String? = null,
    var receiptImageByte: ByteArray? = null
) {
    companion object {
        fun toEntity(accountBookReq: AccountBookReq): Expense {
            val expense = Expense()
            expense.expenseDate = accountBookReq.expenseDate
            expense.itemName = accountBookReq.itemName
            expense.amount = if (!accountBookReq.amount.isNullOrEmpty()) {
                BigDecimal(accountBookReq.amount)
            } else {
                BigDecimal.ZERO
            }
            expense.paidBy = accountBookReq.paidByUserId ?: ""
            expense.receiptImage = accountBookReq.receiptImageByte
            return expense
        }

        fun toDTO(expense: Expense): AccountBookReq {
            return AccountBookReq(
                expenseDate = expense.expenseDate,
                itemName = expense.itemName,
                amount = expense.amount.toString(),
                paidByUserId = expense.paidBy,
                receiptImageByte = expense.receiptImage
            )
        }
    }
}
