package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.DashboardSummaryDto;
import apsoftware.operationsboard.dto.MemberDashboardDto;
import apsoftware.operationsboard.dto.TeamDashboardDto;
import apsoftware.operationsboard.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/executive")
    public DashboardSummaryDto getExecutiveDashboard(@RequestParam Long currentUserId) {
        return dashboardService.getExecutiveDashboard(currentUserId);
    }

    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary(@RequestParam Long currentUserId) {
        return dashboardService.getDashboardSummary(currentUserId);
    }
    
    @GetMapping("/team/{teamId}")
    public TeamDashboardDto getTeamDashboard(
            @RequestParam Long currentUserId,
            @PathVariable Long teamId
    ) {
        return dashboardService.getTeamDashboard(currentUserId, teamId);
    }
    
    @GetMapping("/me")
    public MemberDashboardDto getMemberDashboard(@RequestParam Long currentUserId) {
        return dashboardService.getMemberDashboard(currentUserId);
    }
}
