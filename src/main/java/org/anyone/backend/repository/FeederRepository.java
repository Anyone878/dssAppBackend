package org.anyone.backend.repository;

import org.anyone.backend.model.Feeder;
import org.anyone.backend.model.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FeederRepository extends CrudRepository<Feeder, Integer> {
    Optional<Feeder> findByUser(Users user);
}
