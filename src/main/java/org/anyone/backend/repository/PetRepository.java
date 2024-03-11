package org.anyone.backend.repository;

import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PetRepository extends CrudRepository<Pet, Integer> {
    Optional<Pet> findByUser(Users user);
}
