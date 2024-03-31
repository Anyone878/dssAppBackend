package org.anyone.backend.service;

import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet getPet(Users user) {
        Optional<Pet> optionalPet = petRepository.findByUser(user);
        return optionalPet.orElse(null);
    }
}
