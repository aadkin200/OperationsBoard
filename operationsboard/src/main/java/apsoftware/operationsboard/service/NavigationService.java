package apsoftware.operationsboard.service;

import apsoftware.operationsboard.dto.SidebarTeamDto;
import apsoftware.operationsboard.entity.Membership;
import apsoftware.operationsboard.entity.Team;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.MembershipRole;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.repository.MembershipRepository;
import apsoftware.operationsboard.repository.TeamRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NavigationService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MembershipRepository membershipRepository;
    private final PermissionService permissionService;

    public NavigationService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            MembershipRepository membershipRepository,
            PermissionService permissionService
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.membershipRepository = membershipRepository;
        this.permissionService = permissionService;
    }

    public List<SidebarTeamDto> getSidebarTeams(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        if (permissionService.isExecutive(currentUser)) {
            return getExecutiveSidebarTeams(currentUserId);
        }

        return membershipRepository.findByUserId(currentUserId)
                .stream()
                .filter(membership -> Boolean.TRUE.equals(membership.getActive()))
                .map(this::toMemberSidebarTeam)
                .toList();
    }

    private List<SidebarTeamDto> getExecutiveSidebarTeams(Long currentUserId) {
        List<Team> teams = teamRepository.findAll();
        List<SidebarTeamDto> sidebarTeams = new ArrayList<>();

        for (Team team : teams) {
            if (!Boolean.TRUE.equals(team.getActive())) {
                continue;
            }

            Optional<Membership> membership = membershipRepository.findByUserIdAndTeamId(currentUserId, team.getId());

            MembershipRole role = membership
                    .map(Membership::getRole)
                    .orElse(null);

            sidebarTeams.add(new SidebarTeamDto(
                    team.getId(),
                    team.getName(),
                    role,
                    true,
                    true,
                    membership.isEmpty()
            ));
        }

        return sidebarTeams;
    }

    private SidebarTeamDto toMemberSidebarTeam(Membership membership) {
        boolean isManager = membership.getRole() == MembershipRole.MANAGER;

        return new SidebarTeamDto(
                membership.getTeam().getId(),
                membership.getTeam().getName(),
                membership.getRole(),
                true,
                isManager,
                false
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }
}
