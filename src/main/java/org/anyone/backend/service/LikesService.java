package org.anyone.backend.service;

import org.anyone.backend.model.Likes;
import org.anyone.backend.model.PostComments;
import org.anyone.backend.model.Posts;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.LikesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final Logger logger = LoggerFactory.getLogger(LikesService.class);

    public LikesService(LikesRepository likesRepository) {
        this.likesRepository = likesRepository;
    }

    private Likes addLike(Users user, Likes.Type type, int fid) {
        Likes like = new Likes();
        like.setFid(fid);
        like.setUser(user);
        like.setType(type);
        like.setLikeDateTime(LocalDateTime.now());
        return likesRepository.save(like);
    }

    private Likes deleteLike(Users user, Likes.Type type, int fid) {
        Likes like = likesRepository.findByFidAndUserAndType(fid, user, type).orElse(null);
        if (like == null) return null;
        likesRepository.delete(like);
        return like;
    }

    public Likes getPostLike(Users user, int postID) {
        return likesRepository.findByFidAndUserAndType(postID, user, Likes.Type.POST).orElse(null);
    }

    public Likes getCommentLike(Users user, int commentID) {
        return likesRepository.findByFidAndUserAndType(commentID, user, Likes.Type.COMMENT).orElse(null);
    }

    public Likes likePost(Users user, int postID) {
        return addLike(user, Likes.Type.POST, postID);
    }

    public Likes likeComment(Users user, int commentID) {
        return addLike(user, Likes.Type.COMMENT, commentID);
    }

    public Likes unlikePost(Users user, int postID) {
        return deleteLike(user, Likes.Type.POST, postID);
    }

    public Likes unlikeComment(Users user, int commentID) {
        return deleteLike(user, Likes.Type.COMMENT, commentID);
    }

    public boolean isUserLikedPost(Users currentUser, Posts post) {
        Optional<Likes> optionalLikes =
                likesRepository.findByFidAndUserAndType(post.getPostID(), currentUser, Likes.Type.POST);
        return optionalLikes.isPresent();
    }

    public boolean isUserLikedComment(Users currentUser, PostComments comment) {
        Optional<Likes> optionalLikes =
                likesRepository.findByFidAndUserAndType(comment.getCommentID(), currentUser, Likes.Type.COMMENT);
        return optionalLikes.isPresent();
    }

    /**
     * get num of likes received.
     */
    public int getLikesNum(Users user) {
        // TODO
        return 0;
    }
}
