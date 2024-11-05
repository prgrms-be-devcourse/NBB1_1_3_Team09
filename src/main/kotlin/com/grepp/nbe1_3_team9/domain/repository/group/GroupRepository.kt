package com.grepp.nbe1_3_team9.domain.repository.group

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Long>