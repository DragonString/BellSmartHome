package net.softbell.bsh.iot.service.v1;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import net.softbell.bsh.domain.EnableStatusRule;
import net.softbell.bsh.domain.entity.Node;
import net.softbell.bsh.domain.entity.NodeItem;
import net.softbell.bsh.domain.entity.NodeItemHistory;
import net.softbell.bsh.domain.repository.NodeItemHistoryRepo;
import net.softbell.bsh.domain.repository.NodeItemRepo;
import net.softbell.bsh.domain.repository.NodeRepo;
import net.softbell.bsh.iot.component.v1.IotChannelCompV1;
import net.softbell.bsh.iot.dto.bshp.v1.BaseV1Dto;
import net.softbell.bsh.iot.dto.bshp.v1.ItemValueV1Dto;
import net.softbell.bsh.service.PermissionService;

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : IoT Node 서비스
 */
@AllArgsConstructor
@Service
public class IotNodeServiceV1
{
	// Global Field
	private final IotMessageServiceV1 iotMessageService;
	private final PermissionService permissionService;
	
	private final IotChannelCompV1 iotChannelCompV1;
	
	private final NodeRepo nodeRepo;
	private final NodeItemRepo nodeItemRepo;
	private final NodeItemHistoryRepo nodeItemHistoryRepo;
	
	@Deprecated
	public List<Node> getAllNodes()
	{
		return nodeRepo.findAll();
	}
	
	@Deprecated
	public Page<Node> getAllNodes(int intPage, int intCount)
	{
		// Field
		Pageable curPage;
		
		// Init
		curPage = PageRequest.of(intPage - 1, intCount);
		
		// Return
		return nodeRepo.findAll(curPage);
	}

	@Deprecated
	public List<NodeItem> getAllNodeItems(Authentication auth)
	{
		// Field
		// TODO 회원 권한에 맞는 아이템만 조회하는 기능 추가 필요... 언젠가는... 하겠지.... 권한... 보안... 으어...
		
		// Init
		
		
		// Return
		return nodeItemRepo.findAll();
	}
	
	public Node getNode(long nodeId)
	{
		// Field
		Optional<Node> optNode;
		
		// Init
		optNode = nodeRepo.findById(nodeId);
		
		// Return
		if (optNode.isPresent())
			return optNode.get();
		else
			return null;
	}
	
	public NodeItem getNodeItem(long itemId)
	{
		// Field
		Optional<NodeItem> optNodeItem;
		
		// Init
		optNodeItem = nodeItemRepo.findById(itemId);
		
		// Return
		if (optNodeItem.isPresent())
			return optNodeItem.get();
		else
			return null;
	}
	
	public NodeItemHistory getLastNodeItemHistory(NodeItem nodeItem)
	{
		// Process
		return nodeItemHistoryRepo.findFirstByNodeItemOrderByItemHistoryIdDesc(nodeItem);
	}
	
	public NodeItemHistory getLastNodeItemHistory(long nodeItemId)
	{
		// Field
		Optional<NodeItem> optNodeItem;
		
		// Init
		optNodeItem = nodeItemRepo.findById(nodeItemId);
		
		// Exception
		if (!optNodeItem.isPresent())
			return null;
		
		// Process
		return nodeItemHistoryRepo.findFirstByNodeItemOrderByItemHistoryIdDesc(optNodeItem.get());
	}
	
	public List<NodeItemHistory> getNodeItemHistory(long nodeItemId)
	{
		// Field
		List<NodeItemHistory> listNodeItemHistory;
		Pageable curPage;
		Optional<NodeItem> optNodeItem;
		
		// Init
		optNodeItem = nodeItemRepo.findById(nodeItemId);
		
		// Exception
		if (!optNodeItem.isPresent())
			return null;
		
		// Process
		curPage = PageRequest.of(0, 50, new Sort(Direction.DESC, "itemHistoryId")); // TODO 임시로 50개 뽑게 설정함
		listNodeItemHistory = nodeItemHistoryRepo.findByNodeItem(optNodeItem.get(), curPage);
		Collections.reverse(listNodeItemHistory);
		
		// Return
		return listNodeItemHistory;
	}
	
	public boolean setItemValue(long itemId, double itemValue)
	{
		// Field
		Optional<NodeItem> optNodeItem;
		BaseV1Dto baseMessage;
//		BaseV1Dto getValueMessage;
		ItemValueV1Dto itemValueData;
		String token;
		byte pinId;
		
		// Init
		optNodeItem = nodeItemRepo.findById(itemId);
		
		// Exception
		if (!optNodeItem.isPresent())
			return false;
		
		// Process
		token = optNodeItem.get().getNode().getToken();
		pinId = optNodeItem.get().getItemIndex();
		itemValueData = ItemValueV1Dto.builder().itemIndex(pinId).itemStatus(itemValue).build();
		baseMessage = iotMessageService.getBaseMessage(token, "SET", "VALUE", "ITEM", itemValueData);
//		getValueMessage = iotMessageService.getBaseMessage(token, "GET", "VALUE", "ITEM", pinId);
		
		// Send
		iotChannelCompV1.sendDataToken(baseMessage);
//		iotChannelCompV1.sendDataToken(getValueMessage);
		
		// Return
		return true;
	}
	
	public boolean setNodeEnableStatus(long nodeId, EnableStatusRule enableStatusRule)
	{
		// Field
		Optional<Node> optNode;
		Node node;
		
		// Init
		optNode = nodeRepo.findById(nodeId);
		
		// Exception
		if (!optNode.isPresent())
			return false;
		else
			node = optNode.get();
		if (node.getEnableStatus() == enableStatusRule)
			return false;
		
		// Process
		if (node.getEnableStatus() == EnableStatusRule.WAIT && enableStatusRule != EnableStatusRule.REJECT)
			node.setApprovalDate(new Date());
		node.setEnableStatus(enableStatusRule);
		
		// DB - Update
		nodeRepo.save(node);
		
		// Return
		return true;
	}
	
	public boolean setNodeAlias(long nodeId, String alias)
	{
		// Field
		Node node;
		
		// Init
		node = getNode(nodeId);
		
		// Exception
		if (node == null)
			return false;
		
		// Process
		node.setAlias(alias);
		
		// DB - Update
		nodeRepo.save(node);
		
		// Return
		return true;
	}
	
	public boolean setNodeItemAlias(long nodeItemId, String alias)
	{
		// Field
		NodeItem nodeItem;
		
		// Init
		nodeItem = getNodeItem(nodeItemId);
		
		// Exception
		if (nodeItem == null)
			return false;
		
		// Process
		nodeItem.setAlias(alias);
		
		// DB - Update
		nodeItemRepo.save(nodeItem);
		
		// Return
		return true;
	}

	public boolean restartNode(long nodeId)
	{
		// Field
		Optional<Node> optNode;
		BaseV1Dto baseMessage;
		String token;
		
		// Init
		optNode = nodeRepo.findById(nodeId);
		
		// Exception
		if (!optNode.isPresent())
			return false;
		
		// Process
		token = optNode.get().getToken();
		baseMessage = iotMessageService.getBaseMessage(token, "ACT", "SYS", "RESTART", "NOW");
		
		// Send
		iotChannelCompV1.sendDataToken(baseMessage);
		
		// Return
		return true;
	}
}
