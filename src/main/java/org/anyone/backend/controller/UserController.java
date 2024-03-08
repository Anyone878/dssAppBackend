package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @GetMapping
    public ResponseData<?> getUser(@RequestBody JsonNode requestBody) {
        int userID = requestBody.get("id").asInt();
        if (userID == 0) {
            return new ResponseData<>(400, "bad request body", null);
        }
        Optional<Users> userOptional = userRepository.findById(userID);
        if (userOptional.isEmpty()) {
            return new ResponseData<>(404, "user not found", null);
        }
        return new ResponseData<Users>(200, "user found", userOptional.get());
    }
}
