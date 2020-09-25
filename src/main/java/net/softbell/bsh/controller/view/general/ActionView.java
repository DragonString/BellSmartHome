package net.softbell.bsh.controller.view.general;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import net.softbell.bsh.domain.entity.MemberInterlockToken;
import net.softbell.bsh.domain.entity.NodeAction;
import net.softbell.bsh.domain.entity.NodeActionItem;
import net.softbell.bsh.domain.entity.NodeItem;
import net.softbell.bsh.dto.request.IotActionDto;
import net.softbell.bsh.dto.view.general.ActionInfoCardDto;
import net.softbell.bsh.iot.service.v1.IotActionServiceV1;
import net.softbell.bsh.service.CenterService;
import net.softbell.bsh.service.InterlockService;
import net.softbell.bsh.service.ViewDtoConverterService;

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 모니터 뷰 컨트롤러
 */
@AllArgsConstructor
@Controller
@RequestMapping("/action")
public class ActionView
{
	// Global Field
	private final String G_BASE_PATH = "services/general";
	private final String G_INDEX_REDIRECT_URL = "redirect:/";
	
	private final ViewDtoConverterService viewDtoConverterService;
	private final IotActionServiceV1 iotActionService;
	private final CenterService centerService;
	private final InterlockService interlockService;
	
	@GetMapping()
    public String dispIndex(Model model, Authentication auth)
	{
		// Exception
		if (centerService.getSetting().getIotAction() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		List<NodeAction> listNodeAction;
		
		// Init
		listNodeAction = iotActionService.getAllNodeActions(auth);
		
		// Process
		model.addAttribute("listCardActions", viewDtoConverterService.convActionSummaryCards(listNodeAction));
		
		// Return
        return G_BASE_PATH + "/Action";
    }
	
	@GetMapping("/{id}")
    public String dispAction(Model model, Authentication auth, HttpServletRequest request, @PathVariable("id") long actionId)
	{
		// Exception
		if (centerService.getSetting().getIotAction() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		NodeAction nodeAction;
		List<NodeItem> listNodeItem;
		List<MemberInterlockToken> listMemberInterlockToken;
		String baseUrl;
		
		// Init
		nodeAction = iotActionService.getNodeAction(auth, actionId);
		listNodeItem = iotActionService.getAvailableNodeItem(auth);
		listMemberInterlockToken = interlockService.getAllTokens(auth);
		baseUrl = request.getRequestURL().toString();
		baseUrl = baseUrl.substring(0, baseUrl.indexOf("/", 8));
		
		for (NodeActionItem actionItem : nodeAction.getNodeActionItems())
			listNodeItem.remove(actionItem.getNodeItem());
		
		// Process
		model.addAttribute("cardActionInfo", new ActionInfoCardDto(nodeAction));
		model.addAttribute("listCardInterlocks", viewDtoConverterService.convActionInterlockTokenCards(listMemberInterlockToken));
		model.addAttribute("listCardItems", viewDtoConverterService.convActionItemCards(nodeAction.getNodeActionItems()));
		model.addAttribute("baseURL", baseUrl);
		
		// Return
        return G_BASE_PATH + "/ActionInfo";
    }
	
	@GetMapping("/modify/{id}")
    public String dispActionModify(Model model, Authentication auth, @PathVariable("id") long actionId)
	{
		// Exception
		if (centerService.getSetting().getIotAction() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		NodeAction nodeAction;
		List<NodeItem> listNodeItem;
		
		// Init
		nodeAction = iotActionService.getNodeAction(auth, actionId);
		listNodeItem = iotActionService.getAvailableNodeItem(auth);
		
		for (NodeActionItem actionItem : nodeAction.getNodeActionItems())
			listNodeItem.remove(actionItem.getNodeItem());
		
		// Process
		model.addAttribute("cardActionInfo", new ActionInfoCardDto(nodeAction));
		model.addAttribute("listCardItemActives", viewDtoConverterService.convActionItemCards(nodeAction.getNodeActionItems()));
		model.addAttribute("listCardItems", viewDtoConverterService.convActionItemCards(listNodeItem));
		
		// Return
        return G_BASE_PATH + "/ActionModify";
    }
	
	@GetMapping("/create")
    public String dispCreate(Model model, Authentication auth)
	{
		// Exception
		if (centerService.getSetting().getIotAction() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		List<NodeItem> listNodeItem;
		
		// Init
		listNodeItem = iotActionService.getAvailableNodeItem(auth);
		
		// Process
		model.addAttribute("listCardItems", viewDtoConverterService.convActionItemCards(listNodeItem));
		
		// Return
        return G_BASE_PATH + "/ActionCreate";
    }
	
	@PostMapping("/create")
    public String procCreate(Model model, Authentication auth,
    						IotActionDto iotActionDto)
	{
		// Exception
		if (centerService.getSetting().getIotAction() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		boolean isSuccess;
		
		// Init
		isSuccess = iotActionService.createAction(auth, iotActionDto);
		
		// Return
		if (isSuccess)
			return "redirect:/action";
		else
			return "redirect:/action?error";
    }
	
	@PostMapping("/modify/{id}")
    public String procModify(Model model, Authentication auth,
    						@PathVariable("id") long actionId,
    						IotActionDto iotActionDto)
	{
		// Exception
		if (centerService.getSetting().getIotAction() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		boolean isSuccess;
		
		// Init
		isSuccess = iotActionService.modifyAction(auth, actionId, iotActionDto);
		
		// Return
		if (isSuccess)
			return "redirect:/action";
		else
			return "redirect:/action/modify/" + actionId + "?error";
    }
	
	@PostMapping("/delete/{id}")
    public String procDelete(Model model, Authentication auth, @PathVariable("id") long actionId)
	{
		// Exception
		if (centerService.getSetting().getIotAction() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		boolean isSuccess;
		
		// Init
		isSuccess = iotActionService.deleteAction(auth, actionId);
		
		// Return
		if (isSuccess)
			return "redirect:/action";
		else
			return "redirect:/action/modify/" + actionId + "?error";
    }
}