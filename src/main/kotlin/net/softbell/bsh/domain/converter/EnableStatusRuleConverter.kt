package net.softbell.bsh.domain.converter

import net.softbell.bsh.domain.TriggerLastStatusRule
import javax.persistence.AttributeConverter
import javax.persistence.Converter
import kotlin.Throws

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 활성화 상태 타입 자료형 DB 컨버터
 */
@Converter(autoApply = true)
class EnableStatusRuleConverter : AttributeConverter<TriggerLastStatusRule?, Int?> {
    override fun convertToDatabaseColumn(triggerLastStatusRule: TriggerLastStatusRule?): Int? {
        return triggerLastStatusRule?.getCode()
    }

    override fun convertToEntityAttribute(code: Int?): TriggerLastStatusRule? {
        return TriggerLastStatusRule.Companion.ofLegacyCode(code)
    }
}