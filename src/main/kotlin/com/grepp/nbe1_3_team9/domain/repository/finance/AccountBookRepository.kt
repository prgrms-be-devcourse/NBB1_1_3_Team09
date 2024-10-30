package com.grepp.nbe1_3_team9.domain.repository.finance

import com.grepp.nbe1_3_team9.domain.entity.Expense
import org.springframework.data.jpa.repository.JpaRepository

interface AccountBookRepository : JpaRepository<Expense, Long> {
    fun findAllByGroup_GroupId(groupId: Long): List<Expense>
}
