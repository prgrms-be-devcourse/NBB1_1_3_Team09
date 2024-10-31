package com.grepp.nbe1_3_team9.admin.service

import com.grepp.nbe1_3_team9.domain.entity.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails (
    private val user: User
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role.name))

    override fun getUsername(): String = user.email
    override fun getPassword(): String = user.password

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    // 접근 메서드
    fun getUserId(): Long = user.userId
    fun getUserEmail(): String = user.email
}
