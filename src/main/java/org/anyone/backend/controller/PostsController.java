package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.dto.response.PostDTO;
import org.anyone.backend.model.Likes;
import org.anyone.backend.model.Posts;
import org.anyone.backend.model.Users;
import org.anyone.backend.service.LikesService;
import org.anyone.backend.service.PostsService;
import org.anyone.backend.service.UserService;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/posts")
public class PostsController {
    private final PostsService postsService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(PostsController.class);
    private final LikesService likesService;

    public PostsController(PostsService postsService, UserService userService, LikesService likesService) {
        this.postsService = postsService;
        this.userService = userService;
        this.likesService = likesService;
    }

    @GetMapping
    ResponseData<?> getAll(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails
    ) {
        Users user = userService.getUser(userDetails);
        if (user == null) return ResponseData.userNotFoundResponse();
        ArrayList<PostDTO> posts = postsService.getPostDTOList(user);
        return new ResponseData<>(200, "posts found", posts);
    }

    @GetMapping(params = {"postID"})
    ResponseData<?> getByPostID(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "postID") String id
    ) {
        try {
            Users user = userService.getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();

            int postID = Integer.parseInt(id);
            PostDTO post = postsService.getPostDTO(postID, user);
            if (post == null) return new ResponseData<>(404, "post not found");
            return new ResponseData<>(200, "post found", post);
        } catch (NumberFormatException e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @GetMapping(params = {"userID"})
    ResponseData<?> getAllByUserID(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "userID") String id
    ) {
        try {
            int userID = Integer.parseInt(id);
            Users postUser = userService.getUser(userID);
            if (postUser == null) return new ResponseData<>(404, "user not found");
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            return new ResponseData<>(200, "posts found", postsService.getPostDTOList(postUser, currentUser));
        } catch (NumberFormatException e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @PostMapping
    ResponseData<?> addPost(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode requestBody
    ) {
        Users user = userService.getUser(userDetails);
        if (user == null) return ResponseData.userNotFoundResponse();
        if (!requestBody.has("content")) return ResponseData.badRequestBodyResponse();
        String content = requestBody.get("content").asText();
        PostDTO post = postsService.addPost(user, content);
        return new ResponseData<>(200, "post added", post);
    }

    @DeleteMapping
    ResponseData<?> deletePost(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "id", required = false, defaultValue = "null") String id
    ) {
        try {
            if (Objects.equals(id, "null")) return ResponseData.badRequestBodyResponse();
            int postID = Integer.parseInt(id);
            Users user = userService.getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            if (!postsService.belongTo(postID, user))
                return new ResponseData<>(403, "cannot delete post that dose not belong to the current user.");
            PostDTO post = postsService.deletePost(postID, user);
            return new ResponseData<>(200, "post deleted", post);
        } catch (NumberFormatException e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        } catch (NullPointerException e) {
            logger.error(e.toString());
            return new ResponseData<>(404, "post not found");
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @PostMapping("/{id}/like")
    ResponseData<?> likePost(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @PathVariable String id
    ) {
        try {
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            int postID = Integer.parseInt(id);
            // check if current user liked this post
            Likes like = likesService.getPostLike(currentUser, postID);
            if (like != null) return new ResponseData<>(400, "already liked");
            // like
            PostDTO postDTO = postsService.likePost(currentUser, postID);
            if (postDTO == null) return new ResponseData<>(404, "post (or like) not found");
            return new ResponseData<>(200, "post liked", postDTO);
        } catch (NumberFormatException e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @DeleteMapping("/{id}/like")
    ResponseData<?> unlikePost(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @PathVariable String id
    ) {
        try {
            Users currentUser = userService.getUser(userDetails);
            if (currentUser == null) return ResponseData.userNotFoundResponse();
            int postID = Integer.parseInt(id);
            // check if current user liked the post
            Likes like = likesService.getPostLike(currentUser, postID);
            if (like == null) return new ResponseData<>(400, "never liked");
            // unlike
            PostDTO postDTO = postsService.unlikePost(currentUser, postID);
            if (postDTO == null) return new ResponseData<>(404, "post (or like) not found");
            return new ResponseData<>(200, "post unliked", postDTO);
        } catch (NumberFormatException e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }
}
