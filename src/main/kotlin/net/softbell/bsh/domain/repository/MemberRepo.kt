package net.softbell.bsh.domain.repository

import net.softbell.bsh.domain.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 회원 리포지토리 인터페이스
 */
@Repository
open interface MemberRepo : JpaRepository<Member?, Long?> {
    fun findByUserId(userId: String?): Member?
}