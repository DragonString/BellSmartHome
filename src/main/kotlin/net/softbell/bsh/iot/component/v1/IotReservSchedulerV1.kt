package net.softbell.bsh.iot.component.v1

import mu.KLogging
import net.softbell.bsh.domain.entity.NodeReserv
import net.softbell.bsh.iot.service.v1.IotActionServiceV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.transaction.Transactional

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : IoT 예약 스케줄러 v1
 */
@Component
class IotReservSchedulerV1 {
    // Global Field
    @Autowired private lateinit var iotReservParser: IotReservParserV1
    @Autowired private lateinit var iotActionService: IotActionServiceV1


    @Scheduled(cron = "0 * * * * *")
    @Transactional
    fun iotReservTask() {
        // Field
        val listNodeReserv: List<NodeReserv?>?

        // Init
        listNodeReserv = iotReservParser!!.getEnableReserv()

        // Process
        for (nodeReserv in listNodeReserv!!) {
            // Field
            var isSuccess: Boolean?

            // Parse
            isSuccess = iotReservParser.parseEntity(nodeReserv!!)

            // Process
            if (isSuccess != null && isSuccess == true) {
                logger.info("예약 실행 (" + nodeReserv.description + ")")
                for (nodeAction in iotReservParser.getReservAction(nodeReserv))  // Get Reserv Action
                    iotActionService!!.execAction(nodeAction, nodeReserv.member) // Exec Action
            }
        }
    }

    companion object : KLogging()
}