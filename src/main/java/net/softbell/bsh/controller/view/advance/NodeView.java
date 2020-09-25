package net.softbell.bsh.controller.view.advance;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;
import net.softbell.bsh.domain.GroupRole;
import net.softbell.bsh.domain.entity.Node;
import net.softbell.bsh.domain.entity.NodeItem;
import net.softbell.bsh.domain.entity.NodeItemHistory;
import net.softbell.bsh.dto.view.advance.NodeInfoCardDto;
import net.softbell.bsh.dto.view.advance.NodeItemHistoryCardDto;
import net.softbell.bsh.iot.service.v1.IotNodeServiceV1;
import net.softbell.bsh.service.CenterService;
import net.softbell.bsh.service.ViewDtoConverterService;

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 노드 뷰 컨트롤러
 */
@AllArgsConstructor
@Controller
@RequestMapping("/node")
public class NodeView
{
	// Global Field
	private final String G_BASE_PATH = "services/advance";
	private final String G_INDEX_REDIRECT_URL = "redirect:/";
	
	private final ViewDtoConverterService viewDtoConverterService;
	private final IotNodeServiceV1 iotNodeService;
	private final CenterService centerService;
	
	@GetMapping()
    public String dispIndex(Model model, Authentication auth,
    						@RequestParam(value = "page", required = false, defaultValue = "1")int intPage,
    						@RequestParam(value = "count", required = false, defaultValue = "100")int intCount)
	{
		// Exception
		if (centerService.getSetting().getIotNode() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		List<Node> listNode;
		
		// Init
		listNode = iotNodeService.getAllNodes(auth, GroupRole.MANUAL_CONTROL);
		
		// Process
		model.addAttribute("listCardNodes", viewDtoConverterService.convNodeSummaryCards(listNode));
		
		// Return
        return G_BASE_PATH + "/NodeList";
    }
	
	@GetMapping("/{id}")
    public String dispNode(Model model, @PathVariable("id")int intNodeId)
	{
		// Exception
		if (centerService.getSetting().getIotNode() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		Node node;
		
		// Init
		node = iotNodeService.getNode(intNodeId);
		
		// Process
		model.addAttribute("cardNodeInfo", new NodeInfoCardDto(node));
		model.addAttribute("listCardNodeItems", viewDtoConverterService.convNodeItemCards(node.getNodeItems()));
		
		// Return
        return G_BASE_PATH + "/NodeInfo";
    }
	
	@GetMapping("/item/{id}")
    public String dispNodeItemHistory(Model model, @PathVariable("id")int intNodeItemId)
	{
		// Exception
		if (centerService.getSetting().getIotNode() != 1)
			return G_INDEX_REDIRECT_URL;
		
		// Field
		NodeItem nodeItem;
		List<NodeItemHistory> listNodeItemHistory;
		
		// Init
		nodeItem = iotNodeService.getNodeItem(intNodeItemId);
		listNodeItemHistory = iotNodeService.getNodeItemHistory(intNodeItemId);
		
		// Process
		model.addAttribute("cardNodeItemHistory", new NodeItemHistoryCardDto(nodeItem, listNodeItemHistory));
		
		// Return
        return G_BASE_PATH + "/NodeItemInfo";
    }
}