package com.grepp.nbe1_3_team9.domain.entity.finance

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "expense_tb")
class Expense(
    @field:Column(nullable = false, length = 100)
    var itemName: String = "",

    @field:Column(nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal = BigDecimal.ZERO,

    @field:Column(nullable = false)
    var expenseDate: LocalDateTime = LocalDateTime.now(),

    @field:Column(nullable = false, length = 50)
    var paidBy: String = "",

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var expenseId: Long = 0,
) {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    lateinit var group: Group  // 나중에 초기화할 수 있도록 설정

    @Lob
    @Column
    var receiptImage: ByteArray?=null

    fun updateExpenseDetails(itemName: String, amount: BigDecimal, expenseDate: LocalDateTime) {
        this.itemName = itemName
        this.amount = amount
        this.expenseDate = expenseDate
    }

    fun changePaidBy(username: String) {
        this.paidBy = username
    }

    fun attachReceiptImage(receiptImage: ByteArray) {
        this.receiptImage = receiptImage
    }
}
