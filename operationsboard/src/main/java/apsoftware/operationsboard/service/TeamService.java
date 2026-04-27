package apsoftware.operationsboard.service;

import apsoftware.operationsboard.entity.Membership;
import apsoftware.operationsboard.entity.Team;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.MembershipRole;
import apsoftware.operationsboard.exception.BadRequestException;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.repository.MembershipRepository;
import apsoftware.operationsboard.repository.TeamRepository;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PermissionService permissionService;

    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            PermissionService permissionService
    ) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.permissionService = permissionService;
    }

    public List<Team> getVisibleTeams(Long currentUserId) {
        User currentUser = getUser(currentUserId);

        if (permissionService.isExecutive(currentUser)) {
            return teamRepository.findAll();
        }

        return membershipRepository.findByUserId(currentUserId)
                .stream()
                .map(Membership::getTeam)
                .toList();
    }

    public Team getTeam(Long currentUserId, Long teamId) {
        User currentUser = getUser(currentUserId);
        Team team = getTeamById(teamId);

        if (!permissionService.canViewTeam(currentUser, teamId)) {
            throw new ForbiddenException("You do not have permission to view this team.");
        }

        return team;
    }

    public List<Membership> getTeamMembers(Long currentUserId, Long teamId) {
        User currentUser = getUser(currentUserId);

        if (!permissionService.canViewTeam(currentUser, teamId)) {
            throw new ForbiddenException("You do not have permission to view this team's members.");
        }

        return membershipRepository.findByTeamId(teamId);
    }

    @Transactional
    public Team createTeam(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Team name is required.");
        }

        Team team = new Team();
        team.setName(name.trim());
        team.setDescription(description);
        team.setActive(true);

        return teamRepository.save(team);
    }

    @Transactional
    public Membership addUserToTeam(Long currentUserId, Long teamId, Long userId, MembershipRole role) {
        Team team = getTeamById(teamId);
        User userToAdd = getUser(userId);

        if (!permissionService.isTeamManager(currentUserId, teamId)) {
            throw new ForbiddenException("Only team managers can add users to a team.");
        }

        if (membershipRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new BadRequestException("User is already a member of this team.");
        }

        Membership membership = new Membership();
        membership.setTeam(team);
        membership.setUser(userToAdd);
        membership.setRole(role == null ? MembershipRole.MEMBER : role);
        membership.setActive(true);

        return membershipRepository.save(membership);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
    }
}
