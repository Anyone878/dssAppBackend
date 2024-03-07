package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.User;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.util.ResponseData;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/register")
public class RegisterController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseData<?> register(@RequestBody JsonNode requestBody) {
        String username = requestBody.get("username").asText();
        String rawPassword = requestBody.get("password").asText();
        String email = requestBody.get("email").asText();
        String phoneNumber = requestBody.get("phoneNumber").asText();
        String fullName = requestBody.get("fullName").asText();

        if (username.isEmpty()
                || rawPassword.isEmpty()
                || email.isEmpty()
                || phoneNumber.isEmpty()
                || fullName.isEmpty()) {
            return new ResponseData<>(400, "field invalid", null);
        }

        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if (userOptional.isPresent()) {
            // username is being used.
            return new ResponseData<>(409, "username is being used", null);
        }

        User user = new User(username, passwordEncoder.encode(rawPassword), email, phoneNumber, fullName);
        User savedUser = userRepository.save(user);
        return new ResponseData<>(200, "user created", savedUser);
    }
}
