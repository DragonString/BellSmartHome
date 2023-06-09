package net.softbell.bsh.domain.repository

import net.softbell.bsh.domain.entity.NodeConnectionLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author : Bell(bell@softbell.net)
 * @description : 노드 연결 로그 리포지토리 인터페이스
 */
@Repository
interface NodeConnectionLogRepo : JpaRepository<NodeConnectionLog, Long>