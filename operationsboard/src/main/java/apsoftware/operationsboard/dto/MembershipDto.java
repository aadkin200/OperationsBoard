package apsoftware.operationsboard.dto;

import apsoftware.operationsboard.enums.MembershipRole;

public class MembershipDto {

    private Long id;
    private UserDto user;
    private TeamDto team;
    private MembershipRole role;
    private Boolean active;

    public MembershipDto() {}

    public MembershipDto(Long id, UserDto user, TeamDto team, MembershipRole role, Boolean active) {
        this.id = id;
        this.user = user;
        this.team = team;
        this.role = role;
        this.active = active;
    }

    public Long getId() { return id; }
    public UserDto getUser() { return user; }
    public TeamDto getTeam() { return team; }
    public MembershipRole getRole() { return role; }
    public Boolean getActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setUser(UserDto user) { this.user = user; }
    public void setTeam(TeamDto team) { this.team = team; }
    public void setRole(MembershipRole role) { this.role = role; }
    public void setActive(Boolean active) { this.active = active; }
}
