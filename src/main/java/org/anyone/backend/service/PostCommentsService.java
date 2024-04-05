package org.anyone.backend.service;

import org.anyone.backend.dto.response.CommentDTO;
import org.anyone.backend.model.Likes;
import org.anyone.backend.model.PostComments;
import org.anyone.backend.model.Posts;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.PostCommentsRepository;
import org.anyone.backend.repository.PostsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class PostCommentsService {
    private final PostCommentsRepository postCommentsRepository;
    private final LikesService likesService;
    private final PostsRepository postsRepository;
    private final Logger logger = LoggerFactory.getLogger(PostCommentsService.class);

    public PostCommentsService(PostCommentsRepository postCommentsRepository, LikesService likesService, PostsRepository postsRepository) {
        this.postCommentsRepository = postCommentsRepository;
        this.likesService = likesService;
        this.postsRepository = postsRepository;
    }

    private Iterable<PostComments> getPostComments(int postID) {
        Posts post = postsRepository.findByPostID(postID).orElse(null);
        return post != null ? postCommentsRepository.findAllByPost(post) : new ArrayList<>();
    }

    private ArrayList<CommentDTO> convertToDTOArray(Iterable<PostComments> postComments, Users currentUser) {
        ArrayList<CommentDTO> commentDTOs = new ArrayList<>();
        postComments.forEach(postComment ->
                commentDTOs.add(new CommentDTO(postComment, likesService.isUserLikedComment(currentUser, postComment))));
        return commentDTOs;
    }

    private PostComments getPostComment(int commentID) {
        return postCommentsRepository.findByCommentID(commentID).orElse(null);
    }

    public ArrayList<CommentDTO> getCommentDTOs(int postID, Users currentUser) {
        return convertToDTOArray(getPostComments(postID), currentUser);
    }

    public CommentDTO likeComment(int commentID, Users currentUser) {
        PostComments postComment = getPostComment(commentID);
        if (postComment == null) return null;
        // check if already liked by current user.
        if (likesService.getCommentLike(currentUser, postComment.getCommentID()) != null) return null;
        // like
        Likes like = likesService.likeComment(currentUser, postComment.getCommentID());
        if (like == null) return null;
        postComment.setLikes(postComment.getLikes() + 1);
        PostComments newPostComment = postCommentsRepository.save(postComment);
        return new CommentDTO(newPostComment, likesService.isUserLikedComment(currentUser, newPostComment));
    }

    public CommentDTO unlikeComment(int commentID, Users currentUser) {
        PostComments postComment = getPostComment(commentID);
        if (postComment == null) return null;
        // check if never liked
        if (likesService.getCommentLike(currentUser, postComment.getCommentID()) == null) return null;
        // unlike
        Likes like = likesService.unlikeComment(currentUser, postComment.getCommentID());
        if (like == null) return null;
        postComment.setLikes(postComment.getLikes() - 1);
        PostComments newPostComment = postCommentsRepository.save(postComment);
        return new CommentDTO(newPostComment, likesService.isUserLikedComment(currentUser, newPostComment));
    }

    public CommentDTO addComment(int postID, Users currentUser, String comment) {
        Posts post = postsRepository.findByPostID(postID).orElse(null);
        if (post == null) return null;
        PostComments postComment = new PostComments();
        postComment.setLikes(0);
        postComment.setPost(post);
        postComment.setCommentContent(comment);
        postComment.setCommentDateTime(LocalDateTime.now());
        postComment.setUser(currentUser);
        PostComments newPostComment = postCommentsRepository.save(postComment);
        return new CommentDTO(newPostComment, likesService.isUserLikedComment(currentUser, newPostComment));
    }

    public CommentDTO deleteComment(int commentID, Users currentUser) {
        PostComments postComment = getPostComment(commentID);
        if (postComment == null) return null;
        boolean liked = likesService.isUserLikedComment(currentUser, postComment);
        postCommentsRepository.delete(postComment);
        return new CommentDTO(postComment, liked);
    }

    public boolean belongsTo(int commentID, Users currentUser) throws NullPointerException {
        PostComments postComment = getPostComment(commentID);
        if (postComment == null) throw new NullPointerException();
        return postComment.getUser() == currentUser;
    }
}
