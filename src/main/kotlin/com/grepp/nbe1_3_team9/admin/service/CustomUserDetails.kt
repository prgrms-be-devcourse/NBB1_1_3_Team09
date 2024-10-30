package com.grepp.nbe1_3_team9.admin.service

import com.grepp.nbe1_3_team9.admin.dto.CustomUserInfoDTO
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val user: CustomUserInfoDTO
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val roles = listOf(user.role.toString())
        return roles.map { SimpleGrantedAuthority(it) }
    }

    override fun isAccountNonExpired(): Boolean {
        return true // 기본적으로 true로 설정
    }

    override fun isAccountNonLocked(): Boolean {
        return true // 기본적으로 true로 설정
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true // 기본적으로 true로 설정
    }

    override fun isEnabled(): Boolean {
        return true // 기본적으로 true로 설정
    }

    override fun getPassword(): String {
        return user.password ?: ""
    }

    override fun getUsername(): String {
        return user.userId.toString()
    }
}