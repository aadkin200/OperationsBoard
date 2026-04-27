package apsoftware.operationsboard.controller;

import apsoftware.operationsboard.dto.UserCreateRequest;
import apsoftware.operationsboard.dto.UserDto;
import apsoftware.operationsboard.mapper.DtoMapper;
import apsoftware.operationsboard.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers()
                .stream()
                .map(DtoMapper::toUserDto)
                .toList();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return DtoMapper.toUserDto(userService.getUser(userId));
    }

    @GetMapping("/username/{username}")
    public UserDto getByUsername(@PathVariable String username) {
        return DtoMapper.toUserDto(userService.getByUsername(username));
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserCreateRequest request) {
        return DtoMapper.toUserDto(
                userService.createUser(
                        request.getEmployeeId(),
                        request.getUsername(),
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail()
                )
        );
    }

    @PatchMapping("/{userId}/executive")
    public UserDto setExecutiveAccess(
            @RequestParam Long currentUserId,
            @PathVariable Long userId,
            @RequestParam boolean executive
    ) {
        return DtoMapper.toUserDto(
                userService.setExecutiveAccess(currentUserId, userId, executive)
        );
    }

    @PatchMapping("/{userId}/deactivate")
    public UserDto deactivateUser(@PathVariable Long userId) {
        return DtoMapper.toUserDto(userService.deactivateUser(userId));
    }
}
