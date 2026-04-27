package apsoftware.operationsboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apsoftware.operationsboard.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long>{

}
