package com.grepp.nbe1_3_team9.domain.service.finance

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.AccountBookException
import com.grepp.nbe1_3_team9.common.exception.exceptions.FinancialPlanException
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.AddFinancialPlanReq
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.DeleteFinancialPlanReq
import com.grepp.nbe1_3_team9.controller.finance.dto.financialPlan.FinancialPlanDTO
import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.finance.FinancialPlan
import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.user.User
import com.grepp.nbe1_3_team9.domain.repository.event.eventrepo.EventRepository
import com.grepp.nbe1_3_team9.domain.repository.finance.FinancialPlanRepository
import com.grepp.nbe1_3_team9.domain.repository.group.GroupRepository
import com.grepp.nbe1_3_team9.domain.repository.group.membership.GroupMembershipRepository
import com.grepp.nbe1_3_team9.domain.repository.user.UserRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class FinancialPlanService (
    private val financialPlanRepository: FinancialPlanRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val groupMembershipRepository: GroupMembershipRepository,
    private val em: EntityManager,
    private val eventRepository: EventRepository,
){
    @Transactional
    fun addFinancialPlan(eventId: Long, financialPlanDTO: AddFinancialPlanReq, userId: Long) {
        val event: Event = try {
            eventRepository.findByEventId(eventId);
        }catch (e:Exception){
            throw AccountBookException(ExceptionMessage.EVENT_NOT_FOUND);
        }

        checkUserInGroup(event.group.groupId, userId)

        val financialPlan:FinancialPlan = AddFinancialPlanReq.toEntity(financialPlanDTO)

        financialPlan.event=event
        val result=financialPlanRepository.save(financialPlan)

        if(result.itemName!=financialPlan.itemName){
            throw FinancialPlanException(ExceptionMessage.DB_ERROR)
        }
    }

    @Transactional
    fun getFinancialPlan(eventId: Long, userId: Long):MutableList<FinancialPlanDTO> {
        val event:Event = try {
            eventRepository.findByEventId(eventId);
        }catch (e:Exception){
            throw AccountBookException(ExceptionMessage.EVENT_NOT_FOUND);
        }

        checkUserInGroup(event.group.groupId, userId)

        val financialPlanList = financialPlanRepository.findAllByEvent_EventId(eventId)

        return financialPlanList.map { FinancialPlanDTO.toDTO(it) }.toMutableList()
    }

    @Transactional
    fun updateFinancialPlan(eventId:Long, financialPlanDTO: FinancialPlanDTO, userId: Long): FinancialPlanDTO {
        val event:Event = try {
            eventRepository.findByEventId(eventId);
        }catch (e:Exception){
            throw AccountBookException(ExceptionMessage.EVENT_NOT_FOUND);
        }
        checkUserInGroup(event.group.groupId, userId)

        val financialPlan=em.find(FinancialPlan::class.java, financialPlanDTO.financialPlanId)
        financialPlan.updateExpenseItem(financialPlanDTO.itemName, BigDecimal(financialPlanDTO.amount))

        val result: Optional<FinancialPlan> =financialPlanRepository.findById(financialPlanDTO.financialPlanId)
        if (result.get().itemName != financialPlanDTO.itemName ||
            result.get().amount.compareTo(BigDecimal(financialPlanDTO.amount)) != 0) {
            throw FinancialPlanException(ExceptionMessage.DB_ERROR)
        }
        return FinancialPlanDTO.toDTO(result.get())
    }

    @Transactional
    fun deleteFinancialPlan(groupId: Long, deleteFinancialPlanReq: DeleteFinancialPlanReq, userId: Long) {
        checkUserInGroup(groupId, userId)

        try {
            financialPlanRepository.deleteById(deleteFinancialPlanReq.financialPlanId)
        }catch (e:Exception){
            throw FinancialPlanException(ExceptionMessage.DB_ERROR)
        }
    }


    @Transactional
    fun getFinancialPlanItems(groupId: Long) :List<String>{
        val group=groupRepository.findById(groupId)
        if(!group.isPresent){
            throw FinancialPlanException(ExceptionMessage.GROUP_NOT_FOUND)
        }

        return financialPlanRepository.findFinancialPlanItems(group.get())
    }

    private fun checkUserInGroup(groupId: Long, userId: Long) {
        val group: Group = groupRepository.findById(groupId)
            .orElseThrow {
                //log.warn(">>>> {} : {} <<<<", groupId, AccountBookException(ExceptionMessage.GROUP_NOT_FOUND))
                AccountBookException(ExceptionMessage.GROUP_NOT_FOUND)
            }

        val user: User = userRepository.findById(userId)
            .orElseThrow {
                //log.warn(">>>> {} : {} <<<<", userId, AccountBookException(ExceptionMessage.USER_NOT_FOUND))
                AccountBookException(ExceptionMessage.USER_NOT_FOUND)
            }

        val isMember: Boolean = groupMembershipRepository.existsByGroupAndUser(group, user)

        if (!isMember) {
            //log.warn(">>>> {} : {} <<<<", user.id, AccountBookException(ExceptionMessage.MEMBER_ACCESS_ONLY))
            throw AccountBookException(ExceptionMessage.MEMBER_ACCESS_ONLY)
        }
    }
}
