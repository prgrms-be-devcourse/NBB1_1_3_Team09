package com.grepp.nbe1_3_team9.controller.finance.dto

import com.grepp.nbe1_3_team9.domain.entity.Expense
import java.math.BigDecimal
import java.time.LocalDateTime

data class UpdateAccountBookReq(
    var expenseId: Long? = 0L,
    var expenseDate: LocalDateTime = LocalDateTime.now(),
    var itemName: String = "",
    var amount: BigDecimal = BigDecimal.ZERO,
    var paidByUserId: String? = null,
    var receiptImage: String? = null,
    var receiptImageByte: ByteArray? = null
) {
    companion object {
        fun toEntity(accountBookReq: UpdateAccountBookReq): Expense {
            val expense = Expense()
            expense.expenseId = accountBookReq.expenseId
            expense.expenseDate = accountBookReq.expenseDate
            expense.itemName = accountBookReq.itemName
            expense.amount = accountBookReq.amount
            expense.paidBy = accountBookReq.paidByUserId ?: ""
            expense.receiptImage = accountBookReq.receiptImageByte
            return expense
        }

        fun toDTO(expense: Expense): UpdateAccountBookReq {
            return UpdateAccountBookReq(
                expenseId = expense.expenseId,
                expenseDate = expense.expenseDate,
                itemName = expense.itemName,
                amount = expense.amount,
                paidByUserId = expense.paidBy,
                receiptImageByte = expense.receiptImage ?: ByteArray(0)
            )
        }
    }
}
