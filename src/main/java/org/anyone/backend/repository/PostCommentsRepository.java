package org.anyone.backend.repository;

import org.anyone.backend.model.PostComments;
import org.anyone.backend.model.Posts;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PostCommentsRepository extends CrudRepository<PostComments, Integer> {
    Optional<PostComments> findByCommentID(int commentID);
    Iterable<PostComments> findAllByPost(Posts post);
}
