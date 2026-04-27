package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.*;
import apsoftware.operationsboard.entity.Membership;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<TeamDto> getVisibleTeams(@RequestParam Long currentUserId) {
        return teamService.getVisibleTeams(currentUserId)
                .stream()
                .map(DtoMapper::toTeamDto)
                .toList();
    }

    @GetMapping("/{teamId}")
    public TeamDto getTeam(
            @RequestParam Long currentUserId,
            @PathVariable Long teamId
    ) {
        return DtoMapper.toTeamDto(teamService.getTeam(currentUserId, teamId));
    }

    @GetMapping("/{teamId}/members")
    public List<MembershipDto> getTeamMembers(
            @RequestParam Long currentUserId,
            @PathVariable Long teamId
    ) {
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
            @RequestParam Long currentUserId,
            @PathVariable Long teamId,
            @RequestBody MembershipCreateRequest request
    ) {
        Membership membership = teamService.addUserToTeam(
                currentUserId,
                teamId,
                request.getUserId(),
                request.getRole()
        );

        return DtoMapper.toMembershipDto(membership);
    }
}
