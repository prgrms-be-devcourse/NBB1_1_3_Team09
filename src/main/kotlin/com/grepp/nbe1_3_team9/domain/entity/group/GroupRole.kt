package com.grepp.nbe1_3_team9.domain.entity.group

enum class GroupRole(val priority: Int) {
    OWNER(3),
    ADMIN(2),
    MEMBER(1);
}
