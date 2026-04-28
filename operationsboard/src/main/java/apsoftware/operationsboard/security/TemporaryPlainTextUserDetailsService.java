package apsoftware.operationsboard.security;

import apsoftware.operationsboard.entity.User;
import apsoftware.operationsboard.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TemporaryPlainTextUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public TemporaryPlainTextUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!Boolean.TRUE.equals(appUser.getActive())) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getUsername())
                .password("{noop}" + appUser.getPassword())
                .roles("USER")
                .build();
    }
}
