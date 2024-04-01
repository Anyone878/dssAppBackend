package org.anyone.backend.repository;

import org.anyone.backend.model.Posts;
import org.anyone.backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Integer> {
    Iterable<Posts> findAllByUserOrderByPostDateTimeDesc(Users user);
    Optional<Posts> findByPostID(int postID);
}
