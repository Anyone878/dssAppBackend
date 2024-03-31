package org.anyone.backend.service;

import org.anyone.backend.model.Users;
import org.anyone.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Users getUser(UserDetails userDetails) {
        Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
        return optionalUsers.orElse(null);
    }
}
