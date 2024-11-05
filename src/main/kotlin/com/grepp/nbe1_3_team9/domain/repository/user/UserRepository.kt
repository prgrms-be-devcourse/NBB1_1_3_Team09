package com.grepp.nbe1_3_team9.domain.repository.user

import com.grepp.nbe1_3_team9.domain.entity.user.User
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    // 이메일로 사용자 조회
//    @Cacheable("userByEmail")
    fun findByEmail(email: String): Optional<User>

    // 제공자ID로 사용자 조회
    fun findByProviderId(providerId: String): User?

    // 회원가입 시 이메일 중복확인
    fun existsByEmail(email: String): Boolean

    @Query("SELECT u.username FROM User u WHERE u.userId = :userId")
    fun findUsernameById(@Param("userId") userId: Long): Optional<String>

    fun findByUsername(@Param("username") username: String): Optional<User>
}
