package net.softbell.bsh.domain.repository

import net.softbell.bsh.domain.entity.CenterSetting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author : Bell(bell@softbell.net)
 * @description : 센터 설정 리포지토리 인터페이스
 */
@Repository
interface CenterSettingRepo : JpaRepository<CenterSetting, Byte>