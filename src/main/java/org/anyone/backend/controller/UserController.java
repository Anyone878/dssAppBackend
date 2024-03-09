package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData<?> getUser(@RequestBody JsonNode requestBody) {
        int userID = requestBody.get("id").asInt();
        if (userID == 0) {
            return new ResponseData<>(400, "bad request body", null);
        }
        Optional<Users> userOptional = userRepository.findById(userID);
        if (userOptional.isEmpty()) {
            return new ResponseData<>(404, "user not found in get (1)", null);
        }
        return new ResponseData<>(200, "user found", userOptional.get());
    }

    @GetMapping()
    public ResponseData<?> getUser(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Users> usersOptional = userRepository.findUserByUsername(username);
        if (usersOptional.isEmpty()) {
            return new ResponseData<>(404, "user not found in get (2)", null);
        }
        return new ResponseData<>(200, "user found", usersOptional.get());
    }
}
