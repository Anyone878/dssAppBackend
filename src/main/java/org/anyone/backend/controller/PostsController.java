package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.Posts;
import org.anyone.backend.model.Users;
import org.anyone.backend.service.PostsService;
import org.anyone.backend.service.UserService;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.parameters.P;
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

    public PostsController(PostsService postsService, UserService userService) {
        this.postsService = postsService;
        this.userService = userService;
    }

    @GetMapping
    ResponseData<?> getAll(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails
    ) {
        Users user = userService.getUser(userDetails);
        if (user == null) return ResponseData.userNotFoundResponse();
        ArrayList<Posts> posts = postsService.getPostList();
        return new ResponseData<>(200, "posts found", posts);
    }

    @GetMapping(params = {"postID"})
    ResponseData<?> getByPostID(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "postID") String id
    ) {
        try {
            int postID = Integer.parseInt(id);
            Posts post = postsService.getPosts(postID);
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
            Users user = userService.getUser(userID);
            if (user == null) return new ResponseData<>(404, "user not found");
            return new ResponseData<>(200, "posts found", postsService.getPosts(user));
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
        Posts post = postsService.addPost(user, content);
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
            Posts post = postsService.deletePost(postID);
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
}
