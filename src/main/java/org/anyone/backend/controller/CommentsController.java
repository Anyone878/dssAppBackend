package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.dto.response.CommentDTO;
import org.anyone.backend.model.PostComments;
import org.anyone.backend.model.Users;
import org.anyone.backend.service.LikesService;
import org.anyone.backend.service.PostCommentsService;
import org.anyone.backend.service.UserService;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/comments")
public class CommentsController {
    private final PostCommentsService postCommentsService;
    private final UserService userService;
    private final LikesService likesService;
    private final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    public CommentsController(PostCommentsService postCommentsService, UserService userService, LikesService likesService) {
        this.postCommentsService = postCommentsService;
        this.userService = userService;
        this.likesService = likesService;
    }

    @PostMapping("/{id}/like")
    ResponseData<?> likeComment(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @PathVariable String id
    ) {
        try {
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            int commentID = Integer.parseInt(id);
            // check if already liked
            if (likesService.getCommentLike(currentUser, commentID) != null)
                return new ResponseData<>(400, "already liked");
            // like
            CommentDTO commentDTO = postCommentsService.likeComment(commentID, currentUser);
            if (commentDTO == null) return new ResponseData<>(404, "comment (or like) not found");
            return new ResponseData<>(200, "comment liked", commentDTO);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
            return ResponseData.badRequestBodyResponse();
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseData.serverFailureResponse();
        }
    }

    @DeleteMapping("/{id}/like")
    ResponseData<?> unlikeComment(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @PathVariable String id
    ) {
        try {
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            int commentID = Integer.parseInt(id);
            // check if never liked
            if (likesService.getCommentLike(currentUser, commentID) == null)
                return new ResponseData<>(400, "never liked");
            // unlike
            CommentDTO commentDTO = postCommentsService.unlikeComment(commentID, currentUser);
            if (commentDTO == null) return new ResponseData<>(404, "comment (or unlike) not found");
            return new ResponseData<>(200, "comment unliked", commentDTO);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseData.serverFailureResponse();
        }
    }

    @GetMapping
    ResponseData<?> getComments(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "postID", required = false, defaultValue = "null") String postid
    ) {
        try {
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            if (Objects.equals(postid, "null")) return ResponseData.badRequestBodyResponse();
            int postID = Integer.parseInt(postid);
            ArrayList<CommentDTO> commentDTOS = postCommentsService.getCommentDTOs(postID, currentUser);
            return new ResponseData<>(200, "comments found", commentDTOS);
        } catch (NumberFormatException e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @PostMapping
    ResponseData<?> addComment(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode requestBody
    ) {
        try {
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            if (!requestBody.has("postID") || !requestBody.has("content"))
                return ResponseData.badRequestBodyResponse();
            int postID = requestBody.get("postID").asInt();
            String content = requestBody.get("content").asText();
            if (postID == 0 || content.isEmpty()) return ResponseData.badRequestBodyResponse();
            CommentDTO commentDTO = postCommentsService.addComment(postID, currentUser, content);
            if (commentDTO == null) return new ResponseData<>(404, "post not found, or failed to add comment");
            return new ResponseData<>(200, "comment added", commentDTO);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @DeleteMapping
    ResponseData<?> deleteComment(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "commentID", required = false, defaultValue = "null") String id
    ) {
        try {
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            if (Objects.equals(id, "null")) return ResponseData.badRequestBodyResponse();
            int commentID = Integer.parseInt(id);
            // check authorization
            if (!postCommentsService.belongsTo(commentID, currentUser))
                return new ResponseData<>(403, "cannot delete comment belongs to others.");
            CommentDTO commentDTO = postCommentsService.deleteComment(commentID, currentUser);
            if (commentDTO == null) return new ResponseData<>(404, "comment not found");
            return new ResponseData<>(200, "comment deleted", commentDTO);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }
}
