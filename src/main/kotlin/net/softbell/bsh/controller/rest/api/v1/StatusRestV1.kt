package net.softbell.bsh.controller.rest.api.v1

import io.swagger.annotations.Api
import net.softbell.bsh.dto.response.SingleResultDto
import net.softbell.bsh.service.ResponseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author : Bell(bell@softbell.net)
 * @description : 상태관련 REST API 컨트롤러 V1
 */
@Api(tags = ["0. Status"])
@RestController
@RequestMapping("/api/rest/v1/status")
class StatusRestV1 {
    // Global Field
    @Autowired private lateinit var responseService: ResponseService

    @GetMapping("/server")
    fun checkServer(): SingleResultDto<String> {
        return responseService.getSingleResult("normal")
    }
}