package net.softbell.bsh.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.softbell.bsh.db.model.NodeInfo;

@Repository
public interface NodeInfoRepo extends JpaRepository<NodeInfo, Long>
{
    
}