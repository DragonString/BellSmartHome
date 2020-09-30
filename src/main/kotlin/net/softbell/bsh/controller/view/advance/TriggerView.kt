package net.softbell.bsh.controller.view.advance

import net.softbell.bsh.domain.TriggerStatusRule
import net.softbell.bsh.domain.entity.NodeAction
import net.softbell.bsh.domain.entity.NodeTrigger
import net.softbell.bsh.dto.request.IotTriggerDto
import net.softbell.bsh.dto.view.advance.TriggerInfoCardDto
import net.softbell.bsh.dto.view.general.ActionSummaryCardDto
import net.softbell.bsh.iot.service.v1.IotActionServiceV1
import net.softbell.bsh.iot.service.v1.IotTriggerServiceV1
import net.softbell.bsh.service.CenterService
import net.softbell.bsh.service.ViewDtoConverterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 트리거 뷰 컨트롤러
 */
@Controller
@RequestMapping("/trigger")
class TriggerView {
    // Global Field
    private val G_BASE_PATH: String = "services/advance"
    private val G_INDEX_REDIRECT_URL: String = "redirect:/"

    @Autowired private lateinit var viewDtoConverterService: ViewDtoConverterService
    @Autowired private lateinit var iotTriggerService: IotTriggerServiceV1
    @Autowired private lateinit var iotActionService: IotActionServiceV1
    @Autowired private lateinit var centerService: CenterService

    @GetMapping
    fun dispIndex(model: Model, auth: Authentication): String? {
        // Exception
        if (centerService.getSetting().iotTrigger!!.toInt() != 1) return G_INDEX_REDIRECT_URL

        // Field
        val listTrigger: List<NodeTrigger?>?

        // Init
        listTrigger = iotTriggerService.getAllTriggers(auth)

        // Process
        model.addAttribute("listCardTriggers", viewDtoConverterService.convTriggerSummaryCards(listTrigger!!))

        // Return
        return "$G_BASE_PATH/Trigger"
    }

    @GetMapping("/create")
    fun dispCreate(model: Model, auth: Authentication): String? {
        // Exception
        if (centerService.getSetting().iotTrigger!!.toInt() != 1) return G_INDEX_REDIRECT_URL

        // Field
        val listNodeAction: List<NodeAction?>?

        // Init
        listNodeAction = iotActionService.getAllNodeActions(auth)

        // Process
        model.addAttribute("listCardActions", viewDtoConverterService.convActionSummaryCards(listNodeAction!!))
        //model.addAttribute("listCardNodeItems", viewDtoConverterService.convTriggerItemCards(iotNodeService.getAllNodeItems(auth)));

        // Return
        return "$G_BASE_PATH/TriggerCreate"
    }

    @GetMapping("/{id}")
    fun dispTrigger(model: Model, auth: Authentication, @PathVariable("id") triggerId: Long): String? {
        // Exception
        if (centerService.getSetting().iotTrigger!!.toInt() != 1) return G_INDEX_REDIRECT_URL

        // Field
        val nodeTrigger: NodeTrigger?
        val listCardActionsAll: MutableList<ActionSummaryCardDto>
        val listCardActionsOccurAndRestore: MutableList<ActionSummaryCardDto>
        val listCardActionsOccur: MutableList<ActionSummaryCardDto>
        val listCardActionsRestore: MutableList<ActionSummaryCardDto>
        val listCardActionsError: MutableList<ActionSummaryCardDto>

        // Init
        nodeTrigger = iotTriggerService.getTrigger(auth, triggerId)
        listCardActionsAll = ArrayList()
        listCardActionsOccurAndRestore = ArrayList()
        listCardActionsOccur = ArrayList()
        listCardActionsRestore = ArrayList()
        listCardActionsError = ArrayList()

        // Exception
        if (nodeTrigger == null) return "redirect:/trigger?err"
        for (entity in nodeTrigger.nodeTriggerActions!!) {
            when (entity.triggerStatus) {
                TriggerStatusRule.ALL -> listCardActionsAll.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.OCCUR_AND_RESTORE -> listCardActionsOccurAndRestore.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.OCCUR -> listCardActionsOccur.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.RESTORE -> listCardActionsRestore.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.ERROR -> listCardActionsError.add(ActionSummaryCardDto(entity.nodeAction))
            }
        }

        // Process
        model.addAttribute("cardTriggerInfo", TriggerInfoCardDto(nodeTrigger))
        model.addAttribute("listCardActionsAll", listCardActionsAll)
        model.addAttribute("listCardActionsOccurAndRestore", listCardActionsOccurAndRestore)
        model.addAttribute("listCardActionsOccur", listCardActionsOccur)
        model.addAttribute("listCardActionsRestore", listCardActionsRestore)
        model.addAttribute("listCardActionsError", listCardActionsError)

        // Return
        return "$G_BASE_PATH/TriggerInfo"
    }

    @GetMapping("/modify/{id}")
    fun dispTriggerModify(model: Model, auth: Authentication, @PathVariable("id") triggerId: Long): String? {
        // Exception
        if (centerService.getSetting().iotTrigger!!.toInt() != 1) return G_INDEX_REDIRECT_URL

        // Field
        val nodeTrigger: NodeTrigger?
        val listNodeAction: MutableList<NodeAction?>
        val listCardActionsAll: MutableList<ActionSummaryCardDto>
        val listCardActionsOccurAndRestore: MutableList<ActionSummaryCardDto>
        val listCardActionsOccur: MutableList<ActionSummaryCardDto>
        val listCardActionsRestore: MutableList<ActionSummaryCardDto>
        val listCardActionsError: MutableList<ActionSummaryCardDto>

        // Init
        nodeTrigger = iotTriggerService.getTrigger(auth, triggerId)
        listNodeAction = iotActionService.getAllNodeActions(auth) as MutableList<NodeAction?>
        listCardActionsAll = ArrayList()
        listCardActionsOccurAndRestore = ArrayList()
        listCardActionsOccur = ArrayList()
        listCardActionsRestore = ArrayList()
        listCardActionsError = ArrayList()

        // Exception
        if (nodeTrigger == null) return "redirect:/trigger?err"
        for (entity in nodeTrigger.nodeTriggerActions!!) {
            when (entity.triggerStatus) {
                TriggerStatusRule.ALL -> listCardActionsAll.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.OCCUR_AND_RESTORE -> listCardActionsOccurAndRestore.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.OCCUR -> listCardActionsOccur.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.RESTORE -> listCardActionsRestore.add(ActionSummaryCardDto(entity.nodeAction))
                TriggerStatusRule.ERROR -> listCardActionsError.add(ActionSummaryCardDto(entity.nodeAction))
            }
            listNodeAction.remove(entity.nodeAction)
        }

        // Process
        model.addAttribute("cardTriggerInfo", TriggerInfoCardDto(nodeTrigger))
        model.addAttribute("listCardActionsAll", listCardActionsAll)
        model.addAttribute("listCardActionsOccurAndRestore", listCardActionsOccurAndRestore)
        model.addAttribute("listCardActionsOccur", listCardActionsOccur)
        model.addAttribute("listCardActionsRestore", listCardActionsRestore)
        model.addAttribute("listCardActionsError", listCardActionsError)
        model.addAttribute("listCardActions", viewDtoConverterService.convActionSummaryCards(listNodeAction))

        // Return
        return "$G_BASE_PATH/TriggerModify"
    }

    @PostMapping("/create")
    fun procCreate(auth: Authentication, iotTriggerDto: IotTriggerDto): String? {
        // Exception
        if (centerService.getSetting().iotTrigger!!.toInt() != 1) return G_INDEX_REDIRECT_URL

        // Field
        val isSuccess: Boolean

        // Process
        isSuccess = iotTriggerService.createTrigger(auth, iotTriggerDto)

        // Return
        return if (isSuccess) "redirect:/trigger" else "redirect:/trigger?err"
    }

    @PostMapping("/modify/{id}")
    fun procModify(auth: Authentication, @PathVariable("id") triggerId: Long, iotTriggerDto: IotTriggerDto): String? {
        // Exception
        if (centerService.getSetting().iotTrigger!!.toInt() != 1) return G_INDEX_REDIRECT_URL

        // Field
        val isSuccess: Boolean

        // Process
        isSuccess = iotTriggerService.modifyTrigger(auth, triggerId, iotTriggerDto)

        // Return
        return if (isSuccess) "redirect:/trigger/$triggerId" else "redirect:/trigger/$triggerId?err"
    }

    @PostMapping("/delete/{id}")
    fun procDelete(auth: Authentication, @PathVariable("id") triggerId: Long): String? {
        // Exception
        if (centerService.getSetting().iotTrigger!!.toInt() != 1) return G_INDEX_REDIRECT_URL

        // Field
        val isSuccess: Boolean

        // Process
        isSuccess = iotTriggerService.deleteTrigger(auth, triggerId)

        // Return
        return if (isSuccess) "redirect:/trigger" else "redirect:/trigger?err"
    }
}