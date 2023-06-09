package net.softbell.bsh.domain.repository

import net.softbell.bsh.domain.GroupRole
import net.softbell.bsh.domain.entity.GroupPermission
import net.softbell.bsh.domain.entity.MemberGroup
import net.softbell.bsh.domain.entity.NodeGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author : Bell(bell@softbell.net)
 * @description : 그룹 권한 리포지토리 인터페이스
 */
@Repository
interface GroupPermissionRepo : JpaRepository<GroupPermission, Long> {
    fun findByGroupPermissionAndMemberGroupIn(role: GroupRole, listMemberGroup: List<MemberGroup>): List<GroupPermission>
    fun findByGroupPermissionAndNodeGroupIn(role: GroupRole, listNodeGroup: List<NodeGroup>): List<GroupPermission>
    fun findByGroupPermissionAndMemberGroupInAndNodeGroupIn(role: GroupRole, listMemberGroup: List<MemberGroup>, listNodeGroup: List<NodeGroup>): List<GroupPermission>
}