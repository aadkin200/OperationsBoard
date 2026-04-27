package apsoftware.operationsboard.repository;

import apsoftware.operationsboard.entity.Membership;
import apsoftware.operationsboard.enums.MembershipRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserIdAndTeamId(Long userId, Long teamId);

    boolean existsByUserIdAndTeamId(Long userId, Long teamId);

    boolean existsByUserIdAndTeamIdAndRole(Long userId, Long teamId, MembershipRole role);

    @EntityGraph(attributePaths = {"user", "team"})
    List<Membership> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "team"})
    List<Membership> findByTeamId(Long teamId);
}
