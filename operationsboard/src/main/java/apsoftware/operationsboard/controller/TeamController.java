package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.*;
import apsoftware.operationsboard.entity.Membership;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.security.CurrentUserService;
import apsoftware.operationsboard.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final CurrentUserService currentUserService;

    public TeamController(TeamService teamService, CurrentUserService currentUserService) {
        this.teamService = teamService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<TeamDto> getVisibleTeams() {
    	Long currentUserId = currentUserService.getCurrentUserId();
        return teamService.getVisibleTeams(currentUserId)
                .stream()
                .map(DtoMapper::toTeamDto)
                .toList();
    }

    @GetMapping("/{teamId}")
    public TeamDto getTeam(
            @PathVariable Long teamId
    ) {
    	Long currentUserId = currentUserService.getCurrentUserId();
        return DtoMapper.toTeamDto(teamService.getTeam(currentUserId, teamId));
    }

    @GetMapping("/{teamId}/members")
    public List<MembershipDto> getTeamMembers(
            @PathVariable Long teamId
    ) {
    	Long currentUserId = currentUserService.getCurrentUserId();
        return teamService.getTeamMembers(currentUserId, teamId)
                .stream()
                .map(DtoMapper::toMembershipDto)
                .toList();
    }

    @PostMapping
    public TeamDto createTeam(@RequestBody TeamCreateRequest request) {
        return DtoMapper.toTeamDto(
                teamService.createTeam(request.getName(), request.getDescription())
        );
    }

    @PostMapping("/{teamId}/members")
    public MembershipDto addUserToTeam(
            @PathVariable Long teamId,
            @RequestBody MembershipCreateRequest request
    ) {
    	Long currentUserId = currentUserService.getCurrentUserId();
        Membership membership = teamService.addUserToTeam(
                currentUserId,
                teamId,
                request.getUserId(),
                request.getRole()
        );

        return DtoMapper.toMembershipDto(membership);
    }
}
