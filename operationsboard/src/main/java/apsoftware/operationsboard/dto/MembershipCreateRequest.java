package apsoftware.operationsboard.dto;

import apsoftware.operationsboard.enums.MembershipRole;

public class MembershipCreateRequest {

    private Long userId;
    private MembershipRole role;

    public MembershipCreateRequest() {}

    public Long getUserId() { return userId; }
    public MembershipRole getRole() { return role; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setRole(MembershipRole role) { this.role = role; }
}
