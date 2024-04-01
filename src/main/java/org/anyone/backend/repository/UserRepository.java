package org.anyone.backend.repository;

import org.anyone.backend.model.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Integer> {
    Optional<Users> findUserByUsername(String userName);
    Optional<Users> findByUserID(int userID);
}
