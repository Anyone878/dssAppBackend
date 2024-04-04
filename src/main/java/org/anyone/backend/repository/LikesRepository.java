package org.anyone.backend.repository;

import org.anyone.backend.model.Likes;
import org.anyone.backend.model.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikesRepository extends CrudRepository<Likes, Integer> {
    Iterable<Likes> findAllByUserOrderByLikeDateTimeDesc(Users user);
    Iterable<Likes> findAllByFidAndTypeOrderByLikeDateTimeDesc(int fID, Likes.Type type);

    Optional<Likes> findByFidAndUserAndType(int fid, Users user, Likes.Type type);
}
