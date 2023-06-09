package net.softbell.bsh.domain.converter

import net.softbell.bsh.domain.GroupRole
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * @author : Bell(bell@softbell.net)
 * @description : 그룹 권한 타입 자료형 DB 컨버터
 */
@Converter(autoApply = true)
class GroupRoleConverter : AttributeConverter<GroupRole, Int> {
    override fun convertToDatabaseColumn(groupRole: GroupRole): Int {
        return groupRole.code
    }

    override fun convertToEntityAttribute(code: Int): GroupRole? {
        return GroupRole.ofLegacyCode(code)
    }
}