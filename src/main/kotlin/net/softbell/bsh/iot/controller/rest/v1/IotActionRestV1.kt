package net.softbell.bsh.iot.controller.rest.v1

import net.softbell.bsh.domain.entity.Member
import net.softbell.bsh.dto.response.ResultDto
import net.softbell.bsh.iot.service.v1.IotActionServiceV1
import net.softbell.bsh.service.ResponseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author : Bell(bell@softbell.net)
 * @description : IoT 액션 REST API 컨트롤러 V1
 */
@RestController
@RequestMapping("/api/rest/v1/iot/action")
class IotActionRestV1 {
    @Autowired private lateinit var responseService: ResponseService
    @Autowired private lateinit var iotActionService: IotActionServiceV1

    @PostMapping("/exec/{id}")
    fun execNodeAction(@AuthenticationPrincipal member: Member, @PathVariable("id") actionId: Long): ResultDto {
        // Init
        val isSuccess = iotActionService.execPrivilegesAction(actionId, member)

        // Return
        return if (isSuccess)
            responseService.getSuccessResult()
        else
            responseService.getFailResult(-10, "해당하는 아이템이 없음")
    }
}