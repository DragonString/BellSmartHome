package net.softbell.bsh.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.softbell.bsh.domain.AuthStatusRule;


/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 회원 로그인 로그 엔티티
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="member_login_log",
indexes = {
	    @Index(name = "IDX_PERIOD_DATE", columnList = "request_date") // 수신 시간으로 조회하는 구문이 있으므로 인덱싱 필요 (안하면 데이터 많아졌을때 풀스캔돌려서 조회시간 미침)
})
@NamedQuery(name="MemberLoginLog.findAll", query="SELECT m FROM MemberLoginLog m")
public class MemberLoginLog implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="log_id", unique=true, nullable=false)
	private Long logId;

	@Column(nullable=false, length=15)
	private String ipv4;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="request_date", nullable=false)
	private Date requestDate;

	@Column(nullable=false)
	private AuthStatusRule status;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id", nullable=false)
	private Member member;
}