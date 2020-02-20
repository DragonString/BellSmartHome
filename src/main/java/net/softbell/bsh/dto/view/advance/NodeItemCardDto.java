package net.softbell.bsh.dto.view.advance;

import lombok.Getter;
import lombok.Setter;
import net.softbell.bsh.domain.ItemCategoryRule;
import net.softbell.bsh.domain.ItemModeRule;
import net.softbell.bsh.domain.ItemTypeRule;
import net.softbell.bsh.domain.entity.NodeItem;
import net.softbell.bsh.domain.entity.NodeItemHistory;

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 노드 수정뷰 아이템 카드정보 DTO
 */
@Getter
@Setter
public class NodeItemCardDto
{
	private Long itemId;
	private String alias;
	private String itemName;
	private ItemModeRule itemMode;
	private ItemTypeRule itemType;
	private ItemCategoryRule itemCategory;
	private Byte controlMode;
	private Double lastStatus;
	
	public NodeItemCardDto(NodeItem entity, NodeItemHistory lastHistory)
	{
		// Exception
		if (entity == null)
			return;
		
		// Convert
		this.itemId = entity.getItemId();
		this.alias = entity.getAlias();
		this.itemName = entity.getItemName();
		this.itemMode = entity.getItemMode();
		this.itemType = entity.getItemType();
		this.itemCategory = entity.getItemCategory();
		this.controlMode = entity.getControlMode();
		this.lastStatus = lastHistory.getItemStatus();
	}
}