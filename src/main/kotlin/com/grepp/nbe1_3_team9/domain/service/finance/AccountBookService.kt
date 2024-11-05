package com.grepp.nbe1_3_team9.domain.service.finance

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.AccountBookException
import com.grepp.nbe1_3_team9.controller.finance.dto.accountBook.AccountBookAllResp
import com.grepp.nbe1_3_team9.controller.finance.dto.accountBook.AccountBookOneResp
import com.grepp.nbe1_3_team9.controller.finance.dto.accountBook.AccountBookReq
import com.grepp.nbe1_3_team9.controller.finance.dto.accountBook.UpdateAccountBookReq
import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.finance.Expense
import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.user.User
import com.grepp.nbe1_3_team9.domain.repository.event.eventrepo.EventRepository
import com.grepp.nbe1_3_team9.domain.repository.finance.AccountBookRepository
import com.grepp.nbe1_3_team9.domain.repository.group.GroupRepository
import com.grepp.nbe1_3_team9.domain.repository.group.membership.GroupMembershipRepository
import com.grepp.nbe1_3_team9.domain.repository.user.UserRepository
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountBookService(
    private val accountBookRepository: AccountBookRepository,
    private val groupRepository: GroupRepository,
    private val groupMembershipRepository: GroupMembershipRepository,
    private val em: EntityManager,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
) {
    //가계부 지출 기록
    fun addAccountBook(eventId: Long, accountBookReq: AccountBookReq, user: String) {
        if (accountBookReq.receiptImage != null) {
            val fileData: ByteArray = Base64.getDecoder().decode(accountBookReq.receiptImage)
            accountBookReq.receiptImageByte=fileData
        }

        val event: Event = try {
            eventRepository.findByEventId(eventId);
        }catch (e:Exception){
            throw AccountBookException(ExceptionMessage.EVENT_NOT_FOUND);
        }

        val expense: Expense = AccountBookReq.toEntity(accountBookReq)

        val userId = user.toLong()
        checkUserInGroup(event.group.groupId, userId)

        expense.event=event
        try {
            accountBookRepository.save(expense)
        } catch (e: Exception) {
            throw AccountBookException(ExceptionMessage.DB_ERROR)
        }
    }

    //가계부 목록 전체 조회
    fun findAllAccountBooks(eventId: Long, user: String): MutableList<AccountBookAllResp> {
        val userId = user.toLong()

        val event:Event = try {
            eventRepository.findByEventId(eventId);
        }catch (e:Exception){
            throw AccountBookException(ExceptionMessage.EVENT_NOT_FOUND);
        }

        checkUserInGroup(event.group.groupId, userId)

        val expenses: List<Expense> = accountBookRepository.findAllByEvent_eventId(eventId)

        return expenses.map { AccountBookAllResp.toDTO(it) }.toMutableList()
    }

    //가계부 목록 상세 조회
    @Transactional
    fun findAccountBook(expenseId: Long, user: String): AccountBookOneResp {
        val expense: Expense? = accountBookRepository.findById(expenseId)
            .orElseThrow {
//                log.warn(">>>> {} : {} <<<<", expenseId, ExceptionMessage.EXPENSE_NOT_FOUND)
                AccountBookException(ExceptionMessage.EXPENSE_NOT_FOUND)
            }

        if(expense==null){
            throw AccountBookException(ExceptionMessage.EXPENSE_NOT_FOUND)
        }

        val eventId: Long = accountBookRepository.findById(expenseId).get().event.eventId
        val userId = user.toLong()

        val event:Event = try {
            eventRepository.findByEventId(eventId);
        }catch (e:Exception){
            throw AccountBookException(ExceptionMessage.EVENT_NOT_FOUND);
        }
        checkUserInGroup(event.group.groupId, userId)

        val accountBookOneResp: AccountBookOneResp = AccountBookOneResp.toDTO(expense)

        var image: String? = null
        if (expense.receiptImage != null) {
            val receiptImageByteArray = expense.receiptImage
            image = Base64.getEncoder().encodeToString(receiptImageByteArray)
        }

        accountBookOneResp.receiptImage=image
        return accountBookOneResp
    }

    //가계부 지출 수정
    @Transactional
    fun updateAccountBook(updateAccountBookReq: UpdateAccountBookReq, user: String) {
        try {
            updateAccountBookReq.expenseId.let {
                accountBookRepository.findById(it)
            }
        } catch (e: Exception) {
            throw AccountBookException(ExceptionMessage.EXPENSE_NOT_FOUND)
        }

        val event:Event = try {
            accountBookRepository.findById(updateAccountBookReq.expenseId).get().event;
        }catch (e:Exception){
            throw AccountBookException(ExceptionMessage.EVENT_NOT_FOUND);
        }
        val userId = user.toLong()
        checkUserInGroup(event.group.groupId, userId)

//        val expense: Expense = em.find<T>(Expense::class.java, updateAccountBookReq.getExpenseId())
        val expense: Expense? = em.find(Expense::class.java, updateAccountBookReq.expenseId)


        if (expense != null) {
            expense.expenseDate= updateAccountBookReq.expenseDate
        }
        if (expense != null) {
            expense.itemName=updateAccountBookReq.itemName
        }
        if (expense != null) {
            expense.amount=updateAccountBookReq.amount
        }
        if (expense != null) {
            expense.paidBy= updateAccountBookReq.paidByUserId.toString()
        }

        if (updateAccountBookReq.receiptImage != null) {
            val fileData: ByteArray = Base64.getDecoder().decode(updateAccountBookReq.receiptImage)
            updateAccountBookReq.receiptImageByte=fileData
        }
        if (expense != null) {
            expense.receiptImage=updateAccountBookReq.receiptImageByte
        }
    }

    //가계부 지출 삭제
    @Transactional
    fun deleteAccountBook(expenseId: Long?, user: String) {
        try {
            if (expenseId != null) {
                accountBookRepository.findById(expenseId)
            }
        } catch (e: Exception) {
            throw AccountBookException(ExceptionMessage.EXPENSE_NOT_FOUND)
        }

        val groupId: Long? = accountBookRepository.findById(expenseId!!).get().group.groupId
        val userId = user.toLong()
        if (groupId != null) {
            checkUserInGroup(groupId, userId)
        }

        accountBookRepository.deleteById(expenseId)
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
