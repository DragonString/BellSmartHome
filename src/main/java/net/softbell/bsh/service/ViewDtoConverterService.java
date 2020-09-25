package net.softbell.bsh.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.softbell.bsh.domain.entity.Member;
import net.softbell.bsh.domain.entity.MemberGroup;
import net.softbell.bsh.domain.entity.MemberInterlockToken;
import net.softbell.bsh.domain.entity.MemberLoginLog;
import net.softbell.bsh.domain.entity.Node;
import net.softbell.bsh.domain.entity.NodeAction;
import net.softbell.bsh.domain.entity.NodeActionItem;
import net.softbell.bsh.domain.entity.NodeGroup;
import net.softbell.bsh.domain.entity.NodeItem;
import net.softbell.bsh.domain.entity.NodeItemHistory;
import net.softbell.bsh.domain.entity.NodeReserv;
import net.softbell.bsh.domain.entity.NodeReservAction;
import net.softbell.bsh.domain.entity.NodeTrigger;
import net.softbell.bsh.dto.view.InterlockTokenCardDto;
import net.softbell.bsh.dto.view.MemberActivityLogCardDto;
import net.softbell.bsh.dto.view.admin.GroupMemberCardItemDto;
import net.softbell.bsh.dto.view.admin.GroupNodeCardItemDto;
import net.softbell.bsh.dto.view.admin.MemberSummaryCardDto;
import net.softbell.bsh.dto.view.admin.NodeManageItemCardDto;
import net.softbell.bsh.dto.view.admin.NodeManageSummaryCardDto;
import net.softbell.bsh.dto.view.admin.group.MemberGroupSummaryCardDto;
import net.softbell.bsh.dto.view.admin.group.NodeGroupSummaryCardDto;
import net.softbell.bsh.dto.view.advance.NodeItemCardDto;
import net.softbell.bsh.dto.view.advance.NodeSummaryCardDto;
import net.softbell.bsh.dto.view.advance.TriggerItemCardDto;
import net.softbell.bsh.dto.view.advance.TriggerSummaryCardDto;
import net.softbell.bsh.dto.view.general.ActionInterlockCardDto;
import net.softbell.bsh.dto.view.general.ActionItemCardDto;
import net.softbell.bsh.dto.view.general.ActionSummaryCardDto;
import net.softbell.bsh.dto.view.general.MonitorCardItemDto;
import net.softbell.bsh.dto.view.general.MonitorSummaryCardDto;
import net.softbell.bsh.dto.view.general.ReservActionCardDto;
import net.softbell.bsh.dto.view.general.ReservSummaryCardDto;
import net.softbell.bsh.iot.service.v1.IotNodeServiceV1;

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 엔티티 to 뷰 DTO 변환 서비스
 */
@RequiredArgsConstructor
@Service
public class ViewDtoConverterService
{
	// Global Field
	private final IotNodeServiceV1 iotNodeService;
	
	// Node Entity List to Monitor Card Dto List
	public List<MonitorSummaryCardDto> convMonitorSummaryCards(Collection<Node> listEntity)
	{
		// Field
		List<MonitorSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<MonitorSummaryCardDto>();
		
		// Process
		for (Node entity : listEntity)
		{
			// Field
			MonitorSummaryCardDto cardDto;
			List<MonitorCardItemDto> listItems;
			Date curDate, lastReceive;
			
			// Init
			cardDto = new MonitorSummaryCardDto(entity);
			listItems = new ArrayList<MonitorCardItemDto>();
			curDate = new Date();
			lastReceive = null;
			
			// Process
			for (NodeItem nodeItem :  entity.getNodeItems())
			{
				// Field
				NodeItemHistory lastHistory;
				
				// Init
				listItems.add(new MonitorCardItemDto(nodeItem, iotNodeService.getLastNodeItemHistory(nodeItem)));
				lastHistory = iotNodeService.getLastNodeItemHistory(nodeItem);
				
				if (lastReceive == null || lastHistory.getReceiveDate().compareTo(lastReceive) > 0)
					lastReceive = lastHistory.getReceiveDate();
			}
			cardDto.setLastReceive(lastReceive);
			cardDto.setLastReceiveSecond((curDate.getTime() - lastReceive.getTime()) / 1000);
			
			// Add
			cardDto.setListItems(listItems);
			listCards.add(cardDto);
		}
		
		// Return
		return listCards;
	}

	// NodeReserv Entity List to Reserv Card Dto List
	public List<ReservSummaryCardDto> convReservSummaryCards(Collection<NodeReserv> listEntity)
	{
		// Field
		List<ReservSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<ReservSummaryCardDto>();
		
		// Process
		for (NodeReserv entity : listEntity)
			listCards.add(new ReservSummaryCardDto(entity));
		
		// Return
		return listCards;
	}

	// NodeAction Entity List to Reserv Action Card Dto List
	public <T> List<ReservActionCardDto> convReservActionCards(Collection<T> listEntity)
	{
		// Field
		List<ReservActionCardDto> listCards;
		
		// Init
		listCards = new ArrayList<ReservActionCardDto>();
		
		// Process
		for (T entity : listEntity)
			if (entity instanceof NodeAction)
				listCards.add(new ReservActionCardDto((NodeAction) entity));
			else if (entity instanceof NodeReservAction)
				listCards.add(new ReservActionCardDto(((NodeReservAction) entity).getNodeAction()));
		
		// Return
		return listCards;
	}

	// NodeAction Entity List to Action Summary Card Dto List
	public List<ActionSummaryCardDto> convActionSummaryCards(Collection<NodeAction> listEntity)
	{
		// Field
		List<ActionSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<ActionSummaryCardDto>();
		
		// Process
		for (NodeAction entity : listEntity)
			listCards.add(new ActionSummaryCardDto(entity));
		
		// Return
		return listCards;
	}

	// NodeItem Entity List to Action Item Card Dto List
	public <T> List<ActionItemCardDto> convActionItemCards(Collection<T> listEntity)
	{
		// Field
		List<ActionItemCardDto> listCards;
		
		// Init
		listCards = new ArrayList<ActionItemCardDto>();
		
		// Process
		for (T entity : listEntity)
			if (entity instanceof NodeItem)
				listCards.add(new ActionItemCardDto((NodeItem) entity));
			else if (entity instanceof NodeActionItem)
				listCards.add(new ActionItemCardDto((NodeActionItem) entity));
		
		// Return
		return listCards;
	}

	// Node Entity List to Node Summary Card Dto List
	public <T> List<NodeSummaryCardDto> convNodeSummaryCards(Collection<T> listEntity)
	{
		// Field
		List<NodeSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<NodeSummaryCardDto>();
		
		// Process
		for (T entity : listEntity)
			listCards.add(new NodeSummaryCardDto((Node) entity));
		
		// Return
		return listCards;
	}

	// NodeItem Entity List to Node Item Card Dto List
	public List<NodeItemCardDto> convNodeItemCards(Collection<NodeItem> listEntity)
	{
		// Field
		List<NodeItemCardDto> listCards;
		
		// Init
		listCards = new ArrayList<NodeItemCardDto>();
		
		// Process
		for (NodeItem entity : listEntity)
			listCards.add(new NodeItemCardDto(entity, iotNodeService.getLastNodeItemHistory(entity)));
		
		// Return
		return listCards;
	}

	// Member Entity List to Member Summary Card Dto List
	public List<MemberSummaryCardDto> convMemberSummaryCards(Collection<Member> listEntity)
	{
		// Field
		List<MemberSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<MemberSummaryCardDto>();
		
		// Process
		for (Member entity : listEntity)
			listCards.add(new MemberSummaryCardDto(entity));
		
		// Return
		return listCards;
	}

	// MemberLoginLog Entity List to Member Activity Log Card Dto List
	public List<MemberActivityLogCardDto> convMemberActivityLogCards(Collection<MemberLoginLog> listEntity)
	{
		// Field
		List<MemberActivityLogCardDto> listCards;
		
		// Init
		listCards = new ArrayList<MemberActivityLogCardDto>();
		
		// Process
		for (MemberLoginLog entity : listEntity)
			listCards.add(new MemberActivityLogCardDto(entity));
		
		// Return
		return listCards;
	}

	// Node Entity List to Node Manage Summary Card Dto List
	public List<NodeManageSummaryCardDto> convNodeManageSummaryCards(Collection<Node> listEntity)
	{
		// Field
		List<NodeManageSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<NodeManageSummaryCardDto>();
		
		// Process
		for (Node entity : listEntity)
			listCards.add(new NodeManageSummaryCardDto(entity));
		
		// Return
		return listCards;
	}

	// NodeItem Entity List to Node Manage Item Card Dto List
	public List<NodeManageItemCardDto> convNodeManageItemCards(Collection<NodeItem> listEntity)
	{
		// Field
		List<NodeManageItemCardDto> listCards;
		
		// Init
		listCards = new ArrayList<NodeManageItemCardDto>();
		
		// Process
		for (NodeItem entity : listEntity)
			listCards.add(new NodeManageItemCardDto(entity));
		
		// Return
		return listCards;
	}

	// NodeItem Entity List to Trigger Item Card Dto List
	public List<TriggerItemCardDto> convTriggerItemCards(Collection<NodeItem> listEntity)
	{
		// Field
		List<TriggerItemCardDto> listCards;
		
		// Init
		listCards = new ArrayList<TriggerItemCardDto>();
		
		// Process
		for (NodeItem entity : listEntity)
			listCards.add(new TriggerItemCardDto(entity));
		
		// Return
		return listCards;
	}

	// Node Entity List to Node Manage Summary Card Dto List
	public List<TriggerSummaryCardDto> convTriggerSummaryCards(Collection<NodeTrigger> listEntity)
	{
		// Field
		List<TriggerSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<TriggerSummaryCardDto>();
		
		// Process
		for (NodeTrigger entity : listEntity)
			listCards.add(new TriggerSummaryCardDto(entity));
		
		// Return
		return listCards;
	}

	// Member Entity List to Group Member Card Item Dto List
	public List<GroupMemberCardItemDto> convGroupMemberCardItems(Collection<Member> listEntity)
	{
		// Field
		List<GroupMemberCardItemDto> listCards;
		
		// Init
		listCards = new ArrayList<GroupMemberCardItemDto>();
		
		// Process
		for (Member entity : listEntity)
			listCards.add(new GroupMemberCardItemDto(entity));
		
		// Return
		return listCards;
	}

	// Node Entity List to Group Node Card Item Dto List
	public List<GroupNodeCardItemDto> convGroupNodeCardItems(Collection<Node> listEntity)
	{
		// Field
		List<GroupNodeCardItemDto> listCards;
		
		// Init
		listCards = new ArrayList<GroupNodeCardItemDto>();
		
		// Process
		for (Node entity : listEntity)
			listCards.add(new GroupNodeCardItemDto(entity));
		
		// Return
		return listCards;
	}

	// NodeGroup Entity List to Node Group Summary Card Dto List
	public List<NodeGroupSummaryCardDto> convNodeGroupSummaryCards(Collection<NodeGroup> listEntity)
	{
		// Field
		List<NodeGroupSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<NodeGroupSummaryCardDto>();
		
		// Process
		for (NodeGroup entity : listEntity)
			listCards.add(new NodeGroupSummaryCardDto(entity));
		
		// Return
		return listCards;
	}

	// MemberGroup Entity List to Member Group Summary Card Dto List
	public List<MemberGroupSummaryCardDto> convMemberGroupSummaryCards(Collection<MemberGroup> listEntity)
	{
		// Field
		List<MemberGroupSummaryCardDto> listCards;
		
		// Init
		listCards = new ArrayList<MemberGroupSummaryCardDto>();
		
		// Process
		for (MemberGroup entity : listEntity)
			listCards.add(new MemberGroupSummaryCardDto(entity));
		
		// Return
		return listCards;
	}

	// MemberInterlockToken Entity List to Interlock Token Card Dto List
	public List<InterlockTokenCardDto> convInterlockTokenCards(Collection<MemberInterlockToken> listEntity)
	{
		// Field
		List<InterlockTokenCardDto> listCards;
		
		// Init
		listCards = new ArrayList<InterlockTokenCardDto>();
		
		// Process
		for (MemberInterlockToken entity : listEntity)
			listCards.add(new InterlockTokenCardDto(entity));
		
		// Return
		return listCards;
	}

	// MemberInterlockToken Entity List to Action Interlock Token Card Dto List
	public List<ActionInterlockCardDto> convActionInterlockTokenCards(Collection<MemberInterlockToken> listEntity)
	{
		// Field
		List<ActionInterlockCardDto> listCards;
		
		// Init
		listCards = new ArrayList<ActionInterlockCardDto>();
		
		// Process
		for (MemberInterlockToken entity : listEntity)
			listCards.add(new ActionInterlockCardDto(entity));
		
		// Return
		return listCards;
	}
}