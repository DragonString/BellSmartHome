package net.softbell.bsh.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.softbell.bsh.db.model.MemberAuthType;

@Repository
public interface MemberAuthTypeRepo extends JpaRepository<MemberAuthType, Long>
{

}
