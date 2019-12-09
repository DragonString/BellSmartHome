package net.softbell.bsh.db.model;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.softbell.bsh.db.model.NodeInfo.NodeInfoBuilder;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="node_reserv")
@NamedQuery(name="NodeReserv.findAll", query="SELECT n FROM NodeReserv n")
public class NodeReserv implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NodeReservPK id;

	@Temporal(TemporalType.DATE)
	@Column(name="reserv_date", nullable=false)
	private Date reservDate;

	@Column(name="reserv_item_status", nullable=false)
	private short reservItemStatus;

	@Column(name="reserv_time", nullable=false)
	private Time reservTime;

	//bi-directional many-to-one association to NodeItemProperty
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="item_id", referencedColumnName="item_id", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="node_id", referencedColumnName="node_id", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="type_id", referencedColumnName="type_id", nullable=false, insertable=false, updatable=false)
		})
	private NodeProperty nodeItemProperty;
}