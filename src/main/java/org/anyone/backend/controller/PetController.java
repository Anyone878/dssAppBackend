package org.anyone.backend.controller;

import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.PetRepository;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.util.ResponseData;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class PetController {

    private final UserRepository userRepository;
    private final PetRepository petRepository;

    public PetController(UserRepository userRepository, PetRepository petRepository) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
    }

    @GetMapping
    public ResponseData<?> getPet(
            @CurrentSecurityContext(expression = "authentication.principal")UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Users> optionalUsers = userRepository.findUserByUsername(username);
        if (optionalUsers.isEmpty()) {
            return new ResponseData<>(401, "user not found", null);
        }

        Users user = optionalUsers.get();
        Optional<Pet> optionalPet = petRepository.findByUser(user);
        if (optionalPet.isEmpty()) {
            return new ResponseData<>(404, "pet not found", null);
        }

        return new ResponseData<>(200, "pet found", optionalPet.get());
    }
}
