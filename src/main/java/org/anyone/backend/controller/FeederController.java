package org.anyone.backend.controller;

import org.anyone.backend.model.Feeder;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.FeederRepository;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.util.ResponseData;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/feeder")
public class FeederController {

    private final UserRepository userRepository;
    private final FeederRepository feederRepository;

    public FeederController(UserRepository userRepository, FeederRepository feederRepository) {
        this.userRepository = userRepository;
        this.feederRepository = feederRepository;
    }

    @GetMapping
    public ResponseData<?> getFeeder(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Users> optionalUsers = userRepository.findUserByUsername(username);
        if (optionalUsers.isEmpty()) {
            return new ResponseData<>(401, "user not found", null);
        }

        Users user = optionalUsers.get();
        Optional<Feeder> optionalFeeder = feederRepository.findByUser(user);
        if (optionalFeeder.isEmpty()) {
            return new ResponseData<>(404, "feeder not found", null);
        }

        Feeder feeder = optionalFeeder.get();
        return new ResponseData<>(200, "feeder found", feeder);
    }
}
