package apsoftware.operationsboard.service;

import apsoftware.operationsboard.entity.Task;
import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.GlobalAccessLevel;
import apsoftware.operationsboard.enums.MembershipRole;
import apsoftware.operationsboard.repository.MembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final MembershipRepository membershipRepository;

    public PermissionService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public boolean isExecutive(User user) {
        return user.getGlobalAccessLevel() == GlobalAccessLevel.EXECUTIVE;
    }

    public boolean isTeamMember(Long userId, Long teamId) {
        return membershipRepository.existsByUserIdAndTeamId(userId, teamId);
    }

    public boolean isTeamManager(Long userId, Long teamId) {
        return membershipRepository.existsByUserIdAndTeamIdAndRole(userId, teamId, MembershipRole.MANAGER);
    }

    public boolean canViewTeam(User user, Long teamId) {
        return isExecutive(user) || isTeamMember(user.getId(), teamId);
    }

    public boolean canCommentOnTask(User user, Task task) {
        return isExecutive(user) || isTeamMember(user.getId(), task.getTeam().getId());
    }

    public boolean canManageTask(User user, Task task) {
        return isTeamManager(user.getId(), task.getTeam().getId());
    }

    public boolean canClaimTask(User user, Task task) {
        return isTeamMember(user.getId(), task.getTeam().getId());
    }

    public boolean canUpdateOwnTask(User user, Task task) {
        return task.getAssignedUser() != null
                && task.getAssignedUser().getId().equals(user.getId());
    }
}
