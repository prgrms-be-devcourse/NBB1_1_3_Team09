package com.grepp.nbe1_3_team9.admin.service

import com.grepp.nbe1_3_team9.admin.dto.CustomUserInfoDTO
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.UserException
import com.grepp.nbe1_3_team9.domain.entity.user.User
import com.grepp.nbe1_3_team9.domain.repository.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    private val log = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    override fun loadUserByUsername(userId: String): UserDetails {
        val user = userRepository.findById(userId.toLong()).orElseThrow {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND)
            UserException(ExceptionMessage.USER_NOT_FOUND)
        }

        return createUserDetails(user)
    }

    private fun createUserDetails(user: User): UserDetails {
        val customUserInfoDTO = CustomUserInfoDTO(
            userId = user.userId,
            username = user.username,
            email = user.email,
            password = user.password,
            role = user.role,
            signUpDate = user.signUpDate
        )

        return CustomUserDetails(customUserInfoDTO)
    }
}