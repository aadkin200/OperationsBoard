package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.DashboardSummaryDto;
import apsoftware.operationsboard.dto.ExecutiveDashboardDto;
import apsoftware.operationsboard.dto.MemberDashboardDto;
import apsoftware.operationsboard.dto.TeamDashboardDto;
import apsoftware.operationsboard.security.CurrentUserService;
import apsoftware.operationsboard.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    public DashboardController(DashboardService dashboardService, CurrentUserService currentUserService) {
        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/executive")
    public ExecutiveDashboardDto getExecutiveDashboard() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return dashboardService.getExecutiveDashboard(currentUserId);
    }

    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return dashboardService.getDashboardSummary(currentUserId);
    }

    @GetMapping("/team/{teamId}")
    public TeamDashboardDto getTeamDashboard(@PathVariable Long teamId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return dashboardService.getTeamDashboard(currentUserId, teamId);
    }

    @GetMapping("/me")
    public MemberDashboardDto getMemberDashboard() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return dashboardService.getMemberDashboard(currentUserId);
    }
}
