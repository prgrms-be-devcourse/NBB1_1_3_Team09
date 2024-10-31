package com.grepp.nbe1_3_team9.domain.entity.finance

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "financial_plan_tb")
class FinancialPlan (
    @field:Column(nullable = false, length = 100)
    var itemName:String,

    @field:Column(nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var expenseItemId: Long = 0L,
){
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    lateinit var group: Group

    fun updateExpenseItem(itemName: String, amount: BigDecimal) {
        this.itemName = itemName
        this.amount = amount
    }
}