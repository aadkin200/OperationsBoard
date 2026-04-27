package apsoftware.operationsboard.service;

import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.enums.GlobalAccessLevel;
import apsoftware.operationsboard.exception.BadRequestException;
import apsoftware.operationsboard.exception.ForbiddenException;
import apsoftware.operationsboard.exception.ResourceNotFoundException;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional
    public User createUser(String employeeId, String username, String firstName, String lastName, String email) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required.");
        }

        if (firstName == null || firstName.isBlank()) {
            throw new BadRequestException("First name is required.");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new BadRequestException("Last name is required.");
        }

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required.");
        }

        User user = new User();
        user.setEmployeeId(employeeId);
        user.setUsername(username.trim());
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(email.trim());
        user.setActive(true);
        user.setGlobalAccessLevel(GlobalAccessLevel.NONE);

        return userRepository.save(user);
    }

    @Transactional
    public User deactivateUser(Long userId) {
        User user = getUser(userId);
        user.setActive(false);
        return userRepository.save(user);
    }
    
    @Transactional
    public User setExecutiveAccess(Long currentUserId, Long userId, boolean executive) {
        User currentUser = getUser(currentUserId);

        if (currentUser.getGlobalAccessLevel() != GlobalAccessLevel.SUPER_USER) {
            throw new ForbiddenException("Only super users can change executive access.");
        }

        User user = getUser(userId);
        user.setGlobalAccessLevel(executive ? GlobalAccessLevel.EXECUTIVE : GlobalAccessLevel.NONE);

        return userRepository.save(user);
    }
}
