package net.softbell.bsh.dto.card;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 카드 대시보드 데이터 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class CardDashboard {
	@NonNull
	private String cardTopic;
	@NonNull
	private String cardLast;
	
	@NonNull
	private List<CardItem> cardItems;
}