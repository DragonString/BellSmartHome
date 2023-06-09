package net.softbell.bsh.domain.entity

import net.softbell.bsh.domain.TriggerStatusRule
import java.io.Serializable
import javax.persistence.*

/**
 * @author : Bell(bell@softbell.net)
 * @description : 노드 트리거 액션 엔티티
 */
@Entity
@Table(name = "node_trigger_action")
@NamedQuery(name = "NodeTriggerAction.findAll", query = "SELECT n FROM NodeTriggerAction n")
class NodeTriggerAction(
        @Column(name = "trigger_status", nullable = false)
        var triggerStatus: TriggerStatusRule,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "trigger_id", nullable = false)
        var nodeTrigger: NodeTrigger,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "action_id", nullable = false)
        var nodeAction: NodeAction
) : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trigger_action_id", unique = true, nullable = false)
    var triggerActionId: Long = 0

    companion object {
        private const val serialVersionUID = 1L
    }
}