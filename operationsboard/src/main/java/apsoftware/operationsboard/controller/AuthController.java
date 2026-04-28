package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.UserDto;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CurrentUserService currentUserService;

    public AuthController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public UserDto getCurrentUser() {
        return DtoMapper.toUserDto(currentUserService.getCurrentUser());
    }
}
