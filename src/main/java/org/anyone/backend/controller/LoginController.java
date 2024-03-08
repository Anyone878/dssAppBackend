package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.service.UserDetailsService;
import org.anyone.backend.util.JwtUtil;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginController(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ResponseData<?>> login(@RequestBody JsonNode requestBody) {
        String username = requestBody.get("username").asText();
        String rawPassword = requestBody.get("password").asText();
        try {
            Optional<Users> userOptional = userRepository.findUserByUsername(username);
            if (userOptional.isEmpty()) {
                throw new UsernameNotFoundException("User " + username + " NOT FOUND");
            }

            Users users = userOptional.get();

            logger.info(passwordEncoder.encode(rawPassword));
            if (!passwordEncoder.matches(rawPassword, users.getPassword())) {
                logger.info(passwordEncoder.encode(rawPassword));
                throw new Exception("Password incorrect");
            }

            String token = JwtUtil.generate(username);
            logger.info("Login success");
            return ResponseEntity.status(200)
                    .header("Authorization", "Bearer " + token)
                    .body(new ResponseData<>(200, "login success", users));
        } catch (Exception e) {
            logger.info("Login failed");
            return ResponseEntity.status(401)
                    .body(new ResponseData<>(401, "login failed", null));
        }
    }
}
