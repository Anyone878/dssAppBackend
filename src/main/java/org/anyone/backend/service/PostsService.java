package org.anyone.backend.service;

import org.anyone.backend.dto.response.PostDTO;
import org.anyone.backend.model.Likes;
import org.anyone.backend.model.Posts;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.PostsRepository;
import org.anyone.backend.util.StringUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostsService {
    private final PostsRepository postsRepository;
    private final LikesService likesService;

    public PostsService(PostsRepository postsRepository, LikesService likesService) {
        this.postsRepository = postsRepository;
        this.likesService = likesService;
    }

    private Iterable<Posts> getPosts(Users user) {
        return postsRepository.findAllByUserOrderByPostDateTimeDesc(user);
    }

    private Iterable<Posts> getPosts() {
        return postsRepository.findAll(Sort.by("PostDateTime").descending());
    }

    private Posts getPost(int postID) {
        Optional<Posts> optionalPosts = postsRepository.findByPostID(postID);
        return optionalPosts.orElse(null);
    }

    public PostDTO getPostDTO(int postID, Users currentUser) {
        Posts post = getPost(postID);
        if (post == null) {
            return null;
        } else {
            return new PostDTO(post, likesService.isUserLikedPost(currentUser, post));
        }
    }

    public ArrayList<PostDTO> getPostDTOList(Users postUser, Users currentUser) {
        return convertToDTOList(getPosts(postUser), currentUser);
    }

    public ArrayList<PostDTO> getPostDTOList(Users currentUser) {
        return convertToDTOList(getPosts(), currentUser);
    }

    public PostDTO addPost(Users user, String content) {
        Posts post = new Posts();
        post.setUser(user);
        post.setPostContent(content);
        String ats = StringUtil.matchedArrayToString(StringUtil.matchAts(content));
        String tags = StringUtil.matchedArrayToString(StringUtil.matchTags(content));
        post.setAts(ats);
        post.setTags(tags);
        post.setPostDateTime(LocalDateTime.now());
        post.setLikes(0);
        post.setComments(0);
        return new PostDTO(postsRepository.saveAndFlush(post));
    }

    public PostDTO likePost(Users currentUser, int postID) {
        Posts post = getPost(postID);
        if (post == null) return null;
        Likes like = likesService.likePost(currentUser, post.getPostID());
        if (like == null) return null;
        post.setLikes(post.getLikes() + 1);
        Posts newPost = postsRepository.save(post);
        return new PostDTO(newPost, likesService.isUserLikedPost(currentUser, newPost));
    }

    public PostDTO unlikePost(Users currentUser, int postID) {
        Posts post = getPost(postID);
        if (post == null) return null;
        Likes like = likesService.getPostLike(currentUser, post.getPostID());
        if (like == null) return null;
        likesService.unlikePost(currentUser, post.getPostID());
        post.setLikes(post.getLikes() - 1);
        Posts newPost = postsRepository.save(post);
        return new PostDTO(newPost, likesService.isUserLikedPost(currentUser, newPost));
    }

    public PostDTO deletePost(int postID, Users currentUser) {
        Posts post = getPost(postID);
        if (post == null) return null;
        boolean isLikedByUser = likesService.isUserLikedPost(currentUser, post);
        postsRepository.delete(post);
        return new PostDTO(post, isLikedByUser);
    }

    public boolean belongTo(int postID, Users user) throws NullPointerException {
        Posts post = getPost(postID);
        if (post == null) throw new NullPointerException();
        return post.getUser() == user;
    }

    private ArrayList<PostDTO> convertToDTOList(Iterable<Posts> postsIterable, Users currentUser) {
        ArrayList<PostDTO> dtos = new ArrayList<>();
        postsIterable.forEach(post ->
                dtos.add(new PostDTO(post, likesService.isUserLikedPost(currentUser, post))));
        return dtos;
    }
}
