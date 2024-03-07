package org.anyone.backend.repository;

import org.anyone.backend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findUserByUsername(String userName);
}
