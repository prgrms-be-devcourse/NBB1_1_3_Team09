package com.grepp.nbe1_3_team9.domain.entity.user

import com.grepp.nbe1_3_team9.domain.entity.group.GroupMembership
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_tb")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long = 0L,

    @Column(nullable = false, length = 50)
    var username: String,

    @Column(nullable = false, unique = true, length = 100)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role,

    // 소셜 로그인 제공자
    @Column(nullable = true, length = 50)
    var provider: OAuthProvider? = null,

    // 소셜 제공자에서 유저 식별 ID
    @Column(nullable = true, length = 100)
    var providerId: String? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val signUpDate: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    var lastLoginDate: LocalDateTime? = null,

//    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
//    var groupMemberships: List<GroupMembership> = mutableListOf()
) {
    // 비즈니스 메서드
    fun updateProfile(username: String, email: String) {
        this.username = username
        this.email = email
    }

    fun changePassword(password: String) {
        this.password = password
    }

    // 마지막 로그인 시간 업데이트
    fun updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now()
    }
}