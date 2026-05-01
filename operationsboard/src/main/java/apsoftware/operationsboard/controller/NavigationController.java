package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.SidebarTeamDto;
import apsoftware.operationsboard.security.CurrentUserService;
import apsoftware.operationsboard.service.NavigationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    private final NavigationService navigationService;
    private final CurrentUserService currentUserService;

    public NavigationController(NavigationService navigationService, CurrentUserService currentUserService) {
        this.navigationService = navigationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/sidebar")
    public List<SidebarTeamDto> getSidebarNavigation() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return navigationService.getSidebarTeams(currentUserId);
    }
}
