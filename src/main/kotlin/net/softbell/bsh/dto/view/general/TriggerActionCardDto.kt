package net.softbell.bsh.dto.view.general

import lombok.Getter
import lombok.Setter
import net.softbell.bsh.domain.entity.NodeAction
import kotlin.Throws

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 트리거 등록 및 수정뷰 액션 카드정보 DTO
 */
@Getter
@Setter
class TriggerActionCardDto(entity: NodeAction?) {
    private val actionId: Long
    private val description: String

    init {
        // Exception
        if (entity == null) return

        // Convert
        actionId = entity.getActionId()
        description = entity.getDescription()
    }
}